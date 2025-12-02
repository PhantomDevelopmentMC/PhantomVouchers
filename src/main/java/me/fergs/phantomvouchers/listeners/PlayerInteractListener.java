package me.fergs.phantomvouchers.listeners;

import lombok.Getter;
import me.fergs.phantomvouchers.PhantomVouchers;
import me.fergs.phantomvouchers.managers.ActionManager;
import me.fergs.phantomvouchers.objects.VoucherItem;
import me.fergs.phantomvouchers.utils.RegionUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class PlayerInteractListener implements Listener {
    private final PhantomVouchers plugin;
    private final ActionManager actionManager;
    private final NamespacedKey voucherKey;
    private final Map<String, Long> lastInteractionTimes = new HashMap<>();
    private long interactionCooldown;
    private long confirmationWindow;
    private final Map<String, Long> pendingConfirmations = new HashMap<>();

    public PlayerInteractListener(final PhantomVouchers plugin, final ActionManager actionManager) {
        this.plugin = plugin;
        this.actionManager = actionManager;
        this.voucherKey = new NamespacedKey(plugin, "voucher_id");
        this.interactionCooldown = plugin.getConfigurationManager().getConfig("settings").getLong("settings.interaction-cooldown", 500);
        this.confirmationWindow = plugin.getConfigurationManager().getConfig("settings").getLong("settings.confirmation-window", 3000);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = event.getItem();
        final EquipmentSlot hand = event.getHand();

        if (item == null || !event.getAction().isRightClick() || hand != EquipmentSlot.HAND) {
            return;
        }

        final ItemMeta meta = item.getItemMeta();
        final PersistentDataContainer pdc = meta.getPersistentDataContainer();
        final String voucherId = pdc.get(voucherKey, PersistentDataType.STRING);

        if (voucherId == null) {
            return;
        }

        final String playerUUID = player.getUniqueId().toString();
        final long currentTime = System.currentTimeMillis();

        final Long lastTime = lastInteractionTimes.get(playerUUID);
        if (lastTime != null && currentTime - lastTime < interactionCooldown) {
            return;
        }
        lastInteractionTimes.put(playerUUID, currentTime);

        final VoucherItem voucher = plugin.getVoucherManager().getVoucher(voucherId);

        if (voucher == null) {
            return;
        }

        event.setCancelled(true);
        event.setUseItemInHand(Event.Result.DENY);

        final VoucherItem.VoucherSettings settings = voucher.getSettings();

        final Map<String, String> variables = new HashMap<>();

        if (player.isSneaking()) {
            if (canRedeem(player, voucher)) {
                int amount = item.getAmount();
                for (int i = 0; i < amount; i++) {
                    Map<String, String> executionVariables = new HashMap<>(variables);
                    actionManager.execute(player, voucher.getRedeemActions(), executionVariables);
                }
                item.setAmount(0);
            }
        } else if (Boolean.TRUE.equals(settings.getAdditionalConfirmation())) {
            final String key = playerUUID + ":" + voucherId;
            final Long timestamp = pendingConfirmations.get(key);

            if (timestamp != null && currentTime - timestamp < confirmationWindow) {
                if (canRedeem(player, voucher)) {
                    actionManager.execute(player, voucher.getRedeemActions(), variables);
                    item.setAmount(item.getAmount() - 1);
                }
                pendingConfirmations.remove(key);
            } else {
                plugin.getMessageManager().sendMessage(player, "CONFIRMATION", "%voucher-display%", voucher.getItem().getDisplayName());
                pendingConfirmations.put(key, currentTime);
            }
        } else {
            if (canRedeem(player, voucher)) {
                actionManager.execute(player, voucher.getRedeemActions(), variables);
                item.setAmount(item.getAmount() - 1);
            }
        }
    }

    private boolean canRedeem(final Player player, final VoucherItem voucher) {
        final VoucherItem.VoucherSettings settings = voucher.getSettings();

        if (settings.getBlacklistedWorlds().contains(player.getWorld().getName())) {
            plugin.getMessageManager().sendMessage(player, "BLACKLISTED_WORLD");
            return false;
        }

        if (plugin.isWorldGuardSupported() && !settings.getBlacklistedRegions().isEmpty()) {
            if (RegionUtils.isPlayerInBlacklistedRegion(player, settings.getBlacklistedRegions())) {
                plugin.getMessageManager().sendMessage(player, "BLACKLISTED_REGION");
                return false;
            }
        }

        if (settings.getPermissionRequired() != null && !player.hasPermission(settings.getPermissionRequired())) {
            plugin.getMessageManager().sendMessage(player, "NO_PERMISSION");
            return false;
        }

        return true;
    }

    public void reload() {
        this.interactionCooldown = plugin.getConfigurationManager().getConfig("settings").getLong("settings.interaction-cooldown", 500);
        this.confirmationWindow = plugin.getConfigurationManager().getConfig("settings").getLong("settings.confirmation-window", 3000);
    }
}
