package net.swofty.parkour.plugin.listener.listeners;

import net.swofty.parkour.plugin.holograms.Hologram;
import net.swofty.parkour.plugin.listener.PListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Arrays;

public class JoinEvent extends PListener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage("Example 1");
        new Hologram(event.getPlayer().getLocation(), Arrays.asList("Hello")).show(event.getPlayer());
    }
}
