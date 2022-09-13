package net.swofty.parkour.plugin.listener.listeners;

import net.swofty.parkour.plugin.SwoftyParkour;
import net.swofty.parkour.plugin.listener.PListener;
import net.swofty.parkour.plugin.parkour.ParkourRegistry;
import net.swofty.parkour.plugin.utilities.SUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveEvent extends PListener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().isFlying() && ParkourRegistry.playerParkourCache.containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getString("messages.parkour.flew-during-course")));
            ParkourRegistry.playerParkourCache.remove(event.getPlayer().getUniqueId());
            event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }
}
