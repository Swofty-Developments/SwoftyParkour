package net.swofty.parkour.plugin.parkour;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parkour implements ConfigurationSerializable {

    @Getter
    @Setter
    public String name;
    @Getter
    @Setter
    public Location startLocation;
    @Getter
    @Setter
    public Location endLocation;
    @Getter
    @Setter
    public List<Location> checkpoints;
    @Getter
    @Setter
    public Boolean finished;
    @Getter
    @Setter
    public Location top;

    public Parkour() {
        finished = false;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("checkpoints", checkpoints);
        map.put("finished", finished);
        map.put("top", top);
        map.put("endLocation", endLocation);
        map.put("startLocation", startLocation);
        map.put("name", name);
        return map;
    }
}
