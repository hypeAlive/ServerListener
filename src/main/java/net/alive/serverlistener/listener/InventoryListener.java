package net.alive.serverlistener.listener;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public abstract class InventoryListener {

    private String inventoryTitle;
    private int inventorySize;

    public InventoryListener(String inventoryTitle, int inventorySize){

        this.inventorySize = inventorySize;
        this.inventoryTitle = inventoryTitle;

        init();
    }

    private void init(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {

        });
    }

    public abstract void onInventoryOpen();

    public abstract void onInventoryClose();

    public abstract void onInventoryUpdate();


}
