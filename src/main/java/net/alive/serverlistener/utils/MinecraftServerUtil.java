package net.alive.serverlistener.utils;

import net.alive.serverlistener.CxnListener;
import net.alive.serverlistener.ServerListenerClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import com.mojang.datafixers.util.Pair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MinecraftServerUtil {

    public static boolean onServer = false;

    public static Modes MODE = Modes.NOTHING;

    public static boolean inMode = true;

    private static boolean running = false;

    private static String[] ips = {"cytooxien.de", "cytooxien.net"};
    private static String[] tabSearch = {""};

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
                if (CxnListener.checkConnectionToServer()) {
                    CxnListener.connectToServer();
                    init(CxnListener.getSettingsAsArray("pricecxn.settings.client.server_ip"),
                            CxnListener.getTranslationsAsArray("cxnprice.translation.mode.search"));
                    for (String ip : ips)
                        if (client.getCurrentServerEntry() != null && client.getCurrentServerEntry().address.equals(ip)){
                            sendModMessage(client, Formatting.GREEN, "Verbindung zum Server hergestellt...");
                            sendModMessage(client, Formatting.GREEN, "Mod wurde aktiviert!");
                        }
                }
            }

            if (client.getCurrentServerEntry() == null) return;

            boolean joinServer = false;

            for (String ip : ips)
                if (client.getCurrentServerEntry().address.equals(ip))
                    joinServer = true;

            if (!joinServer) {
                onServer = false;
                return;
            }

            if (!onServer) {
                if (client.player == null) return;

                if (!CxnListener.CONNECTED_TO_SERVER) {
                    sendModMessage(client, Formatting.RED, "Konnte keine Verbindung zum Server herstellen. Versuche es später erneut!");
                    sendModMessage(client, Formatting.RED, "Mod konnte nicht aktiviert werden.");
                } else {
                    sendModMessage(client, Formatting.GREEN, "Mod wurde aktiviert.");
                }
                onServer = true;
            }
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
        MinecraftClient.getInstance().player.sendMessage(StringUtil.getColorizedString("JOINED NEW MODE: " + MODE.toString(), Formatting.RED));
    }

    public static void addMode(Modes mode, String[] search) {
        modeList.add(Pair.of(mode, search));
    }

    public static void cleanModes() {
        modeList = new ArrayList<>();
    }

    private static boolean setMode(Modes mode, String[] strings, String value) {
        for (String string : strings) {
            if (value.contains(string)) {
                MODE = mode;
                System.out.println(MODE);
                return true;
            }
        }
        return false;
    }

    public static void setMode(Modes mode) {
        MODE = mode;
    }

    private static void sendModMessage(MinecraftClient client, Formatting formatting, String message){
        if(client.player == null) return;
        ServerListenerClient.EXECUTOR_SERVICE.schedule(() -> {
            MutableText text1 = StringUtil.getColorizedString("", Formatting.GRAY)
                    .append(ServerListenerClient.MOD_TEXT)
                    .append(StringUtil.getColorizedString(" " + message, formatting));
            client.player.sendMessage(text1);
        }, 2, TimeUnit.SECONDS);
    }

}

