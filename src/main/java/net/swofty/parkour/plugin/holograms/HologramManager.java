package net.swofty.parkour.plugin.holograms;

import net.swofty.parkour.plugin.SwoftyParkour;
import net.swofty.parkour.plugin.parkour.ParkourRegistry;
import net.swofty.parkour.plugin.utilities.SUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class HologramManager {

    public static ArrayList<Hologram> toHide = new ArrayList<>();

    public static void runHologramLoop() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            toHide.forEach(s -> {
                s.hide(player);
            });
            toHide.clear();
        });

        ParkourRegistry.parkourRegistry.forEach(parkour -> {
            toHide.add(new Hologram(
                    parkour.getStartLocation().clone().add(0, 1.5, 0),
                    SUtil.variableize(
                            SUtil.color(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.holograms.start")),
                            Arrays.asList(Map.entry("$NAME", parkour.getName())))
                    )
            );
            AtomicInteger x = new AtomicInteger();
            parkour.getCheckpoints().forEach(location -> {
                x.getAndIncrement();
                toHide.add(new Hologram(
                        location.clone().add(0, 1.5, 0),
                        SUtil.variableize(
                                SUtil.color(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.holograms.checkpoint")),
                                Arrays.asList(Map.entry("$CHECKPOINT", String.valueOf(x.get()))))
                        )
                );
            });
            toHide.add(new Hologram(
                    parkour.getEndLocation().clone().add(0, 1.5, 0),
                    SUtil.variableize(
                            SUtil.color(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.holograms.end")),
                            Arrays.asList(Map.entry("$NAME", parkour.getName())))
                    )
            );
        });

        Bukkit.getOnlinePlayers().forEach(player -> {
            toHide.forEach(s -> {
                s.show(player);
            });
        });
    }

}
