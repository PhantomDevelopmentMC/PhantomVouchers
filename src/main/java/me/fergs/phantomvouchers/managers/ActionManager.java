package me.fergs.phantomvouchers.managers;

import me.fergs.phantomvouchers.actions.IAction;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * Manages the execution of actions for players.
 */
public final class ActionManager {
    /**
     * Executes a list of actions for the given player.
     *
     * @param player  The player for whom the actions will be executed.
     * @param actions The list of actions to execute.
     * @param variables A map of custom variables.
     */
    public void execute(Player player, List<IAction> actions, Map<String, String> variables) {
        for (IAction action : actions) {
            action.execute(player, variables);
        }
    }
}
