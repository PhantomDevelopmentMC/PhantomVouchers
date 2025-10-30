package me.fergs.phantomvouchers.commands.framework.arguments.impl;


import me.fergs.phantomvouchers.commands.framework.arguments.AbstractBaseArgument;
import org.bukkit.command.CommandSender;

public class GreedyStringArgument extends AbstractBaseArgument<String> {
    public GreedyStringArgument(String name) {
        super(name);
    }

    @Override
    public String parse(CommandSender sender, String[] args, int index) {
        StringBuilder builder = new StringBuilder();
        for (int i = index; i < args.length; i++) {
            if (i > index) builder.append(" ");
            builder.append(args[i]);
        }
        return builder.toString();
    }

    @Override
    public int getConsumedArgs() {
        return Integer.MAX_VALUE; // Consumes all remaining
    }
}
