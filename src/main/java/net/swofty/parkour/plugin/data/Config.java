package net.swofty.parkour.plugin.data;

import net.swofty.parkour.plugin.SwoftyParkour;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config extends YamlConfiguration {
    private final SwoftyParkour plugin;
    private final File file;

    public Config(File parent, String name) {
        this.plugin = SwoftyParkour.getPlugin();
        this.file = new File(parent, name);

        if (!file.exists()) {
            options().copyDefaults(true);
            plugin.saveResource(name, false);
        }
        load();
    }

    public Config(String name) {
        this(SwoftyParkour.getPlugin().getDataFolder(), name);
    }

    public void load() {
        try {
            super.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            super.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}