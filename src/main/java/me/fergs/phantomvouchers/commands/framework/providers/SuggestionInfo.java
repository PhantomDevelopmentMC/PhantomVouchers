package me.fergs.phantomvouchers.commands.framework.providers;

import org.bukkit.command.CommandSender;

public final class SuggestionInfo {
    private final CommandSender sender;
    private final String[] args;

    public SuggestionInfo(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = args;
    }

    public CommandSender sender() { return sender; }
    public String[] args() { return args; }
}
