package me.fergs.phantomvouchers;

import lombok.Getter;
import me.fergs.phantomvouchers.commands.AdminCommands;
import me.fergs.phantomvouchers.commands.framework.PhantomCommandFramework;
import me.fergs.phantomvouchers.configuration.ConfigurationManager;
import me.fergs.phantomvouchers.listeners.PlayerInteractListener;
import me.fergs.phantomvouchers.managers.ActionManager;
import me.fergs.phantomvouchers.managers.MessageManager;
import me.fergs.phantomvouchers.managers.VoucherManager;
import me.fergs.phantomvouchers.utils.ConsoleUtil;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Unmodifiable;


@Getter
public final class PhantomVouchers extends JavaPlugin {
    private ConfigurationManager<PhantomVouchers> configurationManager;
    private MessageManager messageManager;
    private VoucherManager voucherManager;
    private ActionManager actionManager;
    private PlayerInteractListener playerInteractListener;
    private boolean isPAPISupported = false;
    private boolean isWorldGuardSupported = false;

    @Override
    public void onEnable() {
        ConsoleUtil.printAsciiArt();

        this.configurationManager = new ConfigurationManager<>(this);

        this.configurationManager.loadConfigs(
                "settings",
                "commands",
                "messages"
        );

        checkPlugins();

        this.messageManager = new MessageManager(this.configurationManager);
        this.actionManager = new ActionManager();
        this.voucherManager = new VoucherManager(this);

        PhantomCommandFramework.CommandBuilder.setPlugin(this);

        AdminCommands.build(this).register();

        this.playerInteractListener = new  PlayerInteractListener(this, actionManager);
    }

    @Override
    public void onDisable() {

    }

    @Unmodifiable
    public static PhantomVouchers getInstance() {
        return JavaPlugin.getPlugin(PhantomVouchers.class);
    }

    public void checkPlugins() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            isPAPISupported = true;
        }

        if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            isWorldGuardSupported = true;
        }
    }
}
