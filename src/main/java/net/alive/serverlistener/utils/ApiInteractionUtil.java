package net.alive.serverlistener.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;

import java.util.List;

public class ApiInteractionUtil {

    public static void testData(MinecraftClient client, List<String> data){
        if(client.player == null) return;

        for (String line : data){
            client.player.sendMessage(StringUtil.getColorizedString(line, Formatting.GRAY));
        }
    }

}
