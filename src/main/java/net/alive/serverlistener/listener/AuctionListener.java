package net.alive.serverlistener.listener;

import net.alive.serverlistener.utils.StringUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Formatting;

public class AuctionListener extends InventoryListener{

    public AuctionListener(String[] inventoryTitles, int inventorySize) {
        super(inventoryTitles, inventorySize);
    }

    @Override
    public void onInventoryOpen(MinecraftClient client) {
        client.player.sendMessage(StringUtil.getColorizedString("Inventar " + this.inventoryTitles[0] + " geöffnet!", Formatting.RED));
    }

    @Override
    public void onInventoryClose(MinecraftClient client) {
        client.player.sendMessage(StringUtil.getColorizedString("Inventar " + this.inventoryTitles[0] + " geschlossen!", Formatting.RED));
    }

    @Override
    public void onInventoryUpdate(MinecraftClient client) {
        client.player.sendMessage(StringUtil.getColorizedString("Inventar " + this.inventoryTitles[0] + " updated!", Formatting.RED));
    }



}
