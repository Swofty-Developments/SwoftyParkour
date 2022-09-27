package net.swofty.parkour.plugin.parkour;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Parkour implements ConfigurationSerializable {

    public String name;
    public Location startLocation;
    public Location endLocation;
    public List<Location> checkpoints;
    public Boolean finished;
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
