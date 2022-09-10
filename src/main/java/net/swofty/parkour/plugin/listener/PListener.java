package net.swofty.parkour.plugin.listener;

import net.swofty.parkour.plugin.SwoftyParkour;
import org.bukkit.event.Listener;

public class PListener implements Listener {
    private static int amount = 0;

    protected PListener() {
        SwoftyParkour.getPlugin().getServer().getPluginManager().registerEvents(this, SwoftyParkour.getPlugin());
        amount++;
    }

    public static int getAmount() {
        return amount;
    }
}