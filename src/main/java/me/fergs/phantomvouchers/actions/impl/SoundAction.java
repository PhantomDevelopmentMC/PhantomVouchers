package me.fergs.phantomvouchers.actions.impl;

import me.fergs.phantomvouchers.actions.IAction;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;

public class SoundAction implements IAction {
    private final Sound sound;
    private final float volume;
    private final float pitch;

    public SoundAction(String soundName, float volume, float pitch) {
        this.sound = Sound.valueOf(soundName.toUpperCase());
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void execute(Player player, Map<String, String> variables) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}
