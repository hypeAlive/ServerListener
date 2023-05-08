package net.alive.serverlistener;

import com.mojang.datafixers.util.Pair;
import net.alive.serverlistener.listener.AuctionListener;
import net.alive.serverlistener.listener.InventoryListener;
import net.alive.serverlistener.utils.ServerUtil;
import net.alive.serverlistener.utils.StringUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.Input;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.*;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        ServerUtil.init();


        InventoryListener auctionListener = new AuctionListener(new String[]{"Auktionshaus"}, 6*9);


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
