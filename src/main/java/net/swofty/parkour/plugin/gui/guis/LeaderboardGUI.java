package net.swofty.parkour.plugin.gui.guis;

import net.swofty.parkour.plugin.gui.GUI;
import net.swofty.parkour.plugin.parkour.Parkour;
import org.bukkit.event.inventory.InventoryClickEvent;

public class LeaderboardGUI extends GUI {

    private final String query;
    private int page;
    private Parkour parkour;

    private static final int[] INTERIOR = new int[]{
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    public LeaderboardGUI(Parkour parkour, String q, int page) {
        super("Item Browser", 54);
        this.query = q;
        this.page = page;
        this.parkour = parkour;
    }

    public LeaderboardGUI(Parkour parkour, String query) {
        this(parkour, query, 1);
    }

    public LeaderboardGUI(Parkour parkour, int page) {
        this(parkour, "", page);
    }

    public LeaderboardGUI(Parkour parkour) {
        this(parkour, 1);
    }

    @Override
    public void onBottomClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }
}
