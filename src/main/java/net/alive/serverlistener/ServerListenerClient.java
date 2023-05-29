package net.alive.serverlistener;

import net.alive.serverlistener.listener.AuctionInventoryListener;
import net.alive.serverlistener.listener.InventoryListener;
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
    public static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static MinecraftClient client;

    public static boolean CONNECTION_ESTABLISHED = false;

    public static MutableText MOD_TEXT = null;

    @Override
    public void onInitializeClient() {

        client = MinecraftClient.getInstance();

        CxnListener server = new CxnListener();

        MOD_TEXT = StringUtil.getColorizedString(Arrays.asList(
                StringUtil.TextComponent("[", Formatting.DARK_GRAY),
                StringUtil.TextComponent(CxnListener.MOD_NAME, Formatting.GOLD),
                StringUtil.TextComponent("]", Formatting.DARK_GRAY))
        );


        //MinecraftServerUtil.init(new String[] {});





        /**
         * Daten Auslesen aus Auktionshaus, /handel, Spielershops
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
