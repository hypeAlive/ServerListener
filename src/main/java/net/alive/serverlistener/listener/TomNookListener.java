package net.alive.serverlistener.listener;

import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.ScreenHandler;

public class TomNookListener extends InventoryListener {
    public TomNookListener(String[] inventoryTitles, int inventorySize) {
        super(inventoryTitles == null ? new String[] { "Shop" } : inventoryTitles, inventorySize);
    }

    @Override
    public void onInventoryOpen(MinecraftClient client, ScreenHandler handler) {

    }

    @Override
    public void onInventoryClose(MinecraftClient client, ScreenHandler handler) {

    }

    @Override
    public void onInventoryUpdate(MinecraftClient client, ScreenHandler handler) {

    }
}
