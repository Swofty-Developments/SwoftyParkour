package net.swofty.parkour.plugin.listener;

import lombok.Getter;
import lombok.Setter;
import net.swofty.parkour.plugin.SwoftyParkour;
import org.bukkit.event.Listener;

public class PListener implements Listener {
    private static int amount = 0;
    @Getter
    @Setter
    public SwoftyParkour plugin;

    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    protected PListener() {
        amount++;
    }

    public static int getAmount() {
        return amount;
    }
}