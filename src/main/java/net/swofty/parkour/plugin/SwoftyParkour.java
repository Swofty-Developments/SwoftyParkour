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
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.reflections.Reflections;

import java.lang.reflect.Field;

public final class SwoftyParkour extends JavaPlugin {

    @Getter
    private static SwoftyParkour plugin;
    public CommandLoader cl;
    public CommandMap commandMap;
    @Getter
    public Config messages;
    @Getter
    public Config config;
    @Getter
    public Config parkours;
    @Getter
    public SQLDatabase sql;

    @Override
    public void onEnable() {
        plugin = this;

        // Handle commands
        try {
            Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (CommandMap) f.get(Bukkit.getServer());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        cl = new CommandLoader();
        loadCommands();

        // Handle holograms
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(Hologram::handleRefreshment);
            }
        }.runTaskTimer(this, 0, 20);

        // Handle listeners
        loadListeners();

        // Handle config
        messages = new Config("messages.yml");
        config = new Config("config.yml");
        parkours = new Config("parkours.yml");

        // Handle SQL
        sql = new SQLDatabase();

        // Handle startup
        ParkourRegistry.loadFromConfig(parkours);
        new BukkitRunnable() {
            @Override
            public void run() {
                HologramManager.runHologramLoop();
            }
        }.runTaskTimer(this, 10, 10);
    }

    @Override
    public void onDisable() {
        plugin = null;
    }

    private void loadListeners() {
        Reflections reflection = new Reflections("net.swofty.parkour.plugin.listener.listeners");
        for(Class<? extends PListener> l:reflection.getSubTypesOf(PListener.class)) {
            try {
                PListener clazz = l.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadCommands() {
        ParkourCommand.register();

        Reflections reflection = new Reflections("net.swofty.parkour.plugin.command.subtypes");
        for(Class<? extends ParkourCommand> l:reflection.getSubTypesOf(ParkourCommand.class)) {
            try {
                ParkourCommand command = l.newInstance();
                cl.register(command);
            } catch (InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }
}
