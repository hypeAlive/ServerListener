package net.alive.serverlistener.utils;

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

public class StringUtil {

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

    public static String getFirstSuffixStartingWith(List<String> strings, String[] prefixes) {
        for (String prefix : prefixes) {
            for (String s : strings) {
                if (s.startsWith(prefix)) {
                    return s.substring(prefix.length());
                }
            }
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

}
