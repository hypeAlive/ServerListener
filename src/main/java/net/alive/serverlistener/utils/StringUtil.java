package net.alive.serverlistener.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Uuids;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

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

    public static List<String> getToolTips(ItemStack stack){
        List<String> result = new ArrayList<>();

        List<Text> tooltip = stack.getTooltip(MinecraftClient.getInstance().player, MinecraftClient.getInstance().options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.BASIC);

        for (Text line : tooltip){
            result.add(line.getString());
        }

        return result;
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

    public static String extractStringFromWildcard(String input, String wildcardPattern) {
        String[] patternParts = wildcardPattern.split("\\*");
        if (patternParts.length == 2) {
            int startIndex = input.indexOf(patternParts[0]);
            if (startIndex != -1) {
                int endIndex = input.indexOf(patternParts[1], startIndex + patternParts[0].length());
                if (endIndex != -1) {
                    return input.substring(startIndex + patternParts[0].length(), endIndex);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
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

}
