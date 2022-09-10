package net.swofty.parkour.plugin;

import lombok.Getter;
import net.swofty.parkour.plugin.command.CommandLoader;
import net.swofty.parkour.plugin.command.ParkourCommand;
import net.swofty.parkour.plugin.data.Config;
import net.swofty.parkour.plugin.listener.PListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.Field;

public final class SwoftyParkour extends JavaPlugin {

    @Getter
    private static SwoftyParkour plugin;
    public CommandLoader cl;
    public CommandMap commandMap;
    public Config messages;

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

        // Handle listeners
        loadListeners();

        // Handle config
        messages = new Config("messages.yml");
    }

    @Override
    public void onDisable() {

    }

    private void loadListeners() {
        Reflections reflection = new Reflections("net.swofty.seniorteamapplication.plugin.listener.listeners");
        for(Class<? extends PListener> l:reflection.getSubTypesOf(PListener.class)) {
            try {
                l.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadCommands() {
        ParkourCommand.register();

        Reflections reflection = new Reflections("net.swofty.lobby.command.commands");
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
