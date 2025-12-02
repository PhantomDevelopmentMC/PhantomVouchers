package me.fergs.phantomvouchers.actions.impl;

import me.fergs.phantomvouchers.actions.IAction;
import me.fergs.phantomvouchers.utils.MessageParser;
import me.fergs.phantomvouchers.utils.RequirementParser;
import org.bukkit.entity.Player;

import java.util.Map;

public final class RequirementAction implements IAction {
    private final String requirement;

    public RequirementAction(String requirement) {
        this.requirement = requirement;
    }

    @Override
    public boolean execute(Player player, Map<String, String> variables) {
        String parsed = MessageParser.parse(requirement, player, variables);
        return RequirementParser.checkRequirement(parsed, player);
    }
}
