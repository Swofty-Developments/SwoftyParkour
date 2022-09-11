package net.swofty.parkour.api.listeners;

import java.util.ArrayList;
import java.util.List;

public class ListenerRegistry {

    public static List<Class<? extends ParkourEventHandler>> registeredEvents = new ArrayList<>();

    /**
     * Register one of your classes as an event listener
     * @param clazz that extends a ParkourEvent or one of its subsidiaries
     */
    public void registerEvent(Class<? extends ParkourEventHandler> clazz) {
        registeredEvents.add(clazz);
    }
}
