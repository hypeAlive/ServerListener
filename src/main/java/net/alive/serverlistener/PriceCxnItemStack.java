package net.alive.serverlistener;

import com.google.gson.Gson;
import net.alive.serverlistener.utils.StringUtil;
import net.alive.serverlistener.utils.TimeUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class PriceCxnItemStack {

    private static final String TIMESTAMP_SEARCH = "Ende: ";
    private static final String SELLER_SEARCH = "Verkäufer: ";
    private static final String BID_SEARCH = "Gebotsbetrag: ";
    private static final String BUY_SEARCH = "Sofortkauf: ";

    private long timeStamp;
    private String sellerName;
    private UUID sellerUuid;
    private String buyPrice;
    private String bidPrice;
    private String itemName;
    private int amount;

    private String comment;

    private String priceKey;

    private ItemStack stack;

    private List<String> toolTips;

    public PriceCxnItemStack(ItemStack stack){
        if(stack == null) return;
        this.stack = stack;

        this.toolTips = StringUtil.getToolTips(stack);

        this.amount = stack.getCount();

        this.sellerName = StringUtil.getFirstSuffixStartingWith(toolTips, SELLER_SEARCH);
        this.sellerUuid = this.sellerName == null ? null : StringUtil.getPlayerUUID(this.sellerName);

        this.bidPrice = StringUtil.getFirstSuffixStartingWith(toolTips, BID_SEARCH);

        this.buyPrice = StringUtil.getFirstSuffixStartingWith(toolTips, BUY_SEARCH);

        String timestamp = StringUtil.getFirstSuffixStartingWith(toolTips, TIMESTAMP_SEARCH);
        this.timeStamp = timestamp == null ? -1 : TimeUtil.getStartTimeStamp(timestamp);

        this.itemName = stack.getItem().getName().getString();

        this.priceKey = createID();

        this.comment = Objects.requireNonNull(StringUtil.getNbtTags(stack)).toString();
    }

    public PriceCxnItemStack(Slot slot){
        this(slot.hasStack() ? slot.getStack() : null);
    }

    public ItemStack getStack() {
        return stack;
    }

    /*
    private String getName(ItemStack item) {

        boolean specialItem = false;

        List<String> data = StringUtil.getNbtTags(item);

        for (String line : data){
            //testen ob SpecialItem
            if(line.contains("PublicBukkitValues")){
                specialItem = true;
                String name2 = StringUtil.extractStringFromWildcard(line, "{\"treasurechestitems:*\":{},\"");

                if(name2 != null)
                    return "treasurechestitems." + name2.toLowerCase();

                String type = StringUtil.extractStringFromWildcard(line, "\"treasurechestitems:skyblockx.*\":\"");

                if(type == null)
                    type = StringUtil.extractStringFromWildcard(line, "\"treasurechestitems:*\":\"");

                String name = StringUtil.extractStringFromWildcard(line, "\":\"*\",\"");
                if(name == null)
                    name = StringUtil.extractStringFromWildcard(line, "\":\"*\"}");

                if(type == null || name == null)
                    return null;

                return "treasurechestitems." + type.toLowerCase() + "." + name.toLowerCase();
            }
        }

        for(String line : data){
            if(line.contains("display:")) {
                //testen ob Name umbenannt
                if (line.contains("Name:")){
                    if(item.getName().toString().contains("empty[style")){
                        String literal = StringUtil.extractStringFromWildcard(item.getName().toString(), "literal{*}");
                        if(literal != null)
                            return "treasurechestitems." + literal.toLowerCase();
                    } else {
                        String name = StringUtil.extractStringFromWildcard(line, "Name:'{\"italic\":false,\"color\":\"gold\",\"text\":\"*\"");

                        return name == null ? null : "treasurechestitems." + name.toLowerCase();

                    }

                    return null;
                } else {
                    String translationKey = StringUtil.extractStringFromWildcard(item.getName().toString(), "translation{key='*'");
                    if(translationKey != null)
                        return translationKey;
                }
            }
        }

        return null;
    }

     */

    private String createID(){
        return this.itemName + "::" + this.bidPrice + this.buyPrice + "::" + this.timeStamp + "::" + this.sellerUuid;
    }

    public void printDisplay(MinecraftClient client){
        if(client.player == null) return;

        client.player.sendMessage(StringUtil.getColorizedString("", Formatting.DARK_GRAY));
        client.player.sendMessage(StringUtil.getColorizedString("ItemName: " + this.itemName + " (" + this.amount + ")", Formatting.GOLD));
        client.player.sendMessage(StringUtil.getColorizedString("itemKey: " + this.priceKey, Formatting.GRAY));
        /*
        client.player.sendMessage(StringUtil.getColorizedString("Seller: " + this.sellerName + " (" + this.sellerUuid + ")", Formatting.GRAY));
        client.player.sendMessage(StringUtil.getColorizedString("BuyPrice: " + this.buyPrice, Formatting.GRAY));
        client.player.sendMessage(StringUtil.getColorizedString("BidPrice: " + this.bidPrice, Formatting.GRAY));
        client.player.sendMessage(StringUtil.getColorizedString("Timetamp: " + this.timeStamp, Formatting.GRAY));
         */

    }

    public String getPriceKey() {
        return priceKey;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
