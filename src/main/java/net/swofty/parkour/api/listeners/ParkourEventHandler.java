package net.swofty.parkour.api.listeners;

import net.swofty.parkour.plugin.parkour.Parkour;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class ParkourEventHandler {

    public void onParkourStart(ParkourStartEvent e) {}
    public void onCheckpointHit(CheckpointHitEvent e) {}
    public void onParkourEnd(ParkourEndEvent e) {}
    
    public static class ParkourStartEvent extends Event {
        private Player player;
        private Parkour parkour;

        public ParkourStartEvent(Player player, Parkour parkour) {
            this.player = player;
            this.parkour = parkour;
        }

        public Player getPlayer() {
            return player;
        }
        public Parkour getParkour() {
            return parkour;
        }

        @Override
        public HandlerList getHandlers() {
            return new HandlerList();
        }
    }

    public static class CheckpointHitEvent extends Event {
        private Player player;
        private Parkour parkour;
        private Integer checkpointHit;
        private Long timeSpent;

        public CheckpointHitEvent(Player player, Parkour parkour, Integer checkpointHit, Long timeSpent) {
            this.player = player;
            this.parkour = parkour;
            this.checkpointHit = checkpointHit;
            this.timeSpent = timeSpent;
        }

        public Player getPlayer() {
            return player;
        }
        public Parkour getParkour() {
            return parkour;
        }
        public Integer getCheckpointHit() {
            return checkpointHit;
        }
        public Long getTimeSpent() {
            return timeSpent;
        }

        @Override
        public HandlerList getHandlers() {
            return new HandlerList();
        }
    }
    public static class ParkourEndEvent extends Event {
        private Player player;
        private Parkour parkour;
        private Long timeSpent;

        public ParkourEndEvent(Player player, Parkour parkour, Long timeSpent) {
            this.player = player;
            this.parkour = parkour;
            this.timeSpent = timeSpent;
        }

        public Player getPlayer() {
            return player;
        }
        public Parkour getParkour() {
            return parkour;
        }
        public Long getTimeSpent() {
            return timeSpent;
        }

        @Override
        public HandlerList getHandlers() {
            return new HandlerList();
        }
    }
}
