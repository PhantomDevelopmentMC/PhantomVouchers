package me.fergs.phantomvouchers.actions.impl;

import me.fergs.phantomvouchers.actions.IAction;
import me.fergs.phantomvouchers.utils.Color;
import me.fergs.phantomvouchers.utils.MessageParser;
import org.bukkit.entity.Player;

import java.util.Map;

public class MessageAction implements IAction {
    private final String message;

    public MessageAction(String message) {
        this.message = message;
    }

    @Override
    public void execute(Player player, Map<String, String> variables) {
        String parsed = MessageParser.parse(message, player, variables);
        player.sendMessage(Color.hex(parsed));
    }
}
