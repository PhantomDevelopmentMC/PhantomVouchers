package me.fergs.phantomvouchers.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.fergs.phantomvouchers.actions.IAction;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherItem {
    private String id;
    private ItemConfig item;
    private List<IAction> redeemActions;
    private VoucherSettings settings;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemConfig {
        private Material material;
        private Integer customModelData;
        private String displayName;
        private List<String> lore;
        private Map<Enchantment, Integer> enchantments;
        private List<ItemFlag> itemFlags;
        private String base64;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VoucherSettings {
        private Set<String> blacklistedWorlds;
        private Set<String> blacklistedRegions;
        private String permissionRequired;
        private Boolean additionalConfirmation;
    }
}
