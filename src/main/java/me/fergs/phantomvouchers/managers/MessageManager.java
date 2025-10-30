package me.fergs.phantomvouchers.managers;

import com.google.common.collect.ImmutableList;
import me.fergs.phantomvouchers.configuration.ConfigurationManager;
import me.fergs.phantomvouchers.utils.Color;
import me.fergs.phantomvouchers.utils.MessageParser;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MessageManager {

    private final ConfigurationManager<?> configurationManager;
    /**
     * Creates a new MessageManager instance.
     * @param config The configuration file.
     */
    public MessageManager(ConfigurationManager<?> config) {
        this.configurationManager = config;
    }
    /**
     * Send a message to the player if enabled in the config.
     * @param executor The executor to send the message to.
     * @param key The key for the message in the config.
     * @param placeholders A map of placeholders to replace in the message.
     */
    public void sendMessage(CommandSender executor, String key, String... placeholders) {
        final Optional<ImmutableList<String>> message = getMessage(key);
        final Optional<String> sound = getSound(key);

        if (message.isPresent() && isMessageEnabled(key)) {
            message.get().forEach(line -> {
                String formattedMessage = MessageParser.parseKeyedValues(line, placeholders);
                executor.sendMessage(Color.hex(formattedMessage));
            });
        }

        if (isTitleEnabled(key) && executor instanceof Player) {
            final Optional<String> title = getTitle(key);
            Optional<String> subtitle = getSubtitle(key);
            title.ifPresent(t -> (((Player) executor).getPlayer()).sendTitle(Color.hex(MessageParser.parseKeyedValues(t, placeholders)), subtitle.map(s -> Color.hex(MessageParser.parse(s, placeholders))).orElse(null)));
        }

        if (isActionBarEnabled(key) && executor instanceof Player) {
            final Optional<String> actionBar = getActionBar(key);
            actionBar.ifPresent(a -> {
                ((Player) executor).sendActionBar(Color.hex(MessageParser.parseKeyedValues(a, placeholders)));
            });
        }

        if (sound.isPresent() && isSoundEnabled(key) && executor instanceof Player player) {
            playSound(player, sound.get());
        }
    }
    /**
     * Get the message from the config.
     * @param key The key for the message in the config.
     * @return The message from the config, if available.
     */
    public Optional<ImmutableList<String>> getMessage(String key) {
        ImmutableList<String> messages = ImmutableList.copyOf(
                configurationManager.getConfig("messages").getStringList("Messages." + key + ".Message.Value")
        );
        if (!messages.isEmpty()) {
            return Optional.of(messages);
        } else {
            return Optional.ofNullable(configurationManager.getConfig("messages").getString("Messages." + key + ".Message.Value"))
                    .map(Collections::singletonList)
                    .map(ImmutableList::copyOf);
        }
    }
    /**
     * Get the message template from the config.
     * @param key The key for the message in the config.
     * @return The message template from the config, if available.
     */
    public Optional<List<String>> getMessageTemplate(String key, String... placeholders) {
        return Optional.of(configurationManager.getConfig("messages").getStringList("Messages." + key + ".Message.Value"))
                .map(messages -> {
                    for (int i = 0; i < placeholders.length; i += 2) {
                        for (int j = 0; j < messages.size(); j++) {
                            messages.set(j, messages.get(j).replace(placeholders[i], placeholders[i + 1]));
                        }
                    }
                    return messages;
                });
    }
    /**
     * Get the title from the config.
     * @param key The key for the title in the config.
     * @return The title from the config, if available.
     */
    private Optional<String> getTitle(String key) {
        return Optional.ofNullable(configurationManager.getConfig("messages").getString("Messages." + key + ".Title.Title"));
    }
    /**
     * Get the subtitle from the config.
     * @param key The key for the subtitle in the config.
     * @return The subtitle from the config, if available.
     */
    private Optional<String> getSubtitle(String key) {
        return Optional.ofNullable(configurationManager.getConfig("messages").getString("Messages." + key + ".Title.Subtitle"));
    }
    /**
     * Get the sound from the config.
     * @param key The key for the sound in the config.
     * @return The sound from the config, if available.
     */
    private Optional<String> getSound(String key) {
        return Optional.ofNullable(configurationManager.getConfig("messages").getString("Messages." + key + ".Sound.Value"));
    }
    /**
     * Get the action bar from the config.
     * @param key The key for the action bar in the config.
     * @return The action bar from the config, if available.
     */
    private Optional<String> getActionBar(String key) {
        return Optional.ofNullable(configurationManager.getConfig("messages").getString("Messages." + key + ".Action-Bar.Value"));
    }
    /**
     * Check if the message is enabled.
     * @param key The key for the message in the config.
     * @return True if the message is enabled, false otherwise.
     */
    private boolean isMessageEnabled(String key) {
        return configurationManager.getConfig("messages").getBoolean("Messages." + key + ".Message.Enable", false);
    }
    /**
     * Check if the sound is enabled.
     * @param key The key for the sound in the config.
     * @return True if the sound is enabled, false otherwise.
     */
    private boolean isSoundEnabled(String key) {
        return configurationManager.getConfig("messages").getBoolean("Messages." + key + ".Sound.Enable", false);
    }
    /**
     * Check if the title is enabled.
     * @param key The key for the title in the config.
     * @return True if the title is enabled, false otherwise.
     */
    private boolean isTitleEnabled(String key) {
        return configurationManager.getConfig("messages").getBoolean("Messages." + key + ".Title.Enable", false);
    }
    private boolean isActionBarEnabled(String key) {
        return configurationManager.getConfig("messages").getBoolean("Messages." + key + ".Action-Bar.Enable", false);
    }
    /**
     * Play the sound for the player.
     * @param player The player to play the sound to.
     * @param soundData The sound data in the format: "sound;volume;pitch".
     */
    private void playSound(Player player, String soundData) {
        String[] parts = soundData.split(";");
        if (parts.length >= 3) {
            String soundName = parts[0];
            float volume = Float.parseFloat(parts[1]);
            float pitch = Float.parseFloat(parts[2]);

            Sound sound = Sound.valueOf(soundName);
            player.playSound(player, sound, volume, pitch);
        }
    }
}