package me.fergs.phantomvouchers.inventories;

import me.fergs.phantomvouchers.PhantomVouchers;
import me.fergs.phantomvouchers.inventories.impl.pagination.PaginatedFastInv;
import me.fergs.phantomvouchers.objects.VoucherItem;
import me.fergs.phantomvouchers.utils.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VoucherPreviewInventory extends PaginatedFastInv implements Listener {

    private final PhantomVouchers plugin;

    public VoucherPreviewInventory(final PhantomVouchers plugin) {
        super(54, me.fergs.phantomvouchers.utils.Color.hex("&8Voucher Preview"));

        this.plugin = plugin;

        setContentSlots(IntStream.range(0, 45).boxed().collect(Collectors.toList()));

        previousPageItem(48, createNavigationItem("&4&l[&c&l!&4&l] &cPrevious Page", Material.TIPPED_ARROW, Color.RED));

        nextPageItem(50, createNavigationItem("&2&l[&a&l!&2&l] &aNext Page", Material.TIPPED_ARROW, Color.GREEN));

        for (int i = 45; i < 54; i++) {
            if (i != 48 && i != 50) {
                setItem(i, createFillerItem());
            }
        }

        loadVouchers();
    }

    private void loadVouchers() {
        for (final VoucherItem voucher : plugin.getVoucherManager().getVouchers().values()) {
            final ItemStack displayItem = createDisplayItem(voucher);
            addContent(displayItem, e -> handleVoucherClick(e, voucher.getId()));
        }
    }

    private ItemStack createDisplayItem(final VoucherItem voucher) {
        final ItemBuilder builder = new ItemBuilder(voucher.getItem().getMaterial())
                .setName(voucher.getItem().getDisplayName() != null ? voucher.getItem().getDisplayName() : voucher.getId())
                .setCustomModelData(voucher.getItem().getCustomModelData())
                .addItemFlags(voucher.getItem().getItemFlags());

        if (voucher.getItem().getBase64() != null) {
            builder.setSkullTexture(voucher.getItem().getBase64());
        }

        if (voucher.getItem().getEnchantments() != null) {
            voucher.getItem().getEnchantments().forEach(builder::addEnchant);
        }

        final List<String> lore = new ArrayList<>();
        if (voucher.getItem().getLore() != null) {
            lore.addAll(voucher.getItem().getLore());
        }
        lore.add("");
        lore.add("&6&l&m------------------------------");
        lore.add(me.fergs.phantomvouchers.utils.Color.hex("&e&lActions:"));
        lore.add(me.fergs.phantomvouchers.utils.Color.hex("&fLeft Click &8- &6Receive Voucher"));
        builder.setLore(lore);

        return builder.build();
    }

    private void handleVoucherClick(final InventoryClickEvent event, final String voucherId) {
        event.setCancelled(true);

        final Player player = (Player) event.getWhoClicked();
        final ItemStack voucherItem = plugin.getVoucherManager().getVoucherItem(voucherId);

        if (voucherItem != null) {
            player.getInventory().addItem(voucherItem.clone());
            plugin.getMessageManager().sendMessage(player, "VOUCHER_RECEIVED", "%voucher-display%", voucherItem.getItemMeta().getDisplayName(), "%amount%", "1");
            player.playSound(player, Sound.ENTITY_ITEM_PICKUP, 0.5f, 0.5f);
        }
    }

    private ItemStack createNavigationItem(final String name, final Material material, final Color arrowColor) {
        return new ItemBuilder(material)
                .setArrowColor(arrowColor)
                .setName(name)
                .addItemFlags(List.of(ItemFlag.HIDE_POTION_EFFECTS))
                .build();
    }

    private ItemStack createFillerItem() {
        return new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build();
    }

    @Override
    protected void onClick(final InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void open(final Player player) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        super.open(player);
    }

    @Override
    protected void onClose(final InventoryCloseEvent event) {
        super.onClose(event);
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getInventory().getHolder() == this) {
            handleClick(event);
        }
    }

    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent event) {
        if (event.getInventory().getHolder() == this) {
            handleOpen(event);
        }
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (event.getInventory().getHolder() == this) {
            handleClose(event);
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        if (event.getInventory().getHolder() == this) {
            handleDrag(event);
        }
    }
}
