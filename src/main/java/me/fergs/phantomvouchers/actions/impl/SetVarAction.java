package me.fergs.phantomvouchers.actions.impl;

import me.fergs.phantomvouchers.actions.IAction;
import me.fergs.phantomvouchers.utils.MessageParser;
import org.bukkit.entity.Player;

import java.util.Map;

public class SetVarAction implements IAction {
    private final String varName;
    private final String valueExpression;

    public SetVarAction(String varName, String valueExpression) {
        this.varName = varName;
        this.valueExpression = valueExpression;
    }

    @Override
    public boolean execute(Player player, Map<String, String> variables) {
        String parsedValue = MessageParser.parse(valueExpression, player, variables);
        variables.put(varName, parsedValue);
        return true;
    }
}
