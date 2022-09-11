package net.swofty.parkour.plugin.listener.listeners;

import net.swofty.parkour.plugin.listener.PListener;
import net.swofty.parkour.plugin.parkour.ParkourRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitEvent extends PListener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ParkourRegistry.playerParkourCache.remove(event.getPlayer().getUniqueId());
    }
}
