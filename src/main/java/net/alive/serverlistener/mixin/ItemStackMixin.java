package net.alive.serverlistener.mixin;

import net.alive.serverlistener.CxnListener;
import net.alive.serverlistener.ItemData;
import net.alive.serverlistener.PriceCxnItemStack;
import net.alive.serverlistener.ServerListenerClient;
import net.alive.serverlistener.utils.ApiInteractionUtil;
import net.alive.serverlistener.utils.MinecraftServerUtil;
import net.alive.serverlistener.utils.Modes;
import net.alive.serverlistener.utils.StringUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract boolean isEmpty();

    @Shadow
    public abstract void removeCustomName();

    @Shadow public abstract Item getItem();

    @Shadow private int count;

    @Inject(method = "getTooltip", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void getToolTip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> callbackInfoReturnable, List<Text> list) {

        MinecraftServerUtil.refreshTabSearch();

        if (!MinecraftServerUtil.onServer) return;
        if (!MinecraftServerUtil.inMode) return;

        ItemStack stack = (ItemStack)(Object)this;

        List<String> nbt = StringUtil.getNbtTags(stack);

        List<String> oldToolTips = new ArrayList<>();

        for (Text text : list) {
            oldToolTips.add(text.getString());
        }

        if(nbt != null && CxnListener.getTranslations("cxprice.translation.is_island") != null) {
            for (String isIsland : CxnListener.getTranslations("cxprice.translation.is_island")) {
                if (nbt.toString().contains(isIsland)){
                    return;
                }
            }
        }

        if(nbt != null)
            System.out.println(nbt.toString().toLowerCase());


        ItemData itemData = ItemData.getItemData(CxnListener.ITEM_DATA, nbt, stack.getItem().getTranslationKey());

        if (itemData == null) return;

        if(ServerListenerClient.DEBUG_MODE) {
            System.out.println(nbt);
            System.out.println(itemData);
        }

        int count = stack.getCount();

        if(itemData.getAdditionalSearch() != null) {

            String additionalSearch = itemData.getAdditionalSearch();

            //System.out.println(additionalSearch);

            String found = StringUtil.extractStringFromWildcard(nbt.toString().toLowerCase(), additionalSearch);

            //System.out.println(found);

            if(found == null) return;

            int add = StringUtil.safeParseInt(found);

            if(add == 0) return;

            count *= add;

            //System.out.println(count);

        }

        if(CxnListener.ACTIVATE)
            list.addAll(addList(MinecraftServerUtil.MODE, itemData.getMaxPrice(), itemData.getMinPrice(), count));

    }

    private @NotNull List<Text> addList(Modes mode, Double maxPrice, Double minPrice, int amount){
        List<Text> list = new ArrayList<>();

        if(mode == Modes.NOTHING || mode == Modes.LOBBY) return list;
        if(maxPrice == null || minPrice == null) return list;

        list.add(MutableText.of(new LiteralTextContent(" ")));

        list.add(StringUtil.getColorizedString("--- ", Formatting.DARK_GRAY)
                .append(ServerListenerClient.MOD_TEXT)
                .append(StringUtil.getColorizedString(" ---", Formatting.DARK_GRAY)));
        list.add(StringUtil.getColorizedString("Preis: " + StringUtil.convertPrice(minPrice * amount) + " - " + StringUtil.convertPrice(maxPrice * amount) + mode.getCurrency(), Formatting.GRAY));

        list.add(MutableText.of(new LiteralTextContent(" ")));

        return list;
    }


}
