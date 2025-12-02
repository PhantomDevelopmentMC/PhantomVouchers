package me.fergs.phantomvouchers.utils;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.parser.ParseException;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public final class RequirementParser {
    static ExpressionConfiguration defaultConfig = ExpressionConfiguration.defaultConfiguration();

    /**
     * Check if a player meets a requirement
     *
     * @param requirement the requirement to check
     * @param player      the player to check
     * @return true if the player meets the requirement, false otherwise
     */
    public static boolean checkRequirement(String requirement, Player player) {
        if (requirement == null || requirement.isEmpty()) {
            return false;
        } else {
            try {
                String exprString = PlaceholderAPI.setPlaceholders(player, requirement);
                exprString = exprString.replace("yes", "true").replace("no", "false");
                Expression exp = new Expression(exprString, defaultConfig);
                return exp.evaluate().getBooleanValue();
            } catch (ParseException | EvaluationException var3) {
                return false;
            }
        }
    }
}
