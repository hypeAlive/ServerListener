package net.alive.serverlistener.utils;

import com.mojang.datafixers.util.Pair;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;

public class StringUtil {

    public static MutableText getColorizedString(String string, Formatting formatting){
        return MutableText.of(new LiteralTextContent(string)).setStyle(Style.EMPTY.withColor(formatting));
    }

    public static Pair<String, Formatting> TextComponent(String str, Formatting formatting) {
        return Pair.of(str, formatting);
    }

    public static MutableText getColorizedString(List<Pair<String, Formatting>> parts) {
        MutableText msg = StringUtil.getColorizedString(parts.get(0).getFirst(), parts.get(0).getSecond());

        parts.remove(0);

        for (Pair<String, Formatting> part : parts) {
            msg.append(StringUtil.getColorizedString(part.getFirst(), part.getSecond()));
        }

        return msg;
    }


}
