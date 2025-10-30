package me.fergs.phantomvouchers.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Color {
    /*
     * Translate color codes and hex codes to their respective color codes.
     * @param message The message to translate.
     */
    public static String hex(String message) {
        Pattern pattern = Pattern.compile("(#[a-fA-F0-9]{6})");
        for(Matcher matcher = pattern.matcher(message); matcher.find(); matcher = pattern.matcher(message)) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');
            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char c : ch) {
                builder.append("&").append(c);
            }
            message = message.replace(hexCode, builder.toString());
        }
        return ChatColor.translateAlternateColorCodes('&', message).replace('&', '§');
    }

    public static List<String> hexList(List<String> messages) {
        return messages.stream()
                .map(Color::hex)
                .collect(Collectors.toList());
    }
}
