package net.alive.serverlistener;

import net.alive.serverlistener.listener.AuctionInventoryListener;
import net.alive.serverlistener.listener.InventoryListener;
import net.alive.serverlistener.utils.MinecraftServerUtil;
import net.alive.serverlistener.utils.StringUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ServerListenerClient implements ClientModInitializer {
    public static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static MinecraftClient client;

    public static boolean DEBUG_MODE = false;

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
    }
}
