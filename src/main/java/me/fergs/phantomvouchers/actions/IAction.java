package me.fergs.phantomvouchers.actions;

import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Represents an action that can be executed for a player.
 */
public interface IAction {
    boolean execute(Player player, Map<String, String> variables);
}
