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
    public static ArrayList<Hologram> toHide2 = new ArrayList<>();

    public static void runHologramLoop() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            toHide.forEach(s -> {
                s.hide(player, true);
            });
            toHide2.forEach(s -> {
                s.hide(player, true);
            });
            toHide.clear();
            toHide2.clear();
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
                Map<UUID, Long> sortedLeaderboard = SUtil.sortByValue(new HashMap<>(SwoftyParkour.getPlugin().getSql().getParkourTop(parkour)));
                ArrayList<String> cached = new ArrayList<>();
                for (int x = 0; x < SwoftyParkour.getPlugin().getConfig().getInt("leaderboard-display-size"); x++) {
                    if (sortedLeaderboard.size() <= x) {
                        cached.add(SUtil.variableize(
                                SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getString("messages.parkour.holograms.leaderboard-entry")),
                                Arrays.asList(
                                        Map.entry("$NUMBER", String.valueOf(x + 1)),
                                        Map.entry("$USERNAME", "§cNone"),
                                        Map.entry("$TIME", "§cNone")
                                )));
                    } else {
                        cached.add(SUtil.variableize(
                                SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getString("messages.parkour.holograms.leaderboard-entry")),
                                Arrays.asList(
                                        Map.entry("$NUMBER", String.valueOf(x + 1)),
                                        Map.entry("$USERNAME", Bukkit.getOfflinePlayer(sortedLeaderboard.entrySet().stream().collect(Collectors.toList()).get(x).getKey()).getName()),
                                        Map.entry("$TIME", new SimpleDateFormat("mm:ss.SSS").format(sortedLeaderboard.entrySet().stream().collect(Collectors.toList()).get(x).getValue()))
                                )));
                    }
                }

                Bukkit.getOnlinePlayers().forEach(player -> {
                    ArrayList<String> hologram = new ArrayList<>();
                    if (SwoftyParkour.getPlugin().getSql().getPosition(player.getUniqueId(), parkour) != 0) {
                        hologram.addAll(
                                SUtil.variableize(
                                        SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.holograms.leaderboard-top")),
                                        Arrays.asList(
                                                Map.entry("$NAME", parkour.getName()),
                                                Map.entry("$PLAYERTIME", new SimpleDateFormat("mm:ss.SSS").format(SwoftyParkour.getPlugin().getSql().getTimesForPlayer(player.getUniqueId()).get(parkour)))
                                        )));
                    } else {
                        hologram.addAll(
                                SUtil.variableize(
                                        SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.holograms.leaderboard-top")),
                                        Arrays.asList(
                                                Map.entry("$NAME", parkour.getName()),
                                                Map.entry("$PLAYERTIME", "§cNever completed course")
                                        )));
                    }

                    hologram.addAll(cached);

                    if (SwoftyParkour.getPlugin().getSql().getPosition(player.getUniqueId(), parkour) != 0) {
                        hologram.addAll(
                                SUtil.variableize(
                                        SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.holograms.leaderboard-bottom")),
                                        Arrays.asList(
                                                Map.entry("$NAME", parkour.getName()),
                                                Map.entry("$PLAYERTIME", new SimpleDateFormat("mm:ss.SSS").format(SwoftyParkour.getPlugin().getSql().getTimesForPlayer(player.getUniqueId()).get(parkour)))
                                        )));
                    } else {
                        hologram.addAll(
                                SUtil.variableize(
                                        SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.holograms.leaderboard-bottom")),
                                        Arrays.asList(
                                                Map.entry("$NAME", parkour.getName()),
                                                Map.entry("$PLAYERTIME", "§cNever completed course")
                                        )));
                    }

                    Hologram holo = new Hologram(location, hologram);
                    holo.show(player);
                    toHide2.add(holo);
                });
            }
        });

        Bukkit.getOnlinePlayers().forEach(player -> {
            toHide.forEach(s -> {
                s.show(player);
            });
        });
    }

}
