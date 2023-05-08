package net.alive.serverlistener.mixin;

import net.alive.serverlistener.ServerListenerClient;
import net.alive.serverlistener.utils.MinecraftServerUtil;
import net.alive.serverlistener.utils.StringUtil;
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

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract boolean isEmpty();

    @Shadow
    public abstract void removeCustomName();

    @Inject(method = "getTooltip", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void getToolTip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> callbackInfoReturnable, List<Text> list) {
        if(!MinecraftServerUtil.onServer) return;
        if(!MinecraftServerUtil.inMode) return;

        list.add(MutableText.of(new LiteralTextContent(" ")));

        list.add(StringUtil.getColorizedString("--- ", Formatting.DARK_GRAY)
                .append(ServerListenerClient.MOD_TEXT)
                .append(StringUtil.getColorizedString(" ---", Formatting.DARK_GRAY)));
        list.add(StringUtil.getColorizedString("Preis: 1341 Taler", Formatting.GRAY));
    }


}
