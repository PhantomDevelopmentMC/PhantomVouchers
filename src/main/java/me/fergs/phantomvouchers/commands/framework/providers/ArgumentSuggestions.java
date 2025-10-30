package me.fergs.phantomvouchers.commands.framework.providers;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ArgumentSuggestions {
    public static SuggestionProvider strings(String... suggestions) {
        return info -> CompletableFuture.completedFuture(suggestions);
    }

    public static SuggestionProvider stringsAsync(Function<SuggestionInfo, CompletableFuture<String[]>> provider) {
        return provider::apply;
    }
}
