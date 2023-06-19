package net.alive.serverlistener.utils;

import com.google.gson.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static net.alive.serverlistener.utils.Http.GET;

public class ApiInteractionUtil {

    public static final String API_URL = "https://preiscxn.de/api";

    public static CompletableFuture<Map<String, List<String>>> importSettingsAsync(String url, List<String> columnNames, String keyColumnName) {
        CompletableFuture<Map<String, List<String>>> future = new CompletableFuture<>();

        GET(url, "", response -> response, jsonString -> {
            Map<String, List<String>> data = null;

            try {
                JsonParser parser = new JsonParser();
                JsonArray array = parser.parse(jsonString).getAsJsonArray();

                data = new HashMap<>();

                for (JsonElement object : array) {
                    JsonObject json = object.getAsJsonObject();
                    String key;

                    try {
                        key = json.get(keyColumnName).getAsString();
                    } catch (Exception e) {
                        return null;
                    }

                    List<String> values = new ArrayList<>();

                    for (String columnName : columnNames) {
                        try {
                            JsonNull no = json.get(columnName).getAsJsonNull();
                        } catch (Exception e) {
                            String[] rowData = json.get(columnName).getAsString().split(", ");
                            values.addAll(Arrays.asList(rowData));
                        }
                    }

                    data.put(key, values);
                }
            } catch (Exception e) {
                future.completeExceptionally(e);
            }

            future.complete(data);
            return null;
        });

        return future;
    }

    public static String sendData(List<String> data, String insertUrl){
        try {

            System.out.println(data.size());

            if(data.size() == 0){
                data.add(0, "{\"sender\": \"" + (MinecraftClient.getInstance().player == null ? null : MinecraftClient.getInstance().player.getUuid()) + "}");
            } else {
                data.set(0, "{\"sender\": \"" + (MinecraftClient.getInstance().player == null ? null : MinecraftClient.getInstance().player.getUuid()) + "\",\"items\": [" + data.get(0));
                data.set(data.size()-1, data.get(data.size()-1) + "]}");
            }

            Gson gson = new Gson();
            String json = gson.toJson(data);

            URL url = new URL(insertUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String output;
            StringBuilder result = new StringBuilder();
            while((output = br.readLine()) != null){
                result.append(output);
            }

            conn.disconnect();
            return result.toString();

        } catch (Exception e){
            e.printStackTrace();
        }
        return null;


    }

    public static String getData(String insertUrl){
        try {

            URL url = new URL(insertUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");


            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String output;
            StringBuilder result = new StringBuilder();
            while ((output = br.readLine()) != null) {
                result.append(output);
            }

            conn.disconnect();
            return result.toString();

        } catch (Exception e){
            e.printStackTrace();
        }
        return null;


    }

    public static String sendGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } else {
            throw new IOException("GET request failed with response code: " + responseCode);
        }
    }

    public static boolean checkWebServerConnection(String url) {
        try {
            URL serverUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(500); // Timeout nach 5000 Millisekunden

            int responseCode = connection.getResponseCode();
            return true;
        } catch (IOException e) {
            return false;
        }
    }



    public static void testData(MinecraftClient client, List<String> data, ItemStack item){
        if(client.player == null) return;
        
        boolean specialItem = false;

        client.player.sendMessage(StringUtil.getColorizedString(item.getName().toString(),Formatting.GOLD));

        /*
        for (String line : data){

            if(line.contains("PublicBukkitValues:")){
                specialItem = true;
            }

            client.player.sendMessage(StringUtil.getColorizedString(line, specialItem ? Formatting.GREEN : Formatting.RED));
            client.player.sendMessage(StringUtil.getColorizedString("", Formatting.GRAY));
        }

         */
    }

}
