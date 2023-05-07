package net.alive.serverlistener.utils;

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
                onServer = true;
            }
        });
    }


}
