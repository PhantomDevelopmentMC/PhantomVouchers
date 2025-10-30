package me.fergs.phantomvouchers.commands.framework.providers;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface SuggestionProvider {
    CompletableFuture<String[]> getSuggestions(SuggestionInfo info);
}
