package net.swofty.parkour.plugin.parkour;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.List;

public class Parkour {

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

    Parkour() {}
}
