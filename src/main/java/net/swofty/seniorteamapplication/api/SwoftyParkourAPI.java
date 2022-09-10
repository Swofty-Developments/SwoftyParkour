package net.swofty.seniorteamapplication.api;

import net.swofty.seniorteamapplication.api.listeners.ListenerRegistry;

public class SwoftyParkourAPI {

    /**
     * Used to retrieve the ListenerRegistry to presumably handle events
     * @return instance of the ListenerRegistry.clazz
     */
    public static ListenerRegistry getListenerRegistry() {
        return new ListenerRegistry();
    }

}
