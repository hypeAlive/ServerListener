package net.alive.serverlistener.utils;

import com.google.gson.Gson;
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
import java.util.List;

public class ApiInteractionUtil {

    public static final String API_URL = "http://localhost:8080/api";

    public static void sendData(List<String> data, String insertUrl){
        try {

            data.set(0, "{\"sender\": \"" + MinecraftClient.getInstance().player.getUuid() + "\",\"items\": [" + data.get(0));
            data.set(data.size()-1, data.get(data.size()-1) + "]}");

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
            while((output = br.readLine()) != null){
                //System.out.println(output);
            }

            conn.disconnect();

        } catch (Exception e){
            e.printStackTrace();
        }

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
