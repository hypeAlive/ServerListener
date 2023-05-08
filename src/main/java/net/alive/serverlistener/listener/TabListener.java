package net.alive.serverlistener.listener;

import net.alive.serverlistener.utils.StringUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.lang.reflect.Field;

public class TabListener {

    public TabListener(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            InGameHud gameHud = MinecraftClient.getInstance().inGameHud;
            PlayerListHud playerListHud = gameHud.getPlayerListHud();

            try {
                Field headerField = PlayerListHud.class.getDeclaredField("header");
                headerField.setAccessible(true);
                String tabHeader = ((Text)headerField.get(playerListHud)).toString();
                System.out.println("Kopfzeilentext der Tabliste: " + tabHeader);

                if(MinecraftClient.getInstance().player == null) return;
                MinecraftClient.getInstance().player.sendMessage(StringUtil.getColorizedString(tabHeader, Formatting.RED));

            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

    }

}
