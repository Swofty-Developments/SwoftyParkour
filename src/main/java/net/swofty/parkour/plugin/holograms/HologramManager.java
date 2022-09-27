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

    public static ArrayList<Hologram> globalHologramCache = new ArrayList<>();
    public static ArrayList<Hologram> playerSpecificHologramCache = new ArrayList<>();

    public static void runHologramLoop(SwoftyParkour plugin) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            globalHologramCache.forEach(s -> {
                s.hide(player, true);
            });
            playerSpecificHologramCache.forEach(s -> {
                s.hide(player, true);
            });
            globalHologramCache.clear();
            playerSpecificHologramCache.clear();
        });

        ParkourRegistry.parkourRegistry.forEach(parkour -> {
            if (parkour.getStartLocation() != null) {
                globalHologramCache.add(new Hologram(
                        parkour.getStartLocation().clone().add(0.5, -0.5, 0.5),
                        SUtil.variableize(
                                SUtil.translateColorWords(plugin.getMessages().getStringList("messages.parkour.holograms.start")),
                                Arrays.asList(Map.entry("$NAME", parkour.getName()))), plugin)
                );
            }
            if (parkour.getCheckpoints() != null) {
                AtomicInteger x = new AtomicInteger();
                parkour.getCheckpoints().forEach(location -> {
                    x.getAndIncrement();
                    globalHologramCache.add(new Hologram(
                            location.clone().add(0.5, -0.5, 0.5),
                            SUtil.variableize(
                                    SUtil.translateColorWords(plugin.getMessages().getStringList("messages.parkour.holograms.checkpoint")),
                                    Arrays.asList(Map.entry("$CHECKPOINT", String.valueOf(x.get())), Map.entry("$NAME", parkour.getName()))), plugin)
                    );
                });
            }
            if (parkour.getEndLocation() != null) {
                globalHologramCache.add(new Hologram(
                        parkour.getEndLocation().clone().add(0.5, -0.5, 0.5),
                        SUtil.variableize(
                                SUtil.translateColorWords(plugin.getMessages().getStringList("messages.parkour.holograms.end")),
                                Arrays.asList(Map.entry("$NAME", parkour.getName()))), plugin)
                );
            }
            if (parkour.getTop() != null) {
                Location location = parkour.getTop();
                Map<UUID, Long> sortedLeaderboard = SUtil.sortByValue(new HashMap<>(plugin.getSql().getParkourTop(parkour, plugin)));
                ArrayList<String> cached = new ArrayList<>();
                for (int x = 0; x < plugin.getConfig().getInt("leaderboard-display-size"); x++) {
                    if (sortedLeaderboard.size() <= x) {
                        cached.add(SUtil.variableize(
                                SUtil.translateColorWords(plugin.getMessages().getString("messages.parkour.holograms.leaderboard-entry")),
                                Arrays.asList(
                                        Map.entry("$NUMBER", String.valueOf(x + 1)),
                                        Map.entry("$USERNAME", "§cNone"),
                                        Map.entry("$TIME", "§cNone")
                                )));
                    } else {
                        cached.add(SUtil.variableize(
                                SUtil.translateColorWords(plugin.getMessages().getString("messages.parkour.holograms.leaderboard-entry")),
                                Arrays.asList(
                                        Map.entry("$NUMBER", String.valueOf(x + 1)),
                                        Map.entry("$USERNAME", Bukkit.getOfflinePlayer(sortedLeaderboard.entrySet().stream().collect(Collectors.toList()).get(x).getKey()).getName()),
                                        Map.entry("$TIME", new SimpleDateFormat("mm:ss.SSS").format(sortedLeaderboard.entrySet().stream().collect(Collectors.toList()).get(x).getValue()))
                                )));
                    }
                }

                Bukkit.getOnlinePlayers().forEach(player -> {
                    ArrayList<String> hologram = new ArrayList<>();
                    if (plugin.getSql().getPosition(player.getUniqueId(), parkour, plugin) != 0) {
                        hologram.addAll(
                                SUtil.variableize(
                                        SUtil.translateColorWords(plugin.getMessages().getStringList("messages.parkour.holograms.leaderboard-top")),
                                        Arrays.asList(
                                                Map.entry("$NAME", parkour.getName()),
                                                Map.entry("$PLAYERTIME", new SimpleDateFormat("mm:ss.SSS").format(plugin.getSql().getTimesForPlayer(player.getUniqueId(), plugin).get(parkour)))
                                        )));
                    } else {
                        hologram.addAll(
                                SUtil.variableize(
                                        SUtil.translateColorWords(plugin.getMessages().getStringList("messages.parkour.holograms.leaderboard-top")),
                                        Arrays.asList(
                                                Map.entry("$NAME", parkour.getName()),
                                                Map.entry("$PLAYERTIME", "§cNever completed course")
                                        )));
                    }

                    hologram.addAll(cached);

                    if (plugin.getSql().getPosition(player.getUniqueId(), parkour, plugin) != 0) {
                        hologram.addAll(
                                SUtil.variableize(
                                        SUtil.translateColorWords(plugin.getMessages().getStringList("messages.parkour.holograms.leaderboard-bottom")),
                                        Arrays.asList(
                                                Map.entry("$NAME", parkour.getName()),
                                                Map.entry("$PLAYERTIME", new SimpleDateFormat("mm:ss.SSS").format(plugin.getSql().getTimesForPlayer(player.getUniqueId(), plugin).get(parkour)))
                                        )));
                    } else {
                        hologram.addAll(
                                SUtil.variableize(
                                        SUtil.translateColorWords(plugin.getMessages().getStringList("messages.parkour.holograms.leaderboard-bottom")),
                                        Arrays.asList(
                                                Map.entry("$NAME", parkour.getName()),
                                                Map.entry("$PLAYERTIME", "§cNever completed course")
                                        )));
                    }

                    Hologram holo = new Hologram(location, hologram, plugin);
                    holo.show(player);
                    playerSpecificHologramCache.add(holo);
                });
            }
        });

        Bukkit.getOnlinePlayers().forEach(player -> {
            globalHologramCache.forEach(s -> {
                s.show(player);
            });
        });
    }

}
