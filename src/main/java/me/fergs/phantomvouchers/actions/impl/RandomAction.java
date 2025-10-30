package me.fergs.phantomvouchers.actions.impl;

import me.fergs.phantomvouchers.actions.IAction;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomAction implements IAction {
    private final List<WeightedAction> options;
    private final Random random = new Random();

    public RandomAction(List<WeightedAction> options) {
        this.options = options;
    }

    @Override
    public void execute(Player player, Map<String, String> variables) {
        int totalWeight = options.stream().mapToInt(WeightedAction::getChance).sum();
        int randomValue = random.nextInt(totalWeight);

        int cumulative = 0;
        for (WeightedAction option : options) {
            cumulative += option.getChance();
            if (randomValue < cumulative) {
                for (IAction action : option.getActions()) {
                    action.execute(player, variables);
                }
                break;
            }
        }
    }

    public static class WeightedAction {
        private final int chance;
        private final List<IAction> actions;

        public WeightedAction(int chance, List<IAction> actions) {
            this.chance = chance;
            this.actions = actions;
        }

        public int getChance() {
            return chance;
        }

        public List<IAction> getActions() {
            return actions;
        }
    }
}
