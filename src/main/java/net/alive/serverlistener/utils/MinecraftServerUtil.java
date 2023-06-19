package net.alive.serverlistener.utils;

import net.alive.serverlistener.CxnListener;
import net.alive.serverlistener.ServerListenerClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import com.mojang.datafixers.util.Pair;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MinecraftServerUtil {

    public static boolean onServer = false;

    public static Modes MODE = Modes.NOTHING;

    public static boolean inMode = true;

    private static boolean running = false;

    private static String[] ips = {"cytooxien.de", "cytooxien.net"};
    public static String[] tabSearch = {""};

    private static List<Pair<Modes, String[]>> modeList = new ArrayList<>();

    public static void init(String[] ips, String[] tabSearch) {

        refreshData(ips, tabSearch);

        if (!running)
            init();
    }

    public static void init() {
        running = true;

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {

            if (!CxnListener.CONNECTED_TO_SERVER) {
                sendModMessage(client, Formatting.RED, "nicht mit Server verbunden");
                if (CxnListener.checkConnectionToServer()) {
                    CxnListener.connectToServer(true);
                    init(CxnListener.getSettingsAsArray("pricecxn.settings.client.server_ip"),
                            CxnListener.getTranslationsAsArray("cxnprice.translation.mode.search"));
                    for (String ip : ips)
                        if (client.getCurrentServerEntry() != null && client.getCurrentServerEntry().address.equals(ip)) {
                            sendModMessage(client, Formatting.GREEN, "Verbindung zum Server herstellen...");
                            sendModConnectionInformation(client);
                        }
                }
            }

            if (client.getCurrentServerEntry() == null) return;

            boolean joinServer = false;

            System.out.println(Arrays.toString(ips));

            for (String ip : ips) {
                if ((client.getCurrentServerEntry().address.toLowerCase().contains("cytooxien") && !client.getCurrentServerEntry().address.toLowerCase().contains("beta"))
                    || client.getCurrentServerEntry().address.toLowerCase().equals(ip))
                    joinServer = true;
            }

            if (!joinServer) {
                onServer = false;
                return;
            }

            if (!onServer) {
                if (client.player == null) return;

                if (!CxnListener.CONNECTED_TO_SERVER) {
                    MinecraftServerUtil.sendModMessage(client, Formatting.RED, "Es konnte keine Verbindung zu den " + CxnListener.MOD_NAME + " Servern aufgebaut werden.");
                    sendModMessage(client, Formatting.RED, "Mod konnte nicht aktiviert werden.");
                } else {
                    sendModConnectionInformation(client);
                }
                onServer = true;
            }


            if (CxnListener.checkConnectionToServer() && (MODE == Modes.SKYBLOCK || MODE == Modes.CITYBUILD))
                CxnListener.connectToServer(false);

            ServerListenerClient.EXECUTOR_SERVICE.schedule(() -> {
                MinecraftServerUtil.refreshTabSearch(tabSearch);
            }, 1, TimeUnit.SECONDS);
        });

        initDisconnect();
    }

    private static void refreshData(String[] ips, String[] tabSearch) {
        if (ips != null && tabSearch != null) {
            MinecraftServerUtil.ips = ips;
            MinecraftServerUtil.tabSearch = tabSearch;
        }
    }

    private static void initDisconnect() {
        ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
            onServer = false;
        }));
    }

    public static void refreshTabSearch(String[] searches) {
        InGameHud gameHud = MinecraftClient.getInstance().inGameHud;
        PlayerListHud playerListHud = gameHud.getPlayerListHud();
        Comparator<PlayerListEntry> playerList = null;

        boolean onTab = false;

        try {
            outerloop:
            for (Field field : playerListHud.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(playerListHud);

                if (value != null) {

                    for (String search : searches) {

                        if (value.toString().contains(search)) {
                            onTab = true;
                        }

                    }

                    if (!onTab) continue;

                    //REALLIFE HINZUFÜGEN
                    for (Pair<Modes, String[]> mode : modeList) {
                        if (MinecraftServerUtil.setMode(mode.getFirst(), mode.getSecond(), value.toString()))
                            break outerloop;
                    }

                    MinecraftServerUtil.setMode(Modes.NOTHING);

                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (MinecraftClient.getInstance().player != null && ServerListenerClient.DEBUG_MODE)
            MinecraftClient.getInstance().player.sendMessage(StringUtil.getColorizedString("JOINED NEW MODE: " + MODE.toString(), Formatting.RED));
    }

    public static void refreshTabSearch() {
        refreshTabSearch(tabSearch);
    }

    public static void addMode(Modes mode, String[] search) {
        modeList.add(Pair.of(mode, search));
    }

    public static void cleanModes() {
        modeList = new ArrayList<>();
    }

    private static boolean setMode(Modes mode, String[] strings, String value) {
        if(strings == null)
            return false;
        for (String string : strings) {
            if (value.contains(string)) {
                MODE = mode;
                if (ServerListenerClient.DEBUG_MODE)
                    System.out.println(MODE);
                return true;
            }
        }
        return false;
    }

    public static void setMode(Modes mode) {
        MODE = mode;
    }

    public static void sendModMessage(MinecraftClient client, Formatting formatting, String message) {
        if (client.player == null) return;
        ServerListenerClient.EXECUTOR_SERVICE.schedule(() -> {
            MutableText text1 = StringUtil.getColorizedString("", Formatting.GRAY)
                    .append(ServerListenerClient.MOD_TEXT)
                    .append(StringUtil.getColorizedString(" " + message, formatting));
            client.player.sendMessage(text1);
        }, 2, TimeUnit.SECONDS);
    }

    private static void sendModConnectionInformation(MinecraftClient client) {
        if (CxnListener.CONNECTED_TO_SERVER) {
            if (CxnListener.ACTIVATE) {
                if (CxnListener.MAINTENANCE) {
                    MinecraftServerUtil.sendModMessage(client, Formatting.RED, "Der " + CxnListener.MOD_NAME + " Server befindet sich im Wartungsmodus.");
                    if (MinecraftClient.getInstance().player != null && CxnListener.getTrustLevel(MinecraftClient.getInstance().player.getUuid()) == 9999999)
                        MinecraftServerUtil.sendModMessage(client, Formatting.GREEN, "Durch deinen Rang wurde dir der Zugriff dennoch genehmigt.");
                    else {
                        MinecraftServerUtil.sendModMessage(client, Formatting.RED, "Versuche es später erneut... (Minecraft neustarten)");
                        CxnListener.ACTIVATE = false;
                    }
                } else
                    sendModMessage(client, Formatting.GREEN, "Mod wurde aktiviert!");
            } else{
                if(CxnListener.MIN_VERSION == null)
                    MinecraftServerUtil.sendModMessage(client, Formatting.RED, "Es konnte keine Verbindung zum " + CxnListener.MOD_NAME +  " Server aufgebaut werden... (server offline)");
                else
                    MinecraftServerUtil.sendModMessage(client, Formatting.RED, "Deine Mod ist veraltet! Bitte update auf mindestens Version " + CxnListener.MIN_VERSION + ".");
            }
        } else {
            MinecraftServerUtil.sendModMessage(client, Formatting.RED, "Es konnte keine Verbindung zu den " + CxnListener.MOD_NAME + " Servern aufgebaut werden.");
        }

    }
}

