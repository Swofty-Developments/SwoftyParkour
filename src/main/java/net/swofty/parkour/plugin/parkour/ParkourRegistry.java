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
        config.getConfigurationSection("parkours").getKeys(false).forEach(key -> {
            ConfigurationSection section = config.getConfigurationSection("parkours").getConfigurationSection(key);
            Parkour parkour = new Parkour();

            parkour.setName(section.getString("name"));
            parkour.setStartLocation((Location) section.get("start"));
            parkour.setEndLocation((Location) section.get("end"));
            List<Location> checkpoints = new ArrayList<>();
            section.getConfigurationSection("checkpoints").getKeys(true).stream().sorted().forEach(s -> {
                checkpoints.add((Location) section.getConfigurationSection("checkpoints").getConfigurationSection(s).get("location"));
            });

            parkourRegistry.add(parkour);
        });
    }

}
