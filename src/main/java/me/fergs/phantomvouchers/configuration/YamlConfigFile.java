package me.fergs.phantomvouchers.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * A configuration file that uses the YAML format.
 */
public class YamlConfigFile extends YamlConfiguration {
    private final File file;
    /**
     * Creates a new YamlConfigFile instance.
     *
     * @param file The file to load.
     */
    public YamlConfigFile(File file) {
        this.file = file;
        reload();
    }
    /**
     * Loads a configuration file from the specified file.
     *
     * @param file The file to load.
     * @return The loaded configuration file.
     */
    public static @NotNull YamlConfigFile loadConfiguration(@NotNull File file) {
        return new YamlConfigFile(file);
    }
    /**
     * Reloads the configuration file.
     */
    public void reload() {
        try {
            load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Saves the configuration file to disk.
     */
    public void save() {
        try {
            save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getConfig() {
        return this;
    }
    /**
     * Saves the configuration file and then reloads it.
     * This is useful for ensuring that any changes made are immediately reflected in the configuration.
     */
    public void saveAndReload() {
        save();
        reload();
    }
    /**
     * Gets a configuration section from the configuration file.
     *
     * @param path The path to the section.
     * @return The configuration section.
     */
    @Override
    public ConfigurationSection getConfigurationSection(@NotNull String path) {
        return super.getConfigurationSection(path);
    }
}
