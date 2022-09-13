package net.swofty.parkour.plugin;

import net.swofty.parkour.api.SwoftyParkourAPI;
import net.swofty.parkour.api.listeners.ListenerRegistry;
import net.swofty.parkour.api.listeners.ParkourEventHandler;
import net.swofty.parkour.plugin.parkour.Parkour;
import net.swofty.parkour.plugin.parkour.ParkourRegistry;
import net.swofty.parkour.plugin.utilities.SUtil;
import net.swofty.parkour.plugin.utilities.Sidebar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Repeater {
    private final List<BukkitTask> tasks;
    private ArrayList<UUID> delayedStart = new ArrayList<>();
    private ArrayList<UUID> delayedEnd = new ArrayList<>();
    private HashMap<UUID, Integer> delayedCheckpoint = new HashMap<>();

    public boolean isDelayedStart(Player player) {
        if (delayedStart.contains(player.getUniqueId())) {
            return true;
        }

        delayedStart.add(player.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                delayedStart.remove(player.getUniqueId());
            }
        }.runTaskLater(SwoftyParkour.getPlugin(), 15);
        return false;
    }

    public boolean isDelayedEnd(Player player) {
        if (delayedEnd.contains(player.getUniqueId())) {
            return true;
        }

        delayedEnd.add(player.getUniqueId());
        new BukkitRunnable() {
            @Override
            public void run() {
                delayedEnd.remove(player.getUniqueId());
            }
        }.runTaskLater(SwoftyParkour.getPlugin(), 15);
        return false;
    }

    public boolean isDelayedCheckpoint(Player player, Integer checkpoint) {
        if (delayedCheckpoint.containsKey(player.getUniqueId()) && delayedCheckpoint.get(player.getUniqueId()).equals(checkpoint)) {
            return true;
        }

        delayedCheckpoint.put(player.getUniqueId(), checkpoint);
        new BukkitRunnable() {
            @Override
            public void run() {
                delayedCheckpoint.remove(player.getUniqueId(), checkpoint);
            }
        }.runTaskLater(SwoftyParkour.getPlugin(), 15);
        return false;
    }

    public Repeater() {
        this.tasks = new ArrayList<>();

        this.tasks.add(new BukkitRunnable() {
            @Override
            public void run() {
                ParkourRegistry.parkourRegistry.forEach(parkour -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Location playerLoc = player.getLocation();
                        if (parkour.getStartLocation() != null) {
                            if (parkour.getStartLocation().distance(playerLoc) < 1) {
                                if (isDelayedStart(player)) return;

                                if (!parkour.getFinished()) {
                                    SUtil.sendMessageList(player, SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.parkour-not-finished"));
                                    return;
                                }

                                if (ParkourRegistry.playerParkourCache.containsKey(player.getUniqueId())) {
                                    if (ParkourRegistry.playerParkourCache.get(player.getUniqueId()).getKey() == parkour) {
                                        SUtil.sendMessageList(player, SUtil.variableize(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.restarted-parkour"), Arrays.asList(Map.entry("$NAME", parkour.getName()))));
                                    } else {
                                        SUtil.sendMessageList(player, SUtil.variableize(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.left-parkour"), Arrays.asList(Map.entry("$NAME", parkour.getName()))));
                                        SUtil.sendMessageList(player, SUtil.variableize(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.started-parkour"), Arrays.asList(Map.entry("$NAME", parkour.getName()))));
                                    }
                                } else {
                                    SUtil.sendMessageList(player, SUtil.variableize(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.started-parkour"), Arrays.asList(Map.entry("$NAME", parkour.getName()))));
                                }

                                ListenerRegistry.registeredEvents.forEach(clazz -> {
                                    try {
                                        clazz.newInstance().onParkourStart(new ParkourEventHandler.ParkourStartEvent(
                                                player,
                                                parkour
                                        ));
                                    } catch (InstantiationException | IllegalAccessException e) {
                                        throw new RuntimeException(e);
                                    }
                                });

                                ParkourRegistry.playerParkourCache.put(
                                        player.getUniqueId(),
                                        Map.entry(parkour, Map.entry(0, System.currentTimeMillis()))
                                );
                                return;
                            }
                        }

                        if (parkour.getEndLocation() != null) {
                            if (parkour.getEndLocation().distance(playerLoc) < 1) {
                                if (isDelayedEnd(player)) return;

                                if (!parkour.getFinished()) {
                                    SUtil.sendMessageList(player, SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.parkour-not-finished"));
                                    return;
                                }

                                if (ParkourRegistry.playerParkourCache.containsKey(player.getUniqueId())) {
                                    int platesHit = ParkourRegistry.playerParkourCache.get(player.getUniqueId()).getValue().getKey();

                                    if (platesHit == parkour.checkpoints.size()) {
                                        ListenerRegistry.registeredEvents.forEach(clazz -> {
                                            try {
                                                clazz.newInstance().onParkourEnd(new ParkourEventHandler.ParkourEndEvent(
                                                        player,
                                                        parkour,
                                                        System.currentTimeMillis() - ParkourRegistry.playerParkourCache.get(player.getUniqueId()).getValue().getValue()
                                                ));
                                            } catch (InstantiationException | IllegalAccessException e) {
                                                throw new RuntimeException(e);
                                            }
                                        });

                                        long timeSpent = System.currentTimeMillis() - ParkourRegistry.playerParkourCache.get(player.getUniqueId()).getValue().getValue();
                                        long oldTimeSpent = SwoftyParkour.getPlugin().getSql().getParkourTime(parkour, player.getUniqueId()) == null ? 0 : SwoftyParkour.getPlugin().getSql().getParkourTime(parkour, player.getUniqueId());

                                        if (oldTimeSpent < timeSpent && oldTimeSpent != 0) {
                                            SUtil.sendMessageList(player, SUtil.translateColorWords(SUtil.variableize(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.finished-course-worse-score"), Arrays.asList(
                                                    Map.entry("$NAME", parkour.getName()),
                                                    Map.entry("$TIME", new SimpleDateFormat("mm:ss.SSS").format(timeSpent)),
                                                    Map.entry("$OLD", new SimpleDateFormat("mm:ss.SSS").format(oldTimeSpent))))
                                            ));
                                        } else {
                                            SUtil.sendMessageList(player, SUtil.translateColorWords(SUtil.variableize(SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.finished-course-new-score"), Arrays.asList(
                                                    Map.entry("$NAME", parkour.getName()),
                                                    Map.entry("$TIME", new SimpleDateFormat("mm:ss.SSS").format(timeSpent)),
                                                    Map.entry("$OLD", new SimpleDateFormat("mm:ss.SSS").format(oldTimeSpent))))
                                            ));

                                            try {
                                                if (SwoftyParkour.getPlugin().getSql().getParkourTime(parkour, player.getUniqueId()) == null) {
                                                    PreparedStatement statement = SwoftyParkour.getPlugin().getSql().getConnection().prepareStatement("INSERT INTO `parkour_" + parkour.getName() + "` (`uuid`, `time`) VALUES (?, ?)");
                                                    statement.setString(1, player.getUniqueId().toString());
                                                    statement.setLong(2, timeSpent);
                                                    statement.executeUpdate();
                                                    statement.close();
                                                } else {
                                                    PreparedStatement statement = SwoftyParkour.getPlugin().getSql().getConnection().prepareStatement("UPDATE `parkour_" + parkour.getName() + "` SET time=" + timeSpent + " WHERE uuid=?");
                                                    statement.setString(1, player.getUniqueId().toString());
                                                    statement.executeUpdate();
                                                    statement.close();
                                                }
                                            } catch (SQLException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }

                                        ParkourRegistry.playerParkourCache.remove(player.getUniqueId());
                                        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                                    } else {
                                        player.sendMessage(SUtil.translateColorWords(SUtil.variableize(SwoftyParkour.getPlugin().getMessages().getString("messages.parkour.missed-plate"), Arrays.asList(Map.entry("$PLATE", String.valueOf(platesHit + 1))))));
                                        ParkourRegistry.playerParkourCache.remove(player.getUniqueId());
                                        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                                    }
                                } else {
                                    player.sendMessage(SUtil.translateColorWords(SUtil.variableize(SwoftyParkour.getPlugin().getMessages().getString("messages.parkour.must-touch-start-plate"), Arrays.asList(Map.entry("$NAME", parkour.getName())))));
                                }
                                return;
                            }
                        }

                        if (parkour.getCheckpoints() != null) {
                            AtomicInteger x = new AtomicInteger();
                            parkour.getCheckpoints().forEach(location -> {
                                if (location.distance(player.getLocation()) < 1) {
                                    x.getAndIncrement();
                                    if (isDelayedCheckpoint(player, x.get())) return;

                                    if (!parkour.getFinished()) {
                                        SUtil.sendMessageList(player, SwoftyParkour.getPlugin().getMessages().getStringList("messages.parkour.parkour-not-finished"));
                                        return;
                                    }

                                    if (ParkourRegistry.playerParkourCache.containsKey(player.getUniqueId())) {
                                        int currentPlate = x.get();
                                        int previousPlate = ParkourRegistry.playerParkourCache.get(player.getUniqueId()).getValue().getKey();

                                        if (currentPlate == previousPlate + 1) {
                                            ListenerRegistry.registeredEvents.forEach(clazz -> {
                                                try {
                                                    clazz.newInstance().onCheckpointHit(new ParkourEventHandler.CheckpointHitEvent(
                                                            player,
                                                            parkour,
                                                            currentPlate,
                                                            System.currentTimeMillis() - ParkourRegistry.playerParkourCache.get(player.getUniqueId()).getValue().getValue()
                                                    ));
                                                } catch (InstantiationException | IllegalAccessException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            });

                                            ParkourRegistry.playerParkourCache.put(
                                                    player.getUniqueId(),
                                                    Map.entry(parkour, Map.entry(currentPlate, ParkourRegistry.playerParkourCache.get(player.getUniqueId()).getValue().getValue()))
                                            );
                                            player.sendMessage(SUtil.translateColorWords(SUtil.variableize(SwoftyParkour.getPlugin().getMessages().getString("messages.parkour.successfully-hit-plate"), Arrays.asList(Map.entry("$PLATE", String.valueOf(currentPlate))))));
                                        } else {
                                            if (currentPlate > previousPlate) {
                                                player.sendMessage(SUtil.translateColorWords(SUtil.variableize(SwoftyParkour.getPlugin().getMessages().getString("messages.parkour.missed-plate"), Arrays.asList(Map.entry("$PLATE", String.valueOf(previousPlate + 1))))));
                                                ParkourRegistry.playerParkourCache.remove(player.getUniqueId());
                                                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                                            } else {
                                                player.sendMessage(SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getString("messages.parkour.went-back")));
                                            }
                                        }
                                    } else {
                                        player.sendMessage(SUtil.translateColorWords(SUtil.variableize(SwoftyParkour.getPlugin().getMessages().getString("messages.parkour.must-touch-start-plate"), Arrays.asList(Map.entry("$NAME", parkour.getName())))));
                                    }
                                }
                                x.getAndIncrement();
                            });
                        }
                    }
                });
            }
        }.runTaskTimer(SwoftyParkour.getPlugin(), 3, 1));

        this.tasks.add(new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, Map.Entry<Parkour, Map.Entry<Integer, Long>>> entry : ParkourRegistry.playerParkourCache.entrySet()) {
                    try {
                        Player player = SwoftyParkour.getPlugin().getServer().getPlayer(entry.getKey());

                        List<String> scoreboardLinesTemp = SwoftyParkour.getPlugin().getMessages().getStringList("messages.scoreboard");
                        List<String> scoreboardLines = new ArrayList<>();
                        scoreboardLinesTemp.forEach(entry2 -> {
                            scoreboardLines.add(SUtil.variableize(SUtil.translateColorWords(entry2),
                                    Arrays.asList(
                                            Map.entry("$NAME", entry.getValue().getKey().getName()),
                                            Map.entry("$TIME", String.valueOf(new DecimalFormat("#.00").format(Double.parseDouble(String.valueOf(System.currentTimeMillis() - entry.getValue().getValue().getValue())) / 1000))),
                                            Map.entry("$POSITION", SwoftyParkour.getPlugin().getSql().getPosition(player.getUniqueId(), entry.getValue().getKey()) == 0 ? "§cNever completed" : String.valueOf(SwoftyParkour.getPlugin().getSql().getPosition(player.getUniqueId(), entry.getValue().getKey()))),
                                            Map.entry("$PREVIOUS_BEST",
                                                    String.valueOf(SwoftyParkour.getPlugin().getSql().getParkourTime(entry.getValue().getKey(), entry.getKey())).equals("null") ?
                                                            "§cNever completed" : new SimpleDateFormat("mm:ss.SSS").format(SwoftyParkour.getPlugin().getSql().getParkourTime(entry.getValue().getKey(), entry.getKey())))
                            )));
                        });
                        //Collections.reverse(scoreboardLines); - did not end up needing this

                        Sidebar sidebar = new Sidebar(player, scoreboardLines.get(0), "parkour");

                        for (int x = 1; x < scoreboardLines.size(); x++) {
                            sidebar.add(scoreboardLines.get(x));
                        }
                        sidebar.apply(player);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskTimer(SwoftyParkour.getPlugin(), 3, 2));
    }

    public void stop() {
        for (BukkitTask task : this.tasks)
            task.cancel();
    }
}
