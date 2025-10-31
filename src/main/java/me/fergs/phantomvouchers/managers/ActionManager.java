package me.fergs.phantomvouchers.managers;

import me.fergs.phantomvouchers.actions.IAction;
import me.fergs.phantomvouchers.actions.impl.RandomAction;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
        final List<RandomAction.WeightedAction> allOptions = new ArrayList<>();
        final List<IAction> nonRandomActions = new ArrayList<>();

        for (final IAction action : actions) {
            if (action instanceof RandomAction) {
                // needs to be casted to access getOptions()
                allOptions.addAll(((RandomAction) action).getOptions());
            } else {
                nonRandomActions.add(action);
            }
        }

        for (final IAction action : nonRandomActions) {
            action.execute(player, variables);
        }

        if (!allOptions.isEmpty()) {
            boolean executed = false;
            final Random random = new Random();
            while (!executed) {
                int total = allOptions.stream().mapToInt(RandomAction.WeightedAction::getChance).sum();
                int rand = random.nextInt(total);
                int cum = 0;
                for (final RandomAction.WeightedAction wa : allOptions) {
                    cum += wa.getChance();
                    if (rand < cum) {
                        for (final IAction sub : wa.getActions()) {
                            sub.execute(player, variables);
                        }
                        executed = true;
                        break;
                    }
                }
            }
        }
    }
}
