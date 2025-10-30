package me.fergs.phantomvouchers.commands.framework;

import me.fergs.phantomvouchers.commands.framework.arguments.CommandArguments;
import me.fergs.phantomvouchers.commands.framework.arguments.IArgument;
import me.fergs.phantomvouchers.commands.framework.arguments.impl.ArgumentHolder;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Lightweight command framework that works across Spigot and Paper
 */
public class PhantomCommandFramework {

    public static class CommandBuilder {
        private final String name;
        private final List<String> aliases = new ArrayList<>();
        private String permission;
        private final List<ArgumentHolder> arguments = new ArrayList<>();
        private final Map<String, CommandBuilder> subcommands = new HashMap<>();
        private BiConsumer<CommandSender, CommandArguments> executor;
        private BiConsumer<Player, CommandArguments> playerExecutor;
        private Predicate<CommandSender> requirement;

        public CommandBuilder(String name) {
            this.name = name;
        }

        public CommandBuilder withAliasList(List<String> aliases) {
            this.aliases.addAll(aliases);
            return this;
        }

        public CommandBuilder withPermission(String permission) {
            this.permission = permission;
            return this;
        }

        public CommandBuilder withArguments(IArgument<?>... args) {
            for (IArgument<?> arg : args) {
                this.arguments.add(new ArgumentHolder(arg));
            }
            return this;
        }

        public CommandBuilder withOptionalArguments(IArgument<?>... args) {
            for (IArgument<?> arg : args) {
                ArgumentHolder holder = new ArgumentHolder(arg);
                holder.optional = true;
                this.arguments.add(holder);
            }
            return this;
        }

        public CommandBuilder withSubcommand(CommandBuilder subcommand) {
            this.subcommands.put(subcommand.name.toLowerCase(), subcommand);
            return this;
        }

        public CommandBuilder executes(BiConsumer<CommandSender, CommandArguments> executor) {
            this.executor = executor;
            return this;
        }

        public CommandBuilder executesPlayer(BiConsumer<Player, CommandArguments> executor) {
            this.playerExecutor = executor;
            return this;
        }

        public CommandBuilder withRequirement(Predicate<CommandSender> requirement) {
            this.requirement = requirement;
            return this;
        }

        private static JavaPlugin pluginInstance;

        public static void setPlugin(JavaPlugin plugin) {
            pluginInstance = plugin;
        }

        private static JavaPlugin getPluginInstance() {
            if (pluginInstance == null) {
                throw new IllegalStateException("Plugin instance not set! Call CommandBuilder.setPlugin(plugin) in onEnable()");
            }
            return pluginInstance;
        }

        public void register() {
            register(getPluginInstance());
        }

        public void register(JavaPlugin plugin) {
            try {
                PluginCommand command = plugin.getCommand(name);
                if (command == null) {
                    command = createPluginCommand(name, plugin);
                }

                CommandExecutor cmdExecutor = new BukkitCommandExecutor(this);
                TabCompleter tabCompleter = new BukkitTabCompleter(this);

                command.setExecutor(cmdExecutor);
                command.setTabCompleter(tabCompleter);

                if (!aliases.isEmpty()) {
                    command.setAliases(aliases);
                    for (String alias : aliases) {
                        PluginCommand aliasCommand = createPluginCommand(alias, plugin);
                        aliasCommand.setExecutor(cmdExecutor);
                        aliasCommand.setTabCompleter(tabCompleter);
                    }
                }

            } catch (Exception e) {
                plugin.getLogger().severe("Failed to register command: " + name);
                e.printStackTrace();
            }
        }

        private PluginCommand createPluginCommand(String name, JavaPlugin plugin) throws Exception {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            PluginCommand command = constructor.newInstance(name, plugin);

            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
            commandMap.register(plugin.getName(), command);

            return command;
        }
    }

    private static class BukkitCommandExecutor implements CommandExecutor {
        private final CommandBuilder command;

        public BukkitCommandExecutor(CommandBuilder command) {
            this.command = command;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            return executeCommand(sender, command, args, 0);
        }

        private boolean executeCommand(CommandSender sender, CommandBuilder cmd, String[] args, int offset) {
            if (cmd.requirement != null && !cmd.requirement.test(sender)) {
                return true;
            }

            if (cmd.permission != null && !cmd.permission.isEmpty() && !sender.hasPermission(cmd.permission)) {
                sender.sendMessage("§cYou don't have permission to use this command.");
                return true;
            }

            if (offset < args.length) {
                String subName = args[offset].toLowerCase();
                CommandBuilder sub = cmd.subcommands.get(subName);
                if (sub != null) {
                    return executeCommand(sender, sub, args, offset + 1);
                }
            }

            CommandArguments parsedArgs = new CommandArguments();
            int argIndex = offset;

            for (ArgumentHolder argHolder : cmd.arguments) {
                if (argIndex >= args.length) {
                    if (!argHolder.optional) {
                        sender.sendMessage("§cMissing required argument: " + argHolder.IArgument.getName());
                        return true;
                    }
                    break;
                }

                try {
                    Object value = argHolder.IArgument.parse(sender, args, argIndex);
                    parsedArgs.put(argHolder.IArgument.getName(), value);
                    argIndex += argHolder.IArgument.getConsumedArgs();
                } catch (Exception e) {
                    sender.sendMessage("§cInvalid argument: " + e.getMessage());
                    return true;
                }
            }

            if (cmd.playerExecutor != null) {
                if (sender instanceof Player player) {
                    cmd.playerExecutor.accept(player, parsedArgs);
                } else {
                    sender.sendMessage("§cOnly players can use this command.");
                }
            } else if (cmd.executor != null) {
                cmd.executor.accept(sender, parsedArgs);
            }

            return true;
        }
    }

    private static class BukkitTabCompleter implements TabCompleter {
        private final CommandBuilder command;

        public BukkitTabCompleter(CommandBuilder command) {
            this.command = command;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
            return getCompletions(sender, command, args, 0);
        }

        private List<String> getCompletions(CommandSender sender, CommandBuilder cmd, String[] args, int offset) {
            if (cmd.permission != null && !cmd.permission.isEmpty() && !sender.hasPermission(cmd.permission)) {
                return Collections.emptyList();
            }

            if (cmd.requirement != null && !cmd.requirement.test(sender)) {
                return Collections.emptyList();
            }

            List<String> completions = new ArrayList<>();

            if (offset < args.length - 1) {
                String subName = args[offset].toLowerCase();
                CommandBuilder sub = cmd.subcommands.get(subName);
                if (sub != null) {
                    return getCompletions(sender, sub, args, offset + 1);
                }
            }

            if (offset == args.length - 1 && !cmd.subcommands.isEmpty()) {
                String partial = args[offset].toLowerCase();
                for (String subName : cmd.subcommands.keySet()) {
                    if (subName.startsWith(partial)) {
                        completions.add(subName);
                    }
                }
                if (!completions.isEmpty()) {
                    return completions;
                }
            }

            int argIndex = offset;
            for (ArgumentHolder argHolder : cmd.arguments) {
                if (argIndex == args.length - 1) {
                    completions.addAll(argHolder.IArgument.getSuggestions(sender, args[argIndex]));
                    break;
                }
                argIndex += argHolder.IArgument.getConsumedArgs();
            }

            return completions;
        }
    }
}