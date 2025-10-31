package me.fergs.phantomvouchers.actions.impl;

import me.fergs.phantomvouchers.actions.IAction;
import me.fergs.phantomvouchers.utils.MessageParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class CommandAction implements IAction {
    private final String command;

    public CommandAction(String command) {
        this.command = command;
    }

    @Override
    public boolean execute(Player player, Map<String, String> variables) {
        String parsed = MessageParser.parse(command, player, variables);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
        return true;
    }
}
