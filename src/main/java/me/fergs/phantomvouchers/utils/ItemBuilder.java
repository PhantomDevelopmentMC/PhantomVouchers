package me.fergs.phantomvouchers.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public final class ItemBuilder {
    private ItemStack item;
    private ItemMeta meta;
    private PersistentDataContainer pdc;
    /**
     * Creates a new ItemBuilder.
     *
     * @param material The material of the item.
     */
    public ItemBuilder(final Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
        if (this.meta != null) {
            this.pdc = this.meta.getPersistentDataContainer();
        }
    }
    /**
     * Creates a new ItemBuilder.
     *
     * @param material The material of the item.
     */
    public static ItemBuilder create(final Material material) {
        return new ItemBuilder(material);
    }
    /**
     * Sets the name of the item.
     *
     * @param name The name of the item.
     * @return The ItemBuilder.
     */
    public ItemBuilder setName(final String name) {
        if (meta != null && name != null) {
            meta.setDisplayName(Color.hex(name));
        }
        return this;
    }
    /**
     * Sets the skull texture of the item.
     *
     * @param base64 The base64 texture string.
     * @return The ItemBuilder.
     */
    public ItemBuilder setSkullTexture(String base64) {
        if (item.getType() == Material.PLAYER_HEAD && base64 != null) {
            this.item = SkullUtils.itemFromBase64(base64);
            this.meta = item.getItemMeta();
            if (this.meta != null) {
                this.pdc = this.meta.getPersistentDataContainer();
            }
        }
        return this;
    }
    /**
     * Sets the color of the arrow (converts to tipped arrow if necessary).
     *
     * @param color The color to set.
     * @return The ItemBuilder.
     */
    public ItemBuilder setArrowColor(final org.bukkit.Color color) {
        if (color != null && item.getType().name().contains("ARROW")) {
            item.setType(Material.TIPPED_ARROW);
            meta = item.getItemMeta();
            if (meta instanceof PotionMeta) {
                ((PotionMeta) meta).setColor(color);
            }
        }
        return this;
    }
    /**
     * Sets the lore of the item.
     *
     * @param lore The lore of the item.
     * @return The ItemBuilder.
     */
    public ItemBuilder setLore(final List<String> lore) {
        if (meta != null && lore != null) {
            final List<String> coloredLore = new ArrayList<>();
            for (final String line : lore) {
                coloredLore.add(Color.hex(line));
            }
            meta.setLore(coloredLore);
        }
        return this;
    }

    /**
     * Sets the custom model data of the item.
     *
     * @param customModelData The custom model data.
     * @return The ItemBuilder.
     */
    public ItemBuilder setCustomModelData(final Integer customModelData) {
        if (meta != null && customModelData != null) {
            meta.setCustomModelData(customModelData);
        }
        return this;
    }
    /**
     * Adds item flags to the item.
     *
     * @param flags The flags to add.
     * @return The ItemBuilder.
     */
    public ItemBuilder addItemFlags(final List<ItemFlag> flags) {
        if (meta != null && flags != null) {
            for (final ItemFlag flag : flags) {
                meta.addItemFlags(flag);
            }
        }
        return this;
    }
    /**
     * Sets a string value in the PersistentDataContainer.
     *
     * @param key The NamespacedKey.
     * @param value The string value.
     * @return The ItemBuilder.
     */
    public ItemBuilder setPDCString(final NamespacedKey key, final String value) {
        if (pdc != null && key != null && value != null) {
            pdc.set(key, PersistentDataType.STRING, value);
        }
        return this;
    }
    /**
     * Adds an enchantment to the item.
     *
     * @param ench The enchantment.
     * @param level The level.
     * @return The ItemBuilder.
     */
    public ItemBuilder addEnchant(final Enchantment ench, final int level) {
        if (meta != null) {
            meta.addEnchant(ench, level, true);
        }
        return this;
    }
    /**
     * Builds the item.
     *
     * @return The built item.
     */
    public ItemStack build() {
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }
}
