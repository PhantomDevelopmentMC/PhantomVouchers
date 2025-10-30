package me.fergs.phantomvouchers.commands.framework.arguments.impl;


import me.fergs.phantomvouchers.commands.framework.arguments.AbstractBaseArgument;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class DoubleArgument extends AbstractBaseArgument<Double> {
    private final double min;
    private final double max;

    public DoubleArgument(String name) {
        this(name, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public DoubleArgument(String name, double min, double max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    @Override
    public Double parse(CommandSender sender, String[] args, int index) throws Exception {
        try {
            double value = Double.parseDouble(args[index]);
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
