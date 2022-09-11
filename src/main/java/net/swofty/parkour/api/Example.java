package net.swofty.parkour.api;

import net.swofty.parkour.api.listeners.ParkourEventHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Example extends ParkourEventHandler {

    @Override
    public void onParkourStart(ParkourStartEvent e) {
        Player player = e.getPlayer();
        Location locationOfFirstCheckpoint = e.getParkour().getCheckpoints().get(0);
    }

    @Override
    public void onParkourEnd(ParkourEndEvent e) {
        Player player = e.getPlayer();
        Location locationOfFirstCheckpoint = e.getParkour().getCheckpoints().get(0);
        Long timeSpent = e.getTimeSpent();
    }

    @Override
    public void onCheckpointHit(CheckpointHitEvent e) {
        Player player = e.getPlayer();
        Location locationOfFirstCheckpoint = e.getParkour().getCheckpoints().get(0);
        Long timeSpent = e.getTimeSpent();
        Integer checkPointHit = e.getCheckpointHit();
        Location locationOfHitCheckpoint = e.getParkour().getCheckpoints().get(checkPointHit);
    }

}
