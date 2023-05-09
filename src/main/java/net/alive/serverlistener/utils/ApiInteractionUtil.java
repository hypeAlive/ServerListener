package net.alive.serverlistener.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;

import java.util.List;

public class ApiInteractionUtil {

    public static void testData(MinecraftClient client, List<String> data, ItemStack item){
        if(client.player == null) return;
        
        boolean specialItem = false;

        client.player.sendMessage(StringUtil.getColorizedString(item.getName().toString(),Formatting.GOLD));

        for (String line : data){

            if(line.contains("PublicBukkitValues:")){
                specialItem = true;
            }

            client.player.sendMessage(StringUtil.getColorizedString(line, specialItem ? Formatting.GREEN : Formatting.RED));
            client.player.sendMessage(StringUtil.getColorizedString("", Formatting.GRAY));
        }
    }

}
