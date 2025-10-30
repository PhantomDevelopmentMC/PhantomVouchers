package me.fergs.phantomvouchers.commands.framework.arguments.impl;

import me.fergs.phantomvouchers.commands.framework.arguments.AbstractBaseArgument;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class IntegerArgument extends AbstractBaseArgument<Integer> {
    private final int min;
    private final int max;

    public IntegerArgument(String name) {
        this(name, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public IntegerArgument(String name, int min, int max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    @Override
    public Integer parse(CommandSender sender, String[] args, int index) throws Exception {
        try {
            int value = Integer.parseInt(args[index]);
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

        List<String> suggestions = new ArrayList<>();
        try {
            int num = Integer.parseInt(partial);
            if (num >= min && num <= max) {
                suggestions.add(partial);
            }
        } catch (NumberFormatException ignored) {
            if (partial.isEmpty()) {
                suggestions.add(String.valueOf(min));
                suggestions.add(String.valueOf(max));
            }
        }
        return suggestions;
    }
}
