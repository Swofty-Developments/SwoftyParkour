package net.swofty.parkour.api;

import net.swofty.parkour.api.listeners.ListenerRegistry;

public class SwoftyParkourAPI {

    /**
     * Used to retrieve the ListenerRegistry to presumably handle events
     * @return instance of the ListenerRegistry.clazz
     */
    public static ListenerRegistry getListenerRegistry() {
        return new ListenerRegistry();
    }

}
