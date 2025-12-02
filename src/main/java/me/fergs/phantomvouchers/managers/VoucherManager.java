package me.fergs.phantomvouchers.managers;

import lombok.Getter;
import me.fergs.phantomvouchers.PhantomVouchers;
import me.fergs.phantomvouchers.actions.*;
import me.fergs.phantomvouchers.actions.impl.*;
import me.fergs.phantomvouchers.configuration.YamlConfigFile;
import me.fergs.phantomvouchers.objects.VoucherItem;
import me.fergs.phantomvouchers.utils.ConsoleUtil;
import me.fergs.phantomvouchers.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public final class VoucherManager {
    private final PhantomVouchers plugin;
    private final Map<String, VoucherItem> vouchers = new HashMap<>();
    private final NamespacedKey voucherKey;

    public VoucherManager(PhantomVouchers plugin) {
        this.plugin = plugin;
        this.voucherKey = new NamespacedKey(plugin, "voucher_id");
        loadVouchers();
    }

    private void loadVouchers() {
        File vouchersFolder = new File(plugin.getDataFolder(), "vouchers");
        if (!vouchersFolder.exists()) {
            vouchersFolder.mkdirs();
            plugin.saveResource("vouchers/test-voucher.yml", false);
        }

        File[] files = vouchersFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String id = file.getName().replace(".yml", "");
                YamlConfigFile config = YamlConfigFile.loadConfiguration(file);
                VoucherItem voucher = parseVoucher(config, id);
                if (voucher != null) {
                    vouchers.put(id, voucher);
                    Bukkit.getLogger().info(ConsoleUtil.translateColors("&6[&e!&6] &eLoaded &f" + id + " &evoucher."));
                }
            }
        }
    }

    private VoucherItem parseVoucher(YamlConfigFile config, String id) {
        try {
            VoucherItem.ItemConfig itemConfig = parseItemConfig(config);
            List<String> actionStrings = config.getStringList("redeem-actions");
            List<IAction> redeemActions = actionStrings.stream()
                    .map(this::parseActionString)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            VoucherItem.VoucherSettings settings = parseSettings(config);

            return new VoucherItem(id, itemConfig, redeemActions, settings);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to parse voucher " + id + ": " + e.getMessage());
            return null;
        }
    }

    private IAction parseActionString(String actionString) {
        if (actionString.startsWith("[MESSAGE] ")) {
            return new MessageAction(actionString.substring(10));
        } else if (actionString.startsWith("[COMMAND] ")) {
            return new CommandAction(actionString.substring(10));
        } else if (actionString.startsWith("[REQUIREMENT] ")) {
            return new RequirementAction(actionString.substring(14));
        } else if (actionString.startsWith("[SOUND] ")) {
            String[] parts = actionString.substring(8).split(";");
            if (parts.length >= 1) {
                try {
                    String soundName = parts[0];
                    float volume = parts.length > 1 ? Float.parseFloat(parts[1]) : 1.0f;
                    float pitch = parts.length > 2 ? Float.parseFloat(parts[2]) : 1.0f;
                    return new SoundAction(soundName, volume, pitch);
                } catch (Exception e) {
                    return null;
                }
            }
        } else if (actionString.startsWith("[TITLE] ")) {
            String[] parts = actionString.substring(8).split(";");
            if (parts.length >= 2) {
                String title = parts[0];
                String subtitle = parts[1];
                int fadeIn = parts.length > 2 ? Integer.parseInt(parts[2]) : 10;
                int stay = parts.length > 3 ? Integer.parseInt(parts[3]) : 70;
                int fadeOut = parts.length > 4 ? Integer.parseInt(parts[4]) : 20;
                return new TitleAction(title, subtitle, fadeIn, stay, fadeOut);
            }
        } else if (actionString.startsWith("[ACTION_BAR] ")) {
            return new ActionBarAction(actionString.substring(13));
        } else if (actionString.startsWith("[RANDOM] ")) {
            String randomPart = actionString.substring(9).trim();
            String[] options = {randomPart};
            List<RandomAction.WeightedAction> weightedActions = new ArrayList<>();
            for (String option : options) {
                String[] chanceActions = option.trim().split(":", 2);
                if (chanceActions.length == 2) {
                    try {
                        int chance = Integer.parseInt(chanceActions[0].trim());
                        String actionsPart = chanceActions[1].trim();
                        String[] subActionStrings = actionsPart.split("\\|");
                        List<IAction> subActions = new ArrayList<>();
                        for (String subActionString : subActionStrings) {
                            IAction subAction = parseActionString(subActionString.trim());
                            if (subAction != null) {
                                subActions.add(subAction);
                            }
                        }
                        if (!subActions.isEmpty()) {
                            weightedActions.add(new RandomAction.WeightedAction(chance, subActions));
                        }
                    } catch (NumberFormatException e) {
                        Bukkit.getLogger().warning("Invalid chance value in RANDOM action: " + chanceActions[0]);
                    }
                }
            }
            if (!weightedActions.isEmpty()) {
                return new RandomAction(weightedActions);
            }
        } else if (actionString.startsWith("[SET_VAR] ")) {
            String varPart = actionString.substring(10).trim();
            String[] parts = varPart.split(" ", 2);
            if (parts.length == 2) {
                String varName = parts[0];
                String valueExpression = parts[1];
                return new SetVarAction(varName, valueExpression);
            }
        }
        return null;
    }

    private VoucherItem.ItemConfig parseItemConfig(YamlConfigFile config) {
        Material material = Material.valueOf(config.getString("item.material", "PAPER"));
        Integer customModelData = config.contains("item.custom-model-data") ? config.getInt("item.custom-model-data") : null;
        String displayName = config.getString("item.display-name");
        List<String> lore = config.getStringList("item.lore");
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        if (config.getConfigurationSection("item.enchantments") != null) {
            for (String key : config.getConfigurationSection("item.enchantments").getKeys(false)) {
                Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(key.toLowerCase()));
                if (ench != null) {
                    int level = config.getInt("item.enchantments." + key);
                    enchantments.put(ench, level);
                }
            }
        }
        List<ItemFlag> itemFlags = config.getStringList("item.item-flags").stream()
                .map(flag -> ItemFlag.valueOf(flag.toUpperCase()))
                .collect(Collectors.toList());
        String base64 = config.getString("item.base-64");

        return new VoucherItem.ItemConfig(material, customModelData, displayName, lore, enchantments, itemFlags, base64);
    }

    private VoucherItem.VoucherSettings parseSettings(YamlConfigFile config) {
        Set<String> blacklistedWorlds = new HashSet<>(config.getStringList("settings.blacklisted-worlds"));
        Set<String> blacklistedRegions = new HashSet<>(config.getStringList("settings.blacklisted-regions"));
        String permissionRequired = config.getString("settings.permission-required");
        Boolean additionalConfirmation = config.getBoolean("settings.additional-confirmation", false);

        return new VoucherItem.VoucherSettings(blacklistedWorlds, blacklistedRegions, permissionRequired, additionalConfirmation);
    }

    private ItemStack buildItemStack(VoucherItem voucher) {
        VoucherItem.ItemConfig item = voucher.getItem();
        ItemBuilder builder = ItemBuilder.create(item.getMaterial());

        if (item.getBase64() != null) {
            builder.setSkullTexture(item.getBase64());
        }

        builder.setName(item.getDisplayName())
                .setLore(item.getLore())
                .setCustomModelData(item.getCustomModelData())
                .addItemFlags(item.getItemFlags());

        for (Map.Entry<Enchantment, Integer> ench : item.getEnchantments().entrySet()) {
            builder.addEnchant(ench.getKey(), ench.getValue());
        }

        builder.setPDCString(voucherKey, voucher.getId());

        YamlConfigFile settings = plugin.getConfigurationManager().getConfig("settings");
        if (settings.getBoolean("settings.uuid-footprint", true)) {
            builder.setPDCString(new NamespacedKey(plugin, "voucher_uuid"), UUID.randomUUID().toString());
        }

        return builder.build();
    }

    public VoucherItem getVoucher(String id) {
        return vouchers.get(id);
    }

    public ItemStack getVoucherItem(String id) {
        VoucherItem voucher = getVoucher(id);
        return voucher != null ? buildItemStack(voucher) : null;
    }

    public void reload() {
        loadVouchers();
    }
}
