package net.alive.serverlistener.listener;

import net.alive.serverlistener.PriceCxnItemStack;
import net.alive.serverlistener.utils.StringUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class AuctionInventoryListener extends InventoryListener{

    private final List<PriceCxnItemStack> prices = new ArrayList<>();

    public AuctionInventoryListener(String[] inventoryTitles, int inventorySize) {
        super(inventoryTitles, inventorySize);
    }

    @Override
    public void onInventoryOpen(MinecraftClient client, ScreenHandler handler) {
        client.player.sendMessage(StringUtil.getColorizedString("Inventar " + this.inventoryTitles[0] + " geöffnet!", Formatting.RED));

        updatePrices(handler);

    }

    @Override
    public void onInventoryClose(MinecraftClient client, ScreenHandler handler) {
        client.player.sendMessage(StringUtil.getColorizedString("Inventar " + this.inventoryTitles[0] + " geschlossen!", Formatting.RED));

        processPrices(client);

    }

    @Override
    public void onInventoryUpdate(MinecraftClient client, ScreenHandler handler) {
        client.player.sendMessage(StringUtil.getColorizedString("Inventar " + this.inventoryTitles[0] + " updated!", Formatting.RED));
    }

    private void updatePrices(ScreenHandler handler){
        for(int i = 10; i < 35; i++){
            Slot slot = handler.getSlot(i);
            if(!slot.hasStack()) continue;
            prices.add(new PriceCxnItemStack(slot));
        }
    }

    private void processPrices(MinecraftClient client){
        if(client.player == null) return;

        for(PriceCxnItemStack item : prices){
            List<String> nbtTags = StringUtil.getNbtTags(item.getStack());

            if(nbtTags == null) continue;

            client.player.sendMessage(StringUtil.getColorizedString(item.getStack().getName().getString(), Formatting.RED));

            for(String tag : nbtTags){
                client.player.sendMessage(StringUtil.getColorizedString(tag, Formatting.BLUE));
                client.player.sendMessage(StringUtil.getColorizedString(" ", Formatting.AQUA));
            }
        }
    }

}
