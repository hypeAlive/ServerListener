package net.alive.serverlistener.listener;

import com.google.gson.Gson;
import com.mojang.authlib.minecraft.client.ObjectMapper;
import net.alive.serverlistener.PriceCxnItemStack;
import net.alive.serverlistener.ServerListenerClient;
import net.alive.serverlistener.utils.ApiInteractionUtil;
import net.alive.serverlistener.utils.MinecraftServerUtil;
import net.alive.serverlistener.utils.StringUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AuctionInventoryListener extends InventoryListener{

    private List<PriceCxnItemStack> prices = new ArrayList<>();

    public AuctionInventoryListener(String[] inventoryTitles, int inventorySize) {
        super(inventoryTitles, inventorySize);
    }

    @Override
    public void onInventoryOpen(MinecraftClient client, ScreenHandler handler) {
        client.player.sendMessage(StringUtil.getColorizedString("Inventar " + this.inventoryTitles[0] + " geöffnet!", Formatting.GRAY));

        prices = new ArrayList<>();

        updatePrices(handler);

    }

    @Override
    public void onInventoryClose(MinecraftClient client, ScreenHandler handler) {
        MinecraftServerUtil.refreshMode();
        client.player.sendMessage(StringUtil.getColorizedString("Inventar " + this.inventoryTitles[0] + " geschlossen!", Formatting.GRAY));

        processPrices(client);

    }

    @Override
    public void onInventoryUpdate(MinecraftClient client, ScreenHandler handler) {
        for(int i = 0; i < 7; i++) {
            ServerListenerClient.EXECUTOR_SERVICE.schedule(() -> {
                if(!this.isOpen) return;
                updatePrices(handler);
            }, 300 + 100 * i, TimeUnit.MILLISECONDS);
        }
    }

    private void updatePrices(ScreenHandler handler){
        int size = prices.size();
        for(int i = 10; i < 35; i++){
            Slot slot = handler.getSlot(i);
            if(slot.getStack() == null) continue;
            if(!slot.hasStack()) continue;

            //MinecraftClient.getInstance().player.sendMessage(StringUtil.getColorizedString(String.valueOf(i), Formatting.RED));

            boolean add = true;

            PriceCxnItemStack item = new PriceCxnItemStack(slot);
            for(PriceCxnItemStack items : prices){
                if(items.getPriceKey().equals(item.getPriceKey())){
                    if(!Objects.equals(items.getBidPrice(), item.getBidPrice()))
                        items.setBidPrice(item.getBidPrice());
                    //MinecraftClient.getInstance().player.sendMessage(StringUtil.getColorizedString("- " + prices.size(), Formatting.GREEN));
                    add = false;
                }
            }

            if(!add)
                continue;

            prices.add(new PriceCxnItemStack(slot));
        }
        MinecraftClient.getInstance().player.sendMessage(StringUtil.getColorizedString("Added " + (prices.size() - size) + " Items to List (" + prices.size()  +")", Formatting.GRAY));
    }

    private void processPrices(MinecraftClient client){
        if(client.player == null) return;

        List<String> data = new ArrayList<>();

        MinecraftClient.getInstance().player.sendMessage(StringUtil.getColorizedString(String.valueOf("Send " + this.prices.size() + " Items to Server"), Formatting.GREEN));

        for(PriceCxnItemStack item : prices){
            List<String> nbtTags = StringUtil.getNbtTags(item.getStack());

            if(nbtTags == null) continue;
            if(item.toString() == null) continue;

            data.add(item.toString());
        }

        ApiInteractionUtil.sendData(data);
    }

}
