package net.swofty.seniorteamapplication.api.listeners;

import java.util.ArrayList;
import java.util.List;

public class ListenerRegistry {

    private static List<Class<? extends ParkourEvent>> registeredEvents = new ArrayList<>();

    /**
     * Register one of your classes as an event listener
     * @param clazz that extends a ParkourEvent or one of its subsidiaries
     */
    public void registerEvent(Class<? extends ParkourEvent> clazz) {
        registeredEvents.add(clazz);
    }
}
