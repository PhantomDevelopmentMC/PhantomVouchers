package me.fergs.phantomvouchers.commands.framework.arguments;


import me.fergs.phantomvouchers.commands.framework.providers.SuggestionInfo;
import me.fergs.phantomvouchers.commands.framework.providers.SuggestionProvider;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractBaseArgument<T> implements IArgument<T> {
    protected final String name;
    protected SuggestionProvider suggestionProvider;

    public AbstractBaseArgument(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getSuggestions(CommandSender sender, String partial) {
        if (suggestionProvider != null) {
            try {
                String[] suggestions = suggestionProvider.getSuggestions(
                        new SuggestionInfo(sender, new String[]{partial})
                ).get();
                return Arrays.stream(suggestions)
                        .filter(s -> s.toLowerCase().startsWith(partial.toLowerCase()))
                        .collect(Collectors.toList());
            } catch (Exception e) {
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }

    @Override
    public IArgument<T> replaceSuggestions(SuggestionProvider provider) {
        this.suggestionProvider = provider;
        return this;
    }
}
