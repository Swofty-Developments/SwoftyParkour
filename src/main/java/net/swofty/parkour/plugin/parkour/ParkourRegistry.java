package net.swofty.parkour.plugin.parkour;

import lombok.Getter;
import net.swofty.parkour.plugin.data.Config;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class ParkourRegistry {

    @Getter
    public static ArrayList<Parkour> parkourRegistry = new ArrayList<>();

    public static void loadFromConfig(Config config) {
        if (!config.contains("parkours")) return;

        config.getConfigurationSection("parkours").getKeys(false).forEach(key -> {
            ConfigurationSection section = config.getConfigurationSection("parkours").getConfigurationSection(key);
            Parkour parkour = new Parkour();

            parkour.setFinished(section.getBoolean("finished"));

            if (!parkour.getFinished()) return;

            parkour.setName(section.getString("name"));
            parkour.setStartLocation((Location) section.get("start"));
            parkour.setEndLocation((Location) section.get("end"));
            parkour.setTop((Location) section.get("top"));
            List<Location> checkpoints = new ArrayList<>();
            section.getConfigurationSection("checkpoints").getKeys(true).stream().sorted().forEach(s -> {
                checkpoints.add((Location) section.getConfigurationSection("checkpoints").getConfigurationSection(s).get("location"));
            });

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

    public static void saveParkour(Parkour parkour, Config config) {
        ConfigurationSection section = config.getConfigurationSection("parkours").getConfigurationSection(parkour.getName());

        section.set("finished", parkour.getFinished());
        section.set("name", parkour.getName());
        section.set("start", parkour.getStartLocation());
        section.set("end", parkour.getEndLocation());
        section.set("top", parkour.getTop());
        section.set("checkpoints", "");
        section.createSection("checkpoints");
        int x = 0;
        for (Location checkpoint : parkour.checkpoints) {
            x++;
            section.getConfigurationSection("checkpoints").set(String.valueOf(x), checkpoint);
        }
    }
}
