package net.alive.serverlistener;

import com.google.gson.Gson;
import com.mojang.authlib.minecraft.client.ObjectMapper;
import net.alive.serverlistener.utils.MinecraftServerUtil;
import net.alive.serverlistener.utils.Modes;
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

    private long timeStamp = -1;
    private String sellerName = null;
    private UUID sellerUuid = null;
    private String buyPrice = null;
    private String bidPrice = null;
    private String itemName = null;
    private int amount = -1;
    private Modes mode;

    private String comment = null;

    private String priceKey = null;

    private ItemStack stack = null;

    private List<String> toolTips = null;

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
        System.out.println("-.-" + timeStamp);

        this.itemName = stack.getItem().getTranslationKey();

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
        String id = this.itemName + "::" + this.buyPrice + "::" + TimeUtil.roundToTenMinutes(this.timeStamp) + "::" + this.sellerUuid;
        System.out.println(id);
        return id;
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

    @Override
    public String toString(){
        if(MinecraftServerUtil.MODE == Modes.NOTHING || MinecraftServerUtil.MODE == Modes.LOBBY)
            return null;


        String sb = "";
        sb += "{";
        sb += "\"mode\": \"" + MinecraftServerUtil.MODE.getTranslationKey() + "\", ";
        sb += "\"itemName\": \"" + this.itemName + "\", ";
        sb += "\"amount\": \"" + this.amount + "\", ";
        sb += "\"timestamp\": \"" + this.timeStamp + "\", ";
        sb += "\"sellerUUID\": \"" + this.sellerUuid + "\", ";
        sb += "\"buyPrice\": \"" + this.buyPrice + "\", ";
        sb += "\"bidPrice\": \"" + this.bidPrice + "\", ";
        sb += "\"priceKey\": \"" + this.priceKey + "\", ";
        sb += "\"comment\": " + StringUtil.convertToJsonObject(this.comment) + "";
        sb += "}";

        sb.replace("\\\"", "");
        System.out.println(sb);

        return sb;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getSellerName() {
        return sellerName;
    }

    public UUID getSellerUuid() {
        return sellerUuid;
    }

    public String getBuyPrice() {
        return buyPrice;
    }

    public String getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(String price){
        this.bidPrice = price;
    }

    public String getItemName() {
        return itemName;
    }

    public int getAmount() {
        return amount;
    }

    public String getComment() {
        return comment;
    }

    public List<String> getToolTips() {
        return toolTips;
    }
}
