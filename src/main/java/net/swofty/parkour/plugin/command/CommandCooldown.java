package net.swofty.parkour.plugin.command;

public interface CommandCooldown {
    long cooldownSeconds();

    default long getCooldown() {
        return cooldownSeconds() * 1000;
    }
}
