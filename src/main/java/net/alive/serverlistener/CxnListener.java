package net.alive.serverlistener;

import com.google.gson.*;
import net.alive.serverlistener.listener.AuctionInventoryListener;
import net.alive.serverlistener.listener.InventoryListener;
import net.alive.serverlistener.utils.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static net.alive.serverlistener.utils.ApiInteractionUtil.importSettingsAsync;

public class CxnListener {

    public static String MOD_NAME = "PreisCxn";
    private static String MOD_VERSION = "1.1.7";
    public static String MIN_VERSION = null;
    public static boolean MAINTENANCE = false;
    public static boolean ACTIVATE = false;
    public static Map<String, List<String>> TRANSLATION = null;
    public static Map<String, List<String>> SETTINGS = null;
    public static Map<String, List<String>> MOD_USERS = null;
    private static List<String> MOD_USER_NAMES = null;
    public static List<ItemData> ITEM_DATA = null;
    public static boolean CONNECTED_TO_SERVER = false;

    public CxnListener(){

        checkConnectionToServer();

        if(CONNECTED_TO_SERVER) {
            connectToServer(true);
        }

        MinecraftServerUtil.init(getSettingsAsArray("pricecxn.settings.client.server_ip"), getTranslationsAsArray("cxnprice.translation.mode.search"));

        InventoryListener auctionListener = new AuctionInventoryListener(getTranslationsAsArray("cxnprice.translation.auctions.search.inventory"), 6*9);
    }

    public static boolean checkConnectionToServer(){

        boolean test = ApiInteractionUtil.checkWebServerConnection(ApiInteractionUtil.API_URL);

        if(CONNECTED_TO_SERVER && !test && MinecraftClient.getInstance().player != null)
            MinecraftServerUtil.sendModMessage(MinecraftClient.getInstance(), Formatting.RED, "Verbindung zum Server verloren...");

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
        return StringUtil.mutantVocable(SETTINGS.get(key));
    }

    public static List<String> getTranslations(String key){
        if(SETTINGS == null) return null;
        return StringUtil.mutantVocable(TRANSLATION.get(key));
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

    public static void connectToServer(boolean all){
        //System.out.println("Connected to Server");
        if(all) {
            SETTINGS = importSettings(ApiInteractionUtil.API_URL + "/settings/mod", "setting_value", "setting_key");

            List<String> columnNames = new ArrayList<>();
            columnNames.add("trust_level");
            columnNames.add("user_name");

            TRANSLATION = importSettings(ApiInteractionUtil.API_URL + "/settings/translations", getSettings("pricecxn.settings.client.languages"), "translation_key");

            MOD_NAME = getOneLineSetting("pricecxn.settings.client.modname") == null ? "PreisCxn" : getOneLineSetting("pricecxn.settings.client.modname");

            MAINTENANCE = getOneLineSetting("pricecxn.settings.client.maintenance") != null && Objects.equals(getOneLineSetting("pricecxn.settings.client.maintenance"), "true");

            MIN_VERSION = getOneLineSetting("pricecxn.settings.client.minversion");

            if(checkModVersion(MIN_VERSION))
                ACTIVATE = true;

        }
        //System.out.println(MOD_NAME);

        List<String> columnNames = new ArrayList<>();
        columnNames.add("trust_level");
        columnNames.add("user_name");
        CompletableFuture<Map<String, List<String>>> future = importSettingsAsync(ApiInteractionUtil.API_URL + "/datahandler/mod_users", columnNames, "user_uuid");

        future.thenAccept(data -> {
            MOD_USERS = data;
            refreshModUserNames();
            //System.out.println(MOD_USERS);
            //System.out.println(MOD_USER_NAMES);
        }).exceptionally(ex -> {
            // Fehlerbehandlung
            //System.out.println("Fehler!!!!!!");
            return null;
        });


        Http.GET("/datahandler/items", ItemData::fromJson, itemData -> ITEM_DATA = itemData);

        //ITEM_DATA = ItemData.fromJson(ApiInteractionUtil.sendData(new ArrayList<>(), ApiInteractionUtil.API_URL + "/datahandler/items"));

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

    private static boolean checkModVersion(String minVersion){
        if(SETTINGS == null) return false;

        int modVersion = StringUtil.safeParseInt(MOD_VERSION.replace(".", ""));
        int minVersionInt = StringUtil.safeParseInt(minVersion.replace(".", ""));

        if(modVersion == StringUtil.INT_NOT_FOUND || minVersionInt == StringUtil.INT_NOT_FOUND)
            return false;

        if(modVersion >= minVersionInt)
            return true;

        return false;
    }

    public static String getOneLineSetting(String key){
        return getSettings(key) == null || Objects.requireNonNull(getSettings(key)).size() != 1 ? null : Objects.requireNonNull(getSettings(key)).get(0);
    }

    public static int getTrustLevel(UUID playerUuid){
        try {
            String trustLevel = MOD_USERS.get(playerUuid.toString()).get(0);

            if (trustLevel == null)
                return StringUtil.INT_NOT_FOUND;

            int trustLevelInt = StringUtil.safeParseInt(trustLevel);

            if (trustLevelInt == StringUtil.INT_NOT_FOUND)
                return StringUtil.INT_NOT_FOUND;
            else
                return trustLevelInt;
        } catch (Exception e){
            return StringUtil.INT_NOT_FOUND;
        }
    }

    public static boolean isModUser(UUID playerUuid){
        return MOD_USERS.containsKey(playerUuid.toString());
    }

    public static boolean isModUser(String playerName){
        if(MOD_USER_NAMES == null) return false;

        return MOD_USER_NAMES.contains(playerName);
    }

    /*
    public static boolean isModUser(String playerName){
        for (List<String> list : MOD_USERS.values()) {
            if (list.contains(playerName)) {
                return true;
            }
        }
        return false;
    }
     */

    private static void refreshModUserNames(){
        if(MOD_USERS == null) return;

        MOD_USER_NAMES = new ArrayList<>();

        for (List<String> list : MOD_USERS.values()) {
            if(list.size() < 2) continue;

            MOD_USER_NAMES.add(list.get(1));

        }
    }


}
