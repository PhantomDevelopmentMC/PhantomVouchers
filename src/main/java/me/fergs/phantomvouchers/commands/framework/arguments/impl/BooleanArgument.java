package me.fergs.phantomvouchers.commands.framework.arguments.impl;

import me.fergs.phantomvouchers.commands.framework.arguments.AbstractBaseArgument;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class BooleanArgument extends AbstractBaseArgument<Boolean> {
    public BooleanArgument(String name) {
        super(name);
    }

    @Override
    public Boolean parse(CommandSender sender, String[] args, int index) throws Exception {
        String value = args[index].toLowerCase();
        if (value.equals("true") || value.equals("yes") || value.equals("on")) {
            return true;
        } else if (value.equals("false") || value.equals("no") || value.equals("off")) {
            return false;
        }
        throw new Exception("Invalid boolean value: " + args[index]);
    }

    @Override
    public List<String> getSuggestions(CommandSender sender, String partial) {
        if (suggestionProvider != null) {
            return super.getSuggestions(sender, partial);
        }
        return Arrays.asList("true", "false");
    }
}
