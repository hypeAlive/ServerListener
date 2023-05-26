package net.alive.serverlistener.mixin;

import net.alive.serverlistener.ServerListenerClient;
import net.alive.serverlistener.utils.MinecraftServerUtil;
import net.alive.serverlistener.utils.Modes;
import net.alive.serverlistener.utils.StringUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract boolean isEmpty();

    @Shadow
    public abstract void removeCustomName();

    @Inject(method = "getTooltip", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void getToolTip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> callbackInfoReturnable, List<Text> list) {
        if (!MinecraftServerUtil.onServer) return;
        if (!MinecraftServerUtil.inMode) return;

        InGameHud gameHud = MinecraftClient.getInstance().inGameHud;
        PlayerListHud playerListHud = gameHud.getPlayerListHud();
        try {
            for (Field field : playerListHud.getClass().getDeclaredFields()) {
                field.setAccessible(true); // You might want to set modifier to public first.
                Object value = field.get(playerListHud);
                if (value != null) {
                    if(value.toString().contains("Du befindest dich auf ")){
                        if(value.toString().contains(Modes.CITYBUILD.toString()))
                            list.addAll(addList(Modes.CITYBUILD));
                        else if(value.toString().contains(Modes.SKYBLOCK.toString()))
                            list.addAll(addList(Modes.SKYBLOCK));
                        else if(value.toString().contains(Modes.LOBBY.toString()))
                            list.addAll(addList(Modes.LOBBY));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private List<Text> addList(Modes mode){
        List<Text> list = new ArrayList<>();

        if(mode == Modes.NOTHING || mode == Modes.LOBBY) return list;

        list.add(MutableText.of(new LiteralTextContent(" ")));

        list.add(StringUtil.getColorizedString("--- ", Formatting.DARK_GRAY)
                .append(ServerListenerClient.MOD_TEXT)
                .append(StringUtil.getColorizedString(" ---", Formatting.DARK_GRAY)));
        list.add(StringUtil.getColorizedString("Preis: " + mode.toString() + mode.getCurrency(), Formatting.GRAY));

        list.add(MutableText.of(new LiteralTextContent(" ")));

        return list;
    }


}
