package net.alive.serverlistener;

import net.alive.serverlistener.listener.AuctionInventoryListener;
import net.alive.serverlistener.listener.InventoryListener;
import net.alive.serverlistener.listener.TabListener;
import net.alive.serverlistener.utils.MinecraftServerUtil;
import net.alive.serverlistener.utils.StringUtil;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ServerListenerClient implements ClientModInitializer {
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static MinecraftClient client;

    public static final MutableText MOD_TEXT = StringUtil.getColorizedString(Arrays.asList(
            StringUtil.TextComponent("[", Formatting.DARK_GRAY),
            StringUtil.TextComponent("PreisCXN", Formatting.GOLD),
            StringUtil.TextComponent("]", Formatting.DARK_GRAY))
    );

    private boolean inAuctionHouse = false;

    private boolean onetime = true;

    private Slot firstSlot = null;

    @Override
    public void onInitializeClient() {

        client = MinecraftClient.getInstance();
        MinecraftServerUtil.init();


        InventoryListener auctionListener = new AuctionInventoryListener(new String[]{"Auktionshaus"}, 6*9);
        TabListener tabListener = new TabListener();


        /**
         * Daten Auslesen aus Auktionshaus, /handel, Spielershops
         */
            /*
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (!(client.currentScreen instanceof HandledScreen && isInventoryTitle(client, "Auktionshaus"))) return;

            ScreenHandler handler = client.player.currentScreenHandler;

            if(handler.getSlot(53).getStack().toString().equals("1 air")){
                client.player.sendMessage(StringUtil.getColorizedString("9-null-stack", Formatting.GREEN));
            }
        });
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (!(client.currentScreen instanceof GenericContainerScreen && isInventoryTitle(client, "Auktionshaus"))) return;

            ScreenHandler handler = client.player.currentScreenHandler;

            if(handler.getSlot(53).getStack().toString().equals("1 air")){
                client.player.sendMessage(StringUtil.getColorizedString("9-null-stack", Formatting.RED));
            }
        });

             */
    }

    private void onAuctionHouseEnter(ScreenHandler handler){
        MinecraftClient.getInstance().player.sendMessage(getText("Auktionshaus betreten", Formatting.DARK_RED));

        for(int i = 10; i < 31; i++){
            Slot slot = handler.getSlot(i);

            if(!slot.hasStack()) {
                continue;
            }
            ItemStack itemStack = slot.getStack();
            List<Text> tooltip = itemStack.getTooltip(MinecraftClient.getInstance().player, MinecraftClient.getInstance().options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.BASIC);

            for (Text line : tooltip){
                //MinecraftClient.getInstance().player.sendMessage(line);
            }
        }
    }

    private void onAuctionHouseLeave(){
        MinecraftClient.getInstance().player.sendMessage(getText("Auktionshaus verlassen", Formatting.DARK_RED));
    }

    private MutableText getText(String sting, Formatting formatting){
        return MutableText.of(new LiteralTextContent(sting)).setStyle(Style.EMPTY.withColor(formatting));
    }
}
