package me.fergs.phantomvouchers.commands;

import me.fergs.phantomvouchers.PhantomVouchers;
import me.fergs.phantomvouchers.commands.framework.PhantomCommandFramework;
import me.fergs.phantomvouchers.commands.framework.arguments.CommandArguments;
import me.fergs.phantomvouchers.commands.framework.arguments.impl.IntegerArgument;
import me.fergs.phantomvouchers.commands.framework.arguments.impl.PlayerArgument;
import me.fergs.phantomvouchers.commands.framework.arguments.impl.StringArgument;
import me.fergs.phantomvouchers.commands.framework.providers.ArgumentSuggestions;
import me.fergs.phantomvouchers.inventories.VoucherPreviewInventory;
import me.fergs.phantomvouchers.utils.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public final class AdminCommands {

    public static PhantomCommandFramework.CommandBuilder build(PhantomVouchers plugin) {
        final String commandName = plugin.getConfigurationManager().getConfig("commands").getString("Commands.Admin.Command", "phantomvouchers");
        final String permission = plugin.getConfigurationManager().getConfig("commands").getString("Commands.Admin.Permission", "phantomvouchers.admin");
        final List<String> aliases = plugin.getConfigurationManager().getConfig("commands").getStringList("Commands.Admin.Aliases");

        return new PhantomCommandFramework.CommandBuilder(commandName)
                .withAliasList(aliases)
                .withPermission(permission)
                .withSubcommand(
                        new PhantomCommandFramework.CommandBuilder("give")
                                .withArguments(new PlayerArgument("player"), new StringArgument("voucher").replaceSuggestions(ArgumentSuggestions.stringsAsync(info -> {
                                    return java.util.concurrent.CompletableFuture.supplyAsync(() -> {
                                        return plugin.getVoucherManager().getVouchers().keySet().toArray(new String[0]);
                                    });
                                })))
                                .withOptionalArguments(new IntegerArgument("amount"))
                                .executes((sender, args) -> {
                                    final Player target = args.getAs("player", Player.class);
                                    final String voucherId = args.getAs("voucher", String.class);
                                    final Integer amount = args.get("amount") != null ? args.getAs("amount", Integer.class) : 1;

                                    final ItemStack voucherItem = plugin.getVoucherManager().getVoucherItem(voucherId);
                                    if (voucherItem == null) {
                                        plugin.getMessageManager().sendMessage(sender, "VOUCHER_NOT_FOUND", voucherId);
                                        return;
                                    }

                                    final ItemStack toGive = voucherItem.clone();
                                    toGive.setAmount(amount);

                                    target.getInventory().addItem(toGive);
                                    plugin.getMessageManager().sendMessage(target, "VOUCHER_RECEIVED", "%voucher-display%", plugin.getVoucherManager().getVouchers().get(voucherId).getItem().getDisplayName(), "%amount%", String.valueOf(amount));
                                })
                )
                .withSubcommand(
                        new PhantomCommandFramework.CommandBuilder("preview")
                                .executesPlayer((player, args) -> {
                                    new VoucherPreviewInventory(plugin).open(player);
                                })
                )
                .withSubcommand(
                        new PhantomCommandFramework.CommandBuilder("reload")
                                .executes((sender, args) -> {
                                    plugin.getConfigurationManager().reloadAllConfigs();
                                    plugin.getVoucherManager().reload();
                                    plugin.getPlayerInteractListener().reload();
                                    plugin.getMessageManager().sendMessage(sender, "RELOAD");
                                })
                )
                .withSubcommand(
                        new PhantomCommandFramework.CommandBuilder("list")
                                .executes((sender, args) -> {
                                    sender.sendMessage(Color.hex("&6&l&m---------------------"));
                                    sender.sendMessage(Color.hex("&e&lAvailable Vouchers:"));
                                    plugin.getVoucherManager().getVouchers().keySet().forEach(id -> sender.sendMessage(Color.hex("&f ┣ &e" + id + "&8 | " + plugin.getVoucherManager().getVouchers().get(id).getItem().getDisplayName())));
                                    sender.sendMessage(Color.hex("&6&l&m---------------------"));
                                })
                )
                .withSubcommand(
                        new PhantomCommandFramework.CommandBuilder("help")
                                .executes((sender, args) -> {
                                    sender.sendMessage(Color.hex("&6&l&m---------------------"));
                                    sender.sendMessage(Color.hex("&6&lPhantom Vouchers &e&lAdmin Commands:"));
                                    sender.sendMessage(Color.hex("&f ┣ &e/" + commandName + " &7give <player> <voucher> [amount] &f- Give vouchers to players"));
                                    sender.sendMessage(Color.hex("&f ┣ &e/" + commandName + " &7preview &f- Open voucher preview menu"));
                                    sender.sendMessage(Color.hex("&f ┣ &e/" + commandName + " &7reload &f- Reload configurations and vouchers"));
                                    sender.sendMessage(Color.hex("&f ┣ &e/" + commandName + " &7list &f- List all available vouchers"));
                                    sender.sendMessage(Color.hex("&f ┗ &e/" + commandName + " &7help &f- Show this help menu"));
                                    sender.sendMessage(Color.hex("&6&l&m---------------------"));
                                })
                );
    }
}
