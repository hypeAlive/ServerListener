package net.alive.serverlistener.utils;

import net.alive.serverlistener.ServerListenerClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

public class MinecraftServerUtil {

    public static boolean onServer = false;

    public static Modes MODE = Modes.NOTHING;

    public static boolean inMode = true;

    public static void init() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.getCurrentServerEntry() != null && client.getCurrentServerEntry().address.equals("cytooxien.de")) {
                if(!onServer) {
                    if(client.player == null) return;
                    MutableText text = StringUtil.getColorizedString("", Formatting.GRAY)
                            .append(ServerListenerClient.MOD_TEXT)
                            .append(StringUtil.getColorizedString(" Mod wurde aktiviert!", Formatting.GRAY));
                    client.player.sendMessage(text);
                }
                onServer = true;
            } else {
                onServer = false;
            }



            ServerListenerClient.EXECUTOR_SERVICE.schedule(() -> {
                InGameHud gameHud = MinecraftClient.getInstance().inGameHud;
                PlayerListHud playerListHud = gameHud.getPlayerListHud();
                try {
                    for (Field field : playerListHud.getClass().getDeclaredFields()) {
                        field.setAccessible(true); // You might want to set modifier to public first.
                        Object value = field.get(playerListHud);
                        if (value != null) {
                            if(value.toString().contains("Du befindest dich auf ")){
                                if(value.toString().contains(Modes.CITYBUILD.toString()))
                                    MODE = Modes.CITYBUILD;
                                else if(value.toString().contains(Modes.SKYBLOCK.toString()))
                                    MODE = Modes.SKYBLOCK;
                                else if(value.toString().contains(Modes.LOBBY.toString()))
                                    MODE = Modes.LOBBY;
                                MinecraftClient.getInstance().player.sendMessage(StringUtil.getColorizedString(MODE.toString(), Formatting.GREEN));
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }, 0, TimeUnit.SECONDS);

        });
        ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
            onServer = false;
        }));
    }


}

