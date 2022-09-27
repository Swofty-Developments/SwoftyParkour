package net.swofty.parkour.plugin;

import lombok.Getter;
import net.swofty.parkour.plugin.command.CommandLoader;
import net.swofty.parkour.plugin.command.ParkourCommand;
import net.swofty.parkour.plugin.data.Config;
import net.swofty.parkour.plugin.holograms.Hologram;
import net.swofty.parkour.plugin.holograms.HologramManager;
import net.swofty.parkour.plugin.listener.PListener;
import net.swofty.parkour.plugin.parkour.ParkourRegistry;
import net.swofty.parkour.plugin.sql.SQLDatabase;
import net.swofty.parkour.plugin.utilities.SUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class SwoftyParkour extends JavaPlugin {

    public CommandLoader commandLoader;
    public CommandMap commandMap;
    @Getter
    public Config messages;
    @Getter
    public Repeater repeater;
    @Getter
    public Config config;
    @Getter
    public Config parkours;
    @Getter
    public SQLDatabase sql;

    @Override
    public void onEnable() {
        // Handle commands
        try {
            Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (CommandMap) f.get(Bukkit.getServer());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        commandLoader = new CommandLoader();
        loadCommands(this);

        SwoftyParkour plugin = this;
        // Handle holograms
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    Hologram.handleRefreshment(player, plugin);
                });
            }
        }.runTaskTimer(this, 0, 20);

        // Handle listeners
        loadListeners();

        // Handle config
        messages = new Config("messages.yml", this);
        config = new Config("config.yml", this);
        parkours = new Config("parkours.yml", this);
        SUtil.setCachedHexColors(this);

        // Handle SQL
        sql = new SQLDatabase();

        // Handle startup
        ParkourRegistry.loadFromConfig(parkours);
        new BukkitRunnable() {
            @Override
            public void run() {
                HologramManager.runHologramLoop(plugin);
            }
        }.runTaskTimer(this, 10, 10);
        replacePlates();
        repeater = new Repeater(this);
    }

    @Override
    public void onDisable() {
        repeater.stop();
    }

    private void loadListeners() {
        Reflections reflection = new Reflections("net.swofty.parkour.plugin.listener.listeners");
        for(Class<? extends PListener> l:reflection.getSubTypesOf(PListener.class)) {
            try {
                PListener clazz = l.newInstance();
                clazz.setPlugin(this);
            } catch (InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadCommands(SwoftyParkour plugin) {
        ParkourCommand.register(plugin);

        Reflections reflection = new Reflections("net.swofty.parkour.plugin.command.subtypes");
        for(Class<? extends ParkourCommand> l:reflection.getSubTypesOf(ParkourCommand.class)) {
            try {
                ParkourCommand command = l.newInstance();
                commandLoader.register(command);
            } catch (InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void replacePlates() {
        ParkourRegistry.getParkourRegistry().forEach(parkour -> {
            if (parkour.getStartLocation() != null) {
                Location loc = parkour.getStartLocation();
                loc.getWorld().getBlockAt(loc).setType(SUtil.getPlate(SUtil.PlateType.START, this));
            }
            if (parkour.getCheckpoints() != null) {
                parkour.getCheckpoints().forEach(location -> {
                    Location loc = location;
                    loc.getWorld().getBlockAt(loc).setType(SUtil.getPlate(SUtil.PlateType.CHECKPOINT, this));
                });
            }
            if (parkour.getEndLocation() != null) {
                Location loc = parkour.getEndLocation();
                loc.getWorld().getBlockAt(loc).setType(SUtil.getPlate(SUtil.PlateType.END, this));
            }
        });
    }
}
