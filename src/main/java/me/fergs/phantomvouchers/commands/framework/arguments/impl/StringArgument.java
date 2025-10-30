package me.fergs.phantomvouchers.commands.framework.arguments.impl;

import me.fergs.phantomvouchers.commands.framework.arguments.AbstractBaseArgument;
import org.bukkit.command.CommandSender;

public class StringArgument extends AbstractBaseArgument<String> {
    public StringArgument(String name) {
        super(name);
    }

    @Override
    public String parse(CommandSender sender, String[] args, int index) {
        return args[index];
    }
}
