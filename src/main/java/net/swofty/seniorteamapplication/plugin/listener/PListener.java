package net.swofty.parkour.plugin.listener;

import net.swofty.parkour.plugin.SwoftyParkour;
import org.bukkit.event.Listener;

public class PListener implements Listener {
    private static int amount = 0;

    protected SwoftyParkour plugin;

    protected PListener() {
        this.plugin = SwoftyParkour.getPlugin();
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
        amount++;
    }

    public static int getAmount() {
        return amount;
    }
}