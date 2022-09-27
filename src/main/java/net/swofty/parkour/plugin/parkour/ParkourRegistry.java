package net.swofty.parkour.plugin.parkour;

import lombok.Getter;
import net.swofty.parkour.plugin.data.Config;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class ParkourRegistry {

    @Getter
    public static ArrayList<Parkour> parkourRegistry = new ArrayList<>();
    @Getter
    public static HashMap<UUID, ParkourSession> playerParkourCache = new HashMap<>();

    public static void loadFromConfig(Config config) {
        if (!config.contains("parkours")) return;

        config.getConfigurationSection("parkours").getKeys(false).forEach(key -> {
            ConfigurationSection section = config.getConfigurationSection("parkours").getConfigurationSection(key);
            Parkour parkour = new Parkour();

            parkour.setFinished(section.getBoolean("finished"));
            parkour.setName(section.getString("name"));
            parkour.setStartLocation((Location) section.get("start"));
            parkour.setEndLocation((Location) section.get("end"));
            parkour.setTop((Location) section.get("top"));
            List<Location> checkpoints = new ArrayList<>();
            if (section.getConfigurationSection("checkpoints") != null) {
                section.getConfigurationSection("checkpoints").getKeys(true).stream().sorted().forEach(s -> {
                    checkpoints.add((Location) section.getConfigurationSection("checkpoints").get(s));
                });
            }
            parkour.setCheckpoints(checkpoints);

            parkourRegistry.add(parkour);
        });
    }

    public static Parkour getFromName(String name) {
        for (Parkour parkour : parkourRegistry) {
            if (parkour.getName().equalsIgnoreCase(name)) {
                return parkour;
            }
        }

        return null;
    }

    public static void updateParkour(String name, Parkour parkour) {
        parkourRegistry.removeIf(parkour2 -> parkour2.getName().equalsIgnoreCase(name));
        parkourRegistry.add(parkour);
    }

    public static void removeParkour(String name, Config config) {
        config.getConfigurationSection("parkours").set(name, null);
        config.save();
    }

    public static void saveParkour(Parkour parkour, Config config) {
        if (config.getConfigurationSection("parkours") == null) {
            config.createSection("parkours");
        }
        ConfigurationSection section = config.getConfigurationSection("parkours").createSection(parkour.getName());

        section.set("finished", parkour.getFinished());
        section.set("name", parkour.getName());
        section.set("start", parkour.getStartLocation());
        section.set("end", parkour.getEndLocation());
        section.set("top", parkour.getTop());
        section.set("checkpoints", "");
        section.createSection("checkpoints");
        int x = 0;
        if (parkour.checkpoints != null) {
            for (Location checkpoint : parkour.checkpoints) {
                x++;
                section.getConfigurationSection("checkpoints").set(String.valueOf(x), checkpoint);
            }
        }
        config.save();
    }
}
