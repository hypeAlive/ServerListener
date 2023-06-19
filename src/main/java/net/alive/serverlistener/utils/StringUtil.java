package net.alive.serverlistener.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.Session;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.UserCache;
import net.minecraft.util.Uuids;

import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static final int INT_NOT_FOUND = -9999999;

    private static final String REGEX = "(\\w+):\\s?(.+?)(?=,\\s?\\w+:|$)";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    public static MutableText getColorizedString(String string, Formatting formatting){
        return MutableText.of(new LiteralTextContent(string)).setStyle(Style.EMPTY.withColor(formatting));
    }

    public static Pair<String, Formatting> TextComponent(String str, Formatting formatting) {
        return Pair.of(str, formatting);
    }

    public static MutableText getColorizedString(List<Pair<String, Formatting>> parts) {

        String first = parts.get(0).getFirst();

        MutableText msg = StringUtil.getColorizedString(parts.get(0).getFirst(), parts.get(0).getSecond());

        for (Pair<String, Formatting> part : parts) {

            if(Objects.equals(part.getFirst(), first)) continue;

            msg.append(StringUtil.getColorizedString(part.getFirst(), part.getSecond()));
        }

        return msg;
    }

    public static List<String> getNbtTags(ItemStack stack){
        if(stack == null) return null;

        List<String> result = new ArrayList<>();

        NbtCompound tag = stack.getNbt();
        if (tag == null) {
            return null;
        } else {
            for (String key : tag.getKeys()) {
                result.add(key + ": " + tag.get(key));
            }
        }

        return result;
    }

    public static String extractStringFromWildcard(String input, String wildcardPattern) {
        String[] patternParts = wildcardPattern.split("\\*");

        if (patternParts.length != 2) {
            System.out.println("Invalid wildcard pattern");
            return null;
        }

        int startIndex = input.indexOf(patternParts[0]);
        if (startIndex == -1) {
            System.out.println("Start pattern not found");
            return null;
        }

        int endIndex = input.indexOf(patternParts[1], startIndex + patternParts[0].length());
        if (endIndex == -1) {
            System.out.println("End pattern not found");
            return null;
        }

        return input.substring(startIndex + patternParts[0].length(), endIndex);
    }

    public static int safeParseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            System.out.println("Invalid integer format: " + str);
            return StringUtil.INT_NOT_FOUND;
        }
    }

    public static List<String> getToolTips(ItemStack stack){
        if(stack == null) return null;
        List<String> result = new ArrayList<>();

        List<Text> tooltip = stack.getTooltip(MinecraftClient.getInstance().player, MinecraftClient.getInstance().options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.BASIC);

        for (Text line : tooltip){
            result.add(line.getString());
        }

        return result;
    }

    public static UUID getPlayerUUIDV2(String playerName) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return null; // Minecraft client not available
        }

        Session session = client.getSession();
        if (session == null) {
            return null; // No active session
        }

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), playerName);
        client.getSessionService().fillProfileProperties(gameProfile, true);

        if (gameProfile.getId() != null) {
            return gameProfile.getId();
        }

        return null; // Player UUID not found
    }

    public static String getFirstSuffixStartingWith(List<String> strings, String prefix) {
        for (String s : strings) {
            if (s.startsWith(prefix)) {
                return s.substring(prefix.length());
            }
        }
        return null;
    }

    public static String getFirstSuffixStartingWith(List<String> strings, String[] prefixes){
        String result;
        for(String prefix : prefixes){
            result = getFirstSuffixStartingWith(strings, prefix);
            if(result != null) return result;

        }
        return null;
    }

    public static UUID getPlayerUUID(String playerName) {
        return Uuids.getOfflinePlayerUuid(playerName);
    }



    public static JsonObject convertToJsonObject(String input) {
        Matcher matcher = PATTERN.matcher(input);

        JsonObject json = new JsonObject();

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);

            if (value.charAt(0) == '{' && value.charAt(value.length() - 1) != '}') {
                value += "}";
            }

            value = value.replaceAll("'", "")
                    .replaceAll("\\\\\"", "");

            try {
                JsonObject jsonObject = JsonParser.parseString(value).getAsJsonObject();

                jsonObject.entrySet().forEach(entry -> {
                    if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsString().length() > 0) {
                        String s1 = entry.getValue().getAsString();
                        char c = s1.charAt(s1.length() - 1);

                        if (c == 'l' || c == 'L') {
                            try {
                                jsonObject.addProperty(entry.getKey(), Long.parseLong(s1.substring(0, s1.length() - 1)));
                            } catch (NumberFormatException ignored) {
                            }
                        }

                        if (c == 'd' || c == 'D') {
                            try {
                                jsonObject.addProperty(entry.getKey(), Double.parseDouble(s1.substring(0, s1.length() - 1)));
                            } catch (NumberFormatException ignored) {
                            }
                        }

                        if (c == 'f' || c == 'F') {
                            try {
                                jsonObject.addProperty(entry.getKey(), Float.parseFloat(s1.substring(0, s1.length() - 1)));
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                });

                json.add(key, jsonObject);
            } catch (Exception e) {
                try {
                    json.add(key, JsonParser.parseString(value).getAsJsonArray());
                } catch (Exception e2) {
                    json.addProperty(key, value);
                }
            }
        }

        return json;
    }

    public static String[] addValueToArray(String[] originalArray, String newValue) {
        // Neues Array mit erhöhter Größe erstellen
        String[] newArray = new String[originalArray.length + 1];

        // Vorhandene Werte in das neue Array kopieren
        System.arraycopy(originalArray, 0, newArray, 0, originalArray.length);

        // Neuen Wert am Ende des Arrays hinzufügen
        newArray[newArray.length - 1] = newValue;

        return newArray;
    }

    public static boolean containsString(String string, String[] searches){
        for(String search : searches){
            if(string.contains(search))
                return true;
        }

        return false;
    }

    public static String mutantVocable(String string){
        if(string == null)
            return null;

        String ä = "Ã¤";
        String Ä = "Ã„";
        String ü = "Ã¼";
        String Ü = "Ãœ";
        String ö = "Ã¶";
        String Ö = "Ã–";
        String ß = "ÃŸ";


        return string
                .replace(ä, "ä")
                .replace(Ä, "Ä")
                .replace(ü, "ü")
                .replace(Ü, "Ü")
                .replace(ö, "ö")
                .replace(Ö, "Ö")
                .replace(ß, "ß");
    }

    public static List<String> mutantVocable(List<String> strings){

        List<String> result = new ArrayList<>();

        for(String string : strings){
            String help = mutantVocable(string);
            result.add(help);
        }

        return result;
    }

    public static String convertPrice(String input) {
        double number = Double.parseDouble(input);

        return convertPrice(number);
    }

    public static String convertPrice(double number) {
        // Formatierung mit Tausendertrennzeichen und Dezimaltrennzeichen
        NumberFormat format = NumberFormat.getNumberInstance(Locale.GERMAN);
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        return format.format(number);
    }

}
