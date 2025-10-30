package me.fergs.phantomvouchers.commands.framework.arguments.impl;

import me.fergs.phantomvouchers.commands.framework.arguments.AbstractBaseArgument;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerArgument extends AbstractBaseArgument<Player> {
    public PlayerArgument(String name) {
        super(name);
    }

    @Override
    public Player parse(CommandSender sender, String[] args, int index) throws Exception {
        String playerName = args[index];
        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) {
            throw new Exception("Player not found: " + playerName);
        }
        return player;
    }

    @Override
    public List<String> getSuggestions(CommandSender sender, String partial) {
        if (suggestionProvider != null) {
            return super.getSuggestions(sender, partial);
        }
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(partial.toLowerCase()))
                .collect(Collectors.toList());
    }
}
