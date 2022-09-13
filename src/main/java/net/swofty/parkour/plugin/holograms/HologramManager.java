package net.swofty.parkour.plugin.holograms;

import net.swofty.parkour.plugin.SwoftyParkour;
import net.swofty.parkour.plugin.parkour.ParkourRegistry;
import net.swofty.parkour.plugin.utilities.SUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class HologramManager {

    public static ArrayList<Hologram> toHide = new ArrayList<>();

    public static void runHologramLoop() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            toHide.forEach(s -> {
                s.hide(player, true);
            });
            toHide.clear();
        });

        ParkourRegistry.parkourRegistry.forEach(parkour -> {
            if (parkour.getStartLocation() != null) {
                toHide.add(new Hologram(
                        parkour.getStartLocation().clone().add(0.5, -0.5, 0.5),
                        SUtil.variableize(
                                SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.holograms.start")),
                                Arrays.asList(Map.entry("$NAME", parkour.getName()))))
                );
            }
            if (parkour.getCheckpoints() != null) {
                AtomicInteger x = new AtomicInteger();
                parkour.getCheckpoints().forEach(location -> {
                    x.getAndIncrement();
                    toHide.add(new Hologram(
                            location.clone().add(0.5, -0.5, 0.5),
                            SUtil.variableize(
                                    SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.holograms.checkpoint")),
                                    Arrays.asList(Map.entry("$CHECKPOINT", String.valueOf(x.get())), Map.entry("$NAME", parkour.getName()))))
                    );
                });
            }
            if (parkour.getEndLocation() != null) {
                toHide.add(new Hologram(
                        parkour.getEndLocation().clone().add(0.5, -0.5, 0.5),
                        SUtil.variableize(
                                SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.holograms.end")),
                                Arrays.asList(Map.entry("$NAME", parkour.getName()))))
                );
            }
            if (parkour.getTop() != null) {
                Location location = parkour.getTop();
                Map<UUID, Long> sortedLeaderboard2 = SwoftyParkour.getPlugin().getSql().getParkourTop(parkour);
                Map<UUID, Long> sortedLeaderboard = new HashMap<>();

                List<UUID> alKeys = new ArrayList<>(sortedLeaderboard2.keySet());
                for(UUID strKey : alKeys){
                    sortedLeaderboard.put(strKey, sortedLeaderboard2.get(strKey));
                }

                ArrayList<String> hologram = new ArrayList<>();
                hologram.addAll(
                        SUtil.variableize(
                                SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.holograms.leaderboard-top")),
                                Arrays.asList(Map.entry("$NAME", parkour.getName()))));

                for (int x = 0; x < 9; x++) {
                    if (sortedLeaderboard.size() <= x) {
                        hologram.add(SUtil.variableize(
                                SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getString("messages.parkour.holograms.leaderboard-entry")),
                                Arrays.asList(
                                        Map.entry("$NUMBER", String.valueOf(x + 1)),
                                        Map.entry("$USERNAME", "§cNone"),
                                        Map.entry("$TIME", "§cNone")
                                )));
                    } else {
                        hologram.add(SUtil.variableize(
                                SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getString("messages.parkour.holograms.leaderboard-entry")),
                                Arrays.asList(
                                        Map.entry("$NUMBER", String.valueOf(x + 1)),
                                        Map.entry("$USERNAME", Bukkit.getOfflinePlayer(sortedLeaderboard.entrySet().stream().collect(Collectors.toList()).get(x).getKey()).getName()),
                                        Map.entry("$TIME", new SimpleDateFormat("mm:ss.SSS").format(sortedLeaderboard.entrySet().stream().collect(Collectors.toList()).get(x).getValue()))
                                )));
                    }
                }

                hologram.addAll(
                        SUtil.variableize(
                                SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.holograms.leaderboard-bottom")),
                                Arrays.asList(Map.entry("$NAME", parkour.getName()))));

                toHide.add(new Hologram(location, hologram));
            }
        });

        Bukkit.getOnlinePlayers().forEach(player -> {
            toHide.forEach(s -> {
                s.show(player);
            });
        });
    }

}
