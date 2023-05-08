package net.alive.serverlistener.utils;

import net.alive.serverlistener.ServerListenerClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.alive.serverlistener.utils.StringUtil.TextComponent;

public class ServerUtil {

    public static boolean onServer = false;

    public static boolean inMode = true;

    public static void init() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.getCurrentServerEntry() != null && client.getCurrentServerEntry().address.equals("cytooxien.de")) {
                if(!onServer) {
                    if(client.player == null) return;
                    MutableText text = StringUtil.getColorizedString("", Formatting.GRAY)
                            .append(ServerListenerClient.MOD_TEXT)
                            .append(StringUtil.getColorizedString(" Mod aktiviert!", Formatting.GRAY));
                    client.player.sendMessage(text);
                }
                onServer = true;
            } else {
                onServer = false;
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
            onServer = false;
        }));
    }


}
