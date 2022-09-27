package net.swofty.parkour.plugin.listener.listeners;

import net.swofty.parkour.plugin.SwoftyParkour;
import net.swofty.parkour.plugin.listener.PListener;
import net.swofty.parkour.plugin.parkour.ParkourRegistry;
import org.bukkit.event.EventHandler;

public class BlockBreakEvent extends PListener {

    @EventHandler
    public void onBreak(org.bukkit.event.block.BlockBreakEvent event) {
        ParkourRegistry.parkourRegistry.forEach(parkour -> {
            if (parkour.getStartLocation() != null) {
                if (parkour.getStartLocation().distance(event.getBlock().getLocation()) < 0.5) {
                    event.setCancelled(true);
                    return;
                }
            }

            if (parkour.getEndLocation() != null) {
                if (parkour.getEndLocation().distance(event.getBlock().getLocation()) < 0.5) {
                    event.setCancelled(true);
                    return;
                }
            }

            if (parkour.getCheckpoints() != null) {
                parkour.getCheckpoints().forEach(location -> {
                    if (location.distance(event.getBlock().getLocation()) < 0.5) {
                        event.setCancelled(true);
                    }
                });
            }
        });
    }
}
