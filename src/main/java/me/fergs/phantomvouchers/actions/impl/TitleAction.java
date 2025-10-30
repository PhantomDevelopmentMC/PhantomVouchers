package me.fergs.phantomvouchers.actions.impl;

import me.fergs.phantomvouchers.actions.IAction;
import me.fergs.phantomvouchers.utils.Color;
import me.fergs.phantomvouchers.utils.MessageParser;
import org.bukkit.entity.Player;

import java.util.Map;

public class TitleAction implements IAction {
    private final String title;
    private final String subtitle;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    public TitleAction(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    @Override
    public void execute(Player player, Map<String, String> variables) {
        String parsedTitle = MessageParser.parse(title, player, variables);
        String parsedSubtitle = MessageParser.parse(subtitle, player, variables);
        player.sendTitle(Color.hex(parsedTitle), Color.hex(parsedSubtitle), fadeIn, stay, fadeOut);
    }
}
