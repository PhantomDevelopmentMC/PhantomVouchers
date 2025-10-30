package me.fergs.phantomvouchers.commands.framework.arguments;


import me.fergs.phantomvouchers.commands.framework.providers.SuggestionProvider;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface IArgument<T> {
    String getName();
    T parse(CommandSender sender, String[] args, int index) throws Exception;
    List<String> getSuggestions(CommandSender sender, String partial);
    default int getConsumedArgs() { return 1; }
    IArgument<T> replaceSuggestions(SuggestionProvider provider);
}
