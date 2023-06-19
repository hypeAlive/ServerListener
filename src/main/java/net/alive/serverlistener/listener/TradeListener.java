package net.alive.serverlistener.listener;

import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.ScreenHandler;

public class TradeListener extends InventoryListener {

    public TradeListener(String[] inventoryTitles, int inventorySize) {
        super(inventoryTitles == null ? new String[] { "Handel" } : inventoryTitles, inventorySize);
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
