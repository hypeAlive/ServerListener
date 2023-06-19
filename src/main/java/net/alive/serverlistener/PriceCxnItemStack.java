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

import java.util.*;


public class PriceCxnItemStack {

    private static String[] TIMESTAMP_SEARCH = { "Ende: " };
    private static String[] SELLER_SEARCH = { "Verkäufer: " };
    private static String[] BID_SEARCH = { "Gebotsbetrag: " };
    private static String[] BUY_SEARCH = { "Sofortkauf: "};

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

        refreshSearchData();

        this.stack = stack;
        this.toolTips = StringUtil.getToolTips(stack);
        this.amount = stack.getCount();

        this.sellerName = StringUtil.getFirstSuffixStartingWith(toolTips, SELLER_SEARCH);

        this.sellerUuid = this.sellerName == null ? null : StringUtil.getPlayerUUID(this.sellerName);

        this.bidPrice = StringUtil.getFirstSuffixStartingWith(toolTips, BID_SEARCH);
        if(this.bidPrice != null)
            this.bidPrice = this.bidPrice.substring(0, this.bidPrice.length() - 1);

        if(ServerListenerClient.DEBUG_MODE) {
            System.out.println(Arrays.toString(BID_SEARCH));
            System.out.println(bidPrice);
        }

        this.buyPrice = StringUtil.getFirstSuffixStartingWith(toolTips, BUY_SEARCH);
        if(buyPrice != null)
            this.buyPrice = this.buyPrice.substring(0, this.buyPrice.length() - 1);

        if(ServerListenerClient.DEBUG_MODE) {
            System.out.println(Arrays.toString(BUY_SEARCH));
            System.out.println(buyPrice);
        }

        String timestamp = StringUtil.getFirstSuffixStartingWith(toolTips, TIMESTAMP_SEARCH);
        this.timeStamp = timestamp == null ? -1 : TimeUtil.getStartTimeStamp(timestamp);


        this.itemName = stack.getItem().getTranslationKey();
        this.priceKey = createID();
        this.comment = Objects.requireNonNull(StringUtil.getNbtTags(stack)).toString();
    }

    public PriceCxnItemStack(ItemStack stack, List<String> toolTips, List<String> nbtTags){
        if(stack == null) return;
        refreshSearchData();

        this.stack = stack;
        this.toolTips = toolTips;
        this.amount = stack.getCount();

        this.comment = nbtTags == null ? null : nbtTags.toString();
        this.itemName = stack.getItem().getTranslationKey();
        this.timeStamp = 0;

    }

    public PriceCxnItemStack(Slot slot){
        this(slot.hasStack() ? slot.getStack() : null);
    }

    public ItemStack getStack() {
        return stack;
    }

    private String createID(){
        String id = this.itemName + "::" + this.buyPrice + "::" + this.timeStamp + "::" + this.sellerUuid;
        //System.out.println(id);
        return id;
    }

    public void printDisplay(MinecraftClient client){
        if(client.player == null) return;

        client.player.sendMessage(StringUtil.getColorizedString("", Formatting.DARK_GRAY));
        client.player.sendMessage(StringUtil.getColorizedString("ItemName: " + this.itemName + " (" + this.amount + ")", Formatting.GOLD));
        client.player.sendMessage(StringUtil.getColorizedString("itemKey: " + this.priceKey, Formatting.GRAY));

    }

    public String getPriceKey() {
        return priceKey;
    }

    @Override
    public String toString() {
        if (MinecraftServerUtil.MODE == Modes.NOTHING || MinecraftServerUtil.MODE == null || timeStamp == -1) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"mode\": \"").append(MinecraftServerUtil.MODE.getTranslationKey()).append("\", ");
        sb.append("\"itemName\": \"").append(this.itemName).append("\", ");
        sb.append("\"amount\": \"").append(this.amount).append("\"");
        sb.append(", \"sellerName\": \"").append(this.sellerName).append("\"");

        if (timeStamp != 0)
            sb.append(", \"timestamp\": \"").append(this.timeStamp).append("\"");
        if (this.sellerUuid != null)
            sb.append(", \"sellerUUID\": \"").append(this.sellerUuid).append("\"");
        if (this.bidPrice != null)
            sb.append(", \"bidPrice\": \"").append(this.bidPrice).append("\"");
        if (this.buyPrice != null)
            sb.append(", \"buyPrice\": \"").append(this.buyPrice).append("\"");
        if (this.priceKey != null)
            sb.append(", \"priceKey\": \"").append(this.priceKey).append("\"");
        if (comment != null)
            sb.append(", \"comment\": ").append(StringUtil.convertToJsonObject(this.comment));

        sb.append("}");
        return sb.toString();
    }

    public boolean equals(PriceCxnItemStack stack){
        if(this.itemName == null || stack.getItemName() == null){
            if(!(this.itemName == null && stack.getItemName() == null)) return false;
        } else if(!this.itemName.equals(stack.getItemName())) return false;

        if(this.buyPrice == null || stack.getBuyPrice() == null){
            if(!(this.buyPrice == null && stack.getBuyPrice() == null)) return false;
        } else if(!this.buyPrice.equals(stack.getBuyPrice())) return false;

        if(this.sellerUuid == null || stack.getSellerUuid() == null){
            if(!(this.sellerUuid == null && stack.getSellerUuid() == null)) return false;
        } else if(!this.sellerUuid.toString().equals(stack.getSellerUuid().toString())) return false;

        return TimeUtil.timestampsEqual(this.timeStamp, stack.getTimeStamp(), 3);
    }

    private void refreshSearchData(){
        if(!CxnListener.CONNECTED_TO_SERVER) return;

        String[] timestamp = CxnListener.getTranslationsAsArray("cxnprice.translation.auctions.search.timestamp");
        String[] seller = CxnListener.getTranslationsAsArray("cxnprice.translation.auctions.search.seller");
        String[] buy = CxnListener.getTranslationsAsArray("cxnprice.translation.auctions.search.buy");
        String[] bid = CxnListener.getTranslationsAsArray("cxnprice.translation.auctions.search.bid");

        if(timestamp != null)
            TIMESTAMP_SEARCH = timestamp;
       if(seller != null)
            SELLER_SEARCH = seller;
        if(buy != null)
            BUY_SEARCH = buy;
        if(bid != null)
            BID_SEARCH = bid;

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
