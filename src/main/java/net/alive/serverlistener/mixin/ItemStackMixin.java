package net.alive.serverlistener.mixin;

import net.alive.serverlistener.utils.ServerUtil;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
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

    @Shadow public abstract void removeCustomName();

    @Inject(method = "getTooltip", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void getToolTip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> callbackInfoReturnable, List<Text> list) {
        if(!ServerUtil.onServer) return;
        if(!ServerUtil.inMode) return;

        list.add(MutableText.of(new LiteralTextContent("testTest")).setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
    }


}
