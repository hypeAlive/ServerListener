package net.alive.serverlistener;

import com.google.gson.Gson;
import com.mojang.datafixers.util.Pair;
import net.alive.serverlistener.utils.MinecraftServerUtil;
import net.alive.serverlistener.utils.Modes;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ItemData {
    private int id;
    private String item_name;
    private String mode;
    private Double max_price;
    private Double min_price;
    private String additional_search;
    private String item_id;

    public static List<ItemData> fromJson(String json) {
        Gson gson = new Gson();
        return Arrays.asList(gson.fromJson(json, ItemData[].class));
    }

    public static String toJson(List<ItemData> itemData) {
        Gson gson = new Gson();
        return gson.toJson(itemData);
    }

    public static ItemData getItemData(List<ItemData> itemData, List<String> nbt, String item_name) {
        if(itemData == null) return null;
        if(!(MinecraftServerUtil.MODE == Modes.CITYBUILD ||MinecraftServerUtil.MODE == Modes.SKYBLOCK)) return null;

        int foundItems = 0;

        ItemData foundItem = null;

        for (ItemData item : itemData) {

            //System.out.println(item.getMode() + " : " + MinecraftServerUtil.MODE.getTranslationKey());


            if(!Objects.equals(item.getMode(), MinecraftServerUtil.MODE.getTranslationKey())){
                continue;
            }


            if (item.getItemName().equals(item_name)) {
                foundItems++;
                foundItem = item;
            }
        }

        System.out.println(nbt);
        if(nbt == null){
            if(foundItems == 1){
                return foundItem;
            } else
                return null;
        }

        for (ItemData item : itemData){
            if(!item.getMode().equals(MinecraftServerUtil.MODE.getTranslationKey())) continue;
            String[] searches = item.getItemName().split("\\.");

            boolean isItem = true;

            for (String search : searches) {
                if(ServerListenerClient.DEBUG_MODE)
                    System.out.println(search);
                if(nbt.toString().contains("StoredEnchantments") && (search.contains("treasurechestitems") || search.contains("enchanted_book"))) {
                    continue;
                }
                if(ServerListenerClient.DEBUG_MODE)
                    System.out.println("Searching for " + search);
                if(nbt.toString().toLowerCase().contains(search)){
                    if(ServerListenerClient.DEBUG_MODE)
                        System.out.println("Found " + search);
                } else {
                    isItem = false;
                }
            }
            if (isItem) {
                foundItems++;
                foundItem = item;
            }
        }

        System.out.println(foundItems);

        if(foundItems == 1)
            return foundItem;
        return null;

    }

    public int getId() {
        return id;
    }

    public String getItemName() {
        return item_name;
    }

    public String getMode() {
        return mode;
    }

    public Double getMaxPrice() {
        return max_price;
    }

    public Double getMinPrice() {
        return min_price;
    }

    public String getAdditionalSearch() {
        return additional_search;
    }

    public String getItemId() {
        return item_id;
    }

    @Override
    public String toString() {

        StringBuilder string = new StringBuilder();

        string.append("ID: ").append(this.getId()).append("\n");
        string.append("Item Name: ").append(this.getItemName()).append("\n");
        string.append("Mode: ").append(this.getMode()).append("\n");
        string.append("Max Price: ").append(this.getMaxPrice()).append("\n");
        string.append("Min Price: ").append(this.getMinPrice()).append("\n");
        string.append("Additional Search: ").append(this.getAdditionalSearch()).append("\n");
        string.append("Item ID: ").append(this.getItemId()).append("\n");

        return string.toString();

    }
}
