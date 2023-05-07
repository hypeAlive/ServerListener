package net.alive.serverlistener.listener;

import net.alive.serverlistener.utils.StringUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class InventoryListener {

    protected String[] inventoryTitles;
    private int inventorySize;

    private List<Slot> slots = new ArrayList<>();

    private boolean open = false;

    public InventoryListener(String[] inventoryTitles, int inventorySize){

        this.inventorySize = inventorySize;
        this.inventoryTitles = inventoryTitles;

        init();
    }

    private void init(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if(!this.open && client.currentScreen instanceof HandledScreen && isInventoryTitle(client, inventoryTitles)){
                onInventoryOpen(client);
                initSlots(client);
                this.open = true;
            }

            if(hadItemsChange(client))
                onInventoryUpdate(client);

            if(this.open && !(client.currentScreen instanceof HandledScreen)) {

                onInventoryClose(client);
                this.open = false;
            }


        });
    }

    public abstract void onInventoryOpen(MinecraftClient client);

    public abstract void onInventoryClose(MinecraftClient client);

    public abstract void onInventoryUpdate(MinecraftClient client);

    private boolean isInventoryTitle(MinecraftClient client, String[] inventoryTitles){
        if(client.currentScreen == null) return false;

        for(String title : inventoryTitles){
            if(client.currentScreen.getTitle().getString().equals(title))
                return true;
        }

        return false;
    }

    private boolean hadItemsChange(MinecraftClient client){

        if(!isInventoryTitle(client, inventoryTitles)) return false;

        ScreenHandler handler = client.player.currentScreenHandler;

        if(handler == null) return false;
        if(this.slots == null) return false;

        for (int i = 0; i < this.inventorySize; i++){
            if(!Objects.equals(slots.get(i).getStack().getName().toString(), handler.getSlot(i).getStack().getName().toString())){
                initSlots(client);
                return true;
            }
        }

        return false;
    }

    private void initSlots(MinecraftClient client){
        ScreenHandler handler = client.player.currentScreenHandler;

        if(handler == null) return;

        slots = new ArrayList<>();

        for (int i = 0; i < this.inventorySize; i++){
            slots.add(handler.getSlot(i));
        }
    }


}
