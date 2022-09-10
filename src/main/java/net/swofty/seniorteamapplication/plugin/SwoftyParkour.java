package net.swofty.parkour.plugin;

import lombok.Getter;
import net.swofty.parkour.plugin.listener.PListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

public final class SwoftyParkour extends JavaPlugin {

    @Getter
    private static SwoftyParkour plugin;

    @Override
    public void onEnable() {
        plugin = this;

        loadListeners();
    }

    @Override
    public void onDisable() {

    }

    private void loadListeners() {
        Reflections reflection = new Reflections("net.swofty.seniorteamapplication.plugin.listener.listeners");
        for(Class<? extends PListener> l:reflection.getSubTypesOf(PListener.class)) {
            try {
                l.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }
}
