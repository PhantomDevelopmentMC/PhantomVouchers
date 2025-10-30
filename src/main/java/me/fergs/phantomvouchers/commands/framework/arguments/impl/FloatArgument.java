package me.fergs.phantomvouchers.commands.framework.arguments.impl;

import me.fergs.phantomvouchers.commands.framework.arguments.AbstractBaseArgument;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class FloatArgument extends AbstractBaseArgument<Float> {
    private final float min;
    private final float max;

    public FloatArgument(String name) {
        this(name, Float.MIN_VALUE, Float.MAX_VALUE);
    }

    public FloatArgument(String name, float min, float max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    @Override
    public Float parse(CommandSender sender, String[] args, int index) throws Exception {
        try {
            float value = Float.parseFloat(args[index]);
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
