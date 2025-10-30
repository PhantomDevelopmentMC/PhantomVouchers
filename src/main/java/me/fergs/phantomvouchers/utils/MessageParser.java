package me.fergs.phantomvouchers.utils;

import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MessageParser {
    private static final Random random = new Random();
    private static final Pattern RANDOM_PATTERN = Pattern.compile("%random_(\\d+)_(\\d+)%");
    private static final Pattern PLAYER_PATTERN = Pattern.compile("%player%");
    private static final Pattern PLAYER_UUID_PATTERN = Pattern.compile("%player_uuid%");
    private static final Pattern WORLD_PATTERN = Pattern.compile("%world%");
    private static final Pattern X_PATTERN = Pattern.compile("%x%");
    private static final Pattern Y_PATTERN = Pattern.compile("%y%");
    private static final Pattern Z_PATTERN = Pattern.compile("%z%");
    private static final Pattern DATE_PATTERN = Pattern.compile("%date%");
    private static final Pattern VAR_PATTERN = Pattern.compile("%var_([a-zA-Z_]+)%");

    /**
     * Parse a message and replace placeholders with actual values.
     * @param message The message to parse.
     * @param player The player object for player-specific placeholders.
     * @return The parsed message with placeholders replaced.
     */
    public static String parse(String message, Player player) {
        message = parseDynamicPlaceholders(message, player, null);
        return message;
    }

    /**
     * Parse a message and replace placeholders with actual values.
     * @param message The message to parse.
     * @param placeholders A map of placeholder keys and values.
     * @return The parsed message with placeholders replaced.
     */
    public static String parse(String message, String... placeholders) {
        // For backward compatibility, assume player is provided in placeholders
        Player player = null;
        if (placeholders.length >= 2 && "%player%".equals(placeholders[0])) {
            // Can't get player from name, so perhaps change to require Player
            // For now, parse without player-specific
            message = parseDynamicPlaceholders(message, null, null);
        } else {
            message = parseDynamicPlaceholders(message, null, null);
        }
        for (int i = 0; i < placeholders.length; i += 2) {
            message = message.replace(placeholders[i], placeholders[i + 1]);
        }
        return message;
    }

    /**
     * Parse a message and replace keyed placeholders with actual values.
     * @param message The message to parse.
     * @param placeholders A map of placeholder keys and values.
     * @return The parsed message with placeholders replaced.
     */
    public static String parseKeyedValues(String message, String... placeholders) {
        message = parseDynamicPlaceholders(message, null, null);
        for (int i = 0; i < placeholders.length - 1; i += 2) {
            message = message.replace(placeholders[i], placeholders[i + 1]);
        }
        return message;
    }

    /**
     * Parse a message and replace placeholders with actual values.
     * @param message The message to parse.
     * @param player The player object for player-specific placeholders.
     * @param variables A map of custom variables.
     * @return The parsed message with placeholders replaced.
     */
    public static String parse(String message, Player player, Map<String, String> variables) {
        message = parseDynamicPlaceholders(message, player, variables);
        return message;
    }

    /**
     * Parse dynamic placeholders.
     * @param message The message to parse.
     * @param player The player, can be null.
     * @param variables A map of custom variables.
     * @return The message with dynamic placeholders replaced.
     */
    private static String parseDynamicPlaceholders(String message, Player player, Map<String, String> variables) {
        // Random
        Matcher randomMatcher = RANDOM_PATTERN.matcher(message);
        StringBuffer sb = new StringBuffer();
        while (randomMatcher.find()) {
            int min = Integer.parseInt(randomMatcher.group(1));
            int max = Integer.parseInt(randomMatcher.group(2));
            int randomValue = random.nextInt(max - min + 1) + min;
            randomMatcher.appendReplacement(sb, String.valueOf(randomValue));
        }
        randomMatcher.appendTail(sb);
        message = sb.toString();

        // Player-specific
        if (player != null) {
            message = PLAYER_PATTERN.matcher(message).replaceAll(player.getName());
            message = PLAYER_UUID_PATTERN.matcher(message).replaceAll(player.getUniqueId().toString());
            message = WORLD_PATTERN.matcher(message).replaceAll(player.getWorld().getName());
            message = X_PATTERN.matcher(message).replaceAll(String.valueOf((int) player.getLocation().getX()));
            message = Y_PATTERN.matcher(message).replaceAll(String.valueOf((int) player.getLocation().getY()));
            message = Z_PATTERN.matcher(message).replaceAll(String.valueOf((int) player.getLocation().getZ()));
        }

        // Date
        message = DATE_PATTERN.matcher(message).replaceAll(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Custom variables
        if (variables != null) {
            Matcher varMatcher = VAR_PATTERN.matcher(message);
            sb = new StringBuffer();
            while (varMatcher.find()) {
                String varName = varMatcher.group(1);
                String value = variables.get(varName);
                varMatcher.appendReplacement(sb, value != null ? value : "");
            }
            varMatcher.appendTail(sb);
            message = sb.toString();
        }

        return message;
    }
}
