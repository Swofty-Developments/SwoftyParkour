package net.swofty.parkour.plugin.parkour;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkourSession {

    public Parkour parkour;
    public Integer checkpoint;
    public Long timeStarted;

    public ParkourSession(Parkour parkour) {
        timeStarted = System.currentTimeMillis();
        checkpoint = 0;
        this.parkour = parkour;
    }

}
