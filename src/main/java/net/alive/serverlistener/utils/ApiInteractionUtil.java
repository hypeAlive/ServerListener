package net.alive.serverlistener.utils;

import com.google.gson.Gson;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ApiInteractionUtil {

    public static void sendData(List<String> data){
        try {

            Gson gson = new Gson();
            String json = gson.toJson(data);

            URL url = new URL("http://localhost:8080/api/datahandler/auctionhouse");
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
                System.out.println(output);
            }

            conn.disconnect();

        } catch (Exception e){
            e.printStackTrace();
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
