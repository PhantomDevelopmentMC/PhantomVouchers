package me.fergs.phantomvouchers.commands.framework.arguments.impl;


import me.fergs.phantomvouchers.commands.framework.arguments.AbstractBaseArgument;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class LongArgument extends AbstractBaseArgument<Long> {
    private final long min;
    private final long max;

    public LongArgument(String name) {
        this(name, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public LongArgument(String name, long min, long max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    @Override
    public Long parse(CommandSender sender, String[] args, int index) throws Exception {
        try {
            long value = Long.parseLong(args[index]);
            if (value < min || value > max) {
                throw new Exception("Number must be between " + min + " and " + max);
            }
            return value;
        } catch (NumberFormatException e) {
            throw new Exception("Invalid number: " + args[index]);
        }
    }

    @Override
    public List<String> getSuggestions(CommandSender sender, String partial) {
        if (suggestionProvider != null) {
            return super.getSuggestions(sender, partial);
        }
        return Collections.emptyList();
    }
}