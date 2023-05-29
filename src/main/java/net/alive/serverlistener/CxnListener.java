package net.alive.serverlistener;

import com.google.gson.*;
import net.alive.serverlistener.listener.AuctionInventoryListener;
import net.alive.serverlistener.listener.InventoryListener;
import net.alive.serverlistener.utils.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CxnListener {

    public static String MOD_NAME = "PreisCxn";
    public static Map<String, List<String>> TRANSLATION = null;
    public static Map<String, List<String>> SETTINGS = null;

    public static boolean CONNECTED_TO_SERVER = false;

    public CxnListener(){

        checkConnectionToServer();

        if(CONNECTED_TO_SERVER) {
            connectToServer();
        }

        MinecraftServerUtil.init(getSettingsAsArray("pricecxn.settings.client.server_ip"), getTranslationsAsArray("cxnprice.translation.mode.search"));

        InventoryListener auctionListener = new AuctionInventoryListener(getTranslationsAsArray("cxnprice.translation.auctions.search.inventory"), 6*9);
    }

    public static boolean checkConnectionToServer(){
        boolean test = ApiInteractionUtil.checkWebServerConnection(ApiInteractionUtil.API_URL);
        CONNECTED_TO_SERVER = test;
        return test;
    }

    private static Map<String, List<String>> importSettings(String url, List<String> columnNames, String keyColumnName) {
        Map<String, List<String>> data = null;

        try {
            String response = ApiInteractionUtil.sendGetRequest(url);

            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(response).getAsJsonArray();

            data = new HashMap<>();

            for (JsonElement object : array) {
                JsonObject json = object.getAsJsonObject();
                String key;

                try {
                    key = json.get(keyColumnName).getAsString();
                } catch (Exception e){
                    return null;
                }

                List<String> values = new ArrayList<>();

                for (String columnName : columnNames) {
                    try{
                        JsonNull no = json.get(columnName).getAsJsonNull();
                    } catch (Exception e){
                        String[] rowData = json.get(columnName).getAsString().split(", ");
                        values.addAll(Arrays.asList(rowData));
                    }
                }

                data.put(key, values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    private static Map<String, List<String>> importSettings(String url, String columnName, String keyColumnName) {
        List<String> list = new ArrayList<>();
        list.add(columnName);
        return importSettings(url, list, keyColumnName);
    }

    public static List<String> getSettings(String key){
        if(SETTINGS == null) return null;
        return SETTINGS.get(key);
    }

    public static List<String> getTranslations(String key){
        if(SETTINGS == null) return null;
        return TRANSLATION.get(key);
    }

    public static void addModes(){
        MinecraftServerUtil.cleanModes();

        if(!checkConnectionToServer()) return;

        String[] lobby = getTranslationsAsArray("cxnprice.translation.mode.lobby.name");
        String[] citybuild = getTranslationsAsArray("cxnprice.translation.mode.citybuild.name");
        String[] skyblock = getTranslationsAsArray("cxnprice.translation.mode.skyblock.name");

        MinecraftServerUtil.addMode(Modes.CITYBUILD, citybuild);
        MinecraftServerUtil.addMode(Modes.SKYBLOCK, skyblock);
        MinecraftServerUtil.addMode(Modes.LOBBY, lobby);
    }

    public static void connectToServer(){
        SETTINGS = importSettings(ApiInteractionUtil.API_URL + "/settings/mod", "setting_value", "setting_key");

        TRANSLATION = importSettings(ApiInteractionUtil.API_URL + "/settings/translations", getSettings("pricecxn.settings.client.languages"), "translation_key");

        MOD_NAME = getSettings("pricecxn.settings.client.modname") == null ? "PreisCxn" : getSettings("pricecxn.settings.client.modname").get(0);
        System.out.println(MOD_NAME);

        TimeUtil.refreshTimeSearch();
        addModes();
    }

    public static String[] getSettingsAsArray(String key){
        if(SETTINGS == null) return null;
        return getSettings(key).toArray(new String[0]);
    }
    public static String[] getTranslationsAsArray(String key){
        if(SETTINGS == null) return null;
        return getTranslations(key).toArray(new String[0]);
    }

}
