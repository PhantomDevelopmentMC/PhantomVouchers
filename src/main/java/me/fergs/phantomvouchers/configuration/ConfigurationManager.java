package me.fergs.phantomvouchers.configuration;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A configuration manager for managing configuration files.
 *
 * @param <T> The plugin type.
 */
public class ConfigurationManager<T extends JavaPlugin> {
    /**
     * The plugin instance.
     * -- GETTER --
     *  Gets the plugin instance.
     *
     * @return The plugin instance.

     */
    @Getter
    private final T plugin;
    /**
     * The configuration cache.
     */
    private final Map<String, YamlConfigFile> configurationCache = new ConcurrentHashMap<>();
    /**
     * Creates a new ConfigurationManager instance.
     *
     * @param plugin The plugin instance.
     */
    public ConfigurationManager(final T plugin) {
        this.plugin = plugin;
    }
    /**
     * Loads a configuration file from the plugin's data folder.
     *
     * @param fileName The name of the file to load.
     * @return The loaded configuration file.
     */
    public YamlConfigFile loadConfig(String fileName) {
        File configFile = new File(plugin.getDataFolder(), fileName + ".yml");
        if (!configFile.exists()) {
            plugin.saveResource(fileName + ".yml", false);
        }

        YamlConfigFile config = YamlConfigFile.loadConfiguration(configFile);
        configurationCache.put(fileName, config);
        return config;
    }
    /**
     * Loads multiple configuration files from the plugin's data folder.
     *
     * @param fileNames The names of the files to load.
     */
    public void loadConfigs(String... fileNames) {
        for (String fileName : fileNames) {
            loadConfig(fileName);
        }
    }
    /**
     * Gets a configuration file from the cache.
     *
     * @param fileName The name of the file to get.
     * @return The configuration file.
     */
    public YamlConfigFile getConfig(String fileName) {
        return configurationCache.computeIfAbsent(fileName, this::loadConfig);
    }
    /**
     * Reloads a configuration file.
     *
     * @param fileName The name of the file to reload.
     */
    public void reloadConfig(String fileName) {
        YamlConfigFile config = loadConfig(fileName);
        config.reload();
    }
    /**
     * Creates a new configuration file in the plugin's data folder.
     *
     * @param fileName The name of the file to create.
     */
    public YamlConfigFile createConfig(String fileName) {
        File configFile = new File(plugin.getDataFolder(), fileName + ".yml");
        if (!configFile.exists()) {
            plugin.saveResource(fileName + ".yml", false);
        }
        YamlConfigFile config = YamlConfigFile.loadConfiguration(configFile);
        configurationCache.put(fileName, config);
        return config;
    }
    /**
     * Reloads all configuration files.
     */
    public void reloadAllConfigs() {
        configurationCache.keySet().forEach(this::reloadConfig);
    }
}