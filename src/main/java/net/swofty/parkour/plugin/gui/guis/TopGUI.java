package net.swofty.parkour.plugin.gui.guis;

import net.swofty.parkour.plugin.SwoftyParkour;
import net.swofty.parkour.plugin.gui.GUI;
import net.swofty.parkour.plugin.gui.guiitem.GUIClickableItem;
import net.swofty.parkour.plugin.gui.guiitem.GUIItem;
import net.swofty.parkour.plugin.parkour.Parkour;
import net.swofty.parkour.plugin.utilities.PaginationList;
import net.swofty.parkour.plugin.utilities.SUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.UUID;

public class TopGUI extends GUI {

    private final String query;
    private int page;
    private Parkour parkour;

    private static final int[] INTERIOR = new int[]{
            10, 11, 12, 13, 14, 15, 16
    };

    public TopGUI(Parkour parkour, String q, int page) {
        super("", 27);
        PaginationList<Map.Entry<UUID, Long>> pagedLeaderboard = new PaginationList<>(7);
        pagedLeaderboard.addAll(SwoftyParkour.getPlugin().getSql().getParkourTop(parkour).entrySet());

        this.title = "Parkour Top | Page " + page + "/" + pagedLeaderboard.getPageCount();
        this.query = q;
        this.page = page;
        this.parkour = parkour;

        border(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        set(new GUIItem() {
            @Override
            public int getSlot() {
                return 4;
            }

            @Override
            public ItemStack getItem() {
                return SUtil.getStack("§aMenu Navigation", Material.WARPED_SIGN, (short) 0, 1, "§e", "§7This menu displays all of the top scores", "§7made on the §f" + parkour.getName() + " §7parkour in pages.", "§7With pagination controls being found at the", "§7bottom of this GUI.");
            }
        });

        if (page != pagedLeaderboard.getPageCount()) {
            set(new GUIClickableItem() {
                @Override
                public void run(InventoryClickEvent e) {
                    new TopGUI(parkour, page + 1).open((Player) e.getWhoClicked());
                }

                @Override
                public int getSlot() {
                    return 23;
                }

                @Override
                public ItemStack getItem() {
                    return SUtil.createNamedItemStack(Material.ARROW, ChatColor.GRAY + "->");
                }
            });
        }
        if (page > 1) {
            set(new GUIClickableItem() {
                @Override
                public void run(InventoryClickEvent e) {
                    new TopGUI(parkour, page - 1).open((Player) e.getWhoClicked());
                }

                @Override
                public int getSlot() {
                    return 21;
                }

                @Override
                public ItemStack getItem() {
                    return SUtil.createNamedItemStack(Material.ARROW, ChatColor.GRAY + "<-");
                }
            });
        }

        set(GUIClickableItem.getCloseItem(22));

        List<Map.Entry<UUID, Long>> entries = pagedLeaderboard.getPage(page);
        for (int i = 0; i < entries.size(); i++) {
            int slot = INTERIOR[i];
            Map.Entry<UUID, Long> score = entries.get(i);
            int finalI = i;
            set(new GUIClickableItem() {
                @Override
                public void run(InventoryClickEvent e) {
                    Player player = (Player) e.getWhoClicked();
                    player.performCommand("parkour stats " + score.getKey().toString());
                }

                @Override
                public int getSlot() {
                    return slot;
                }

                @Override
                public ItemStack getItem() {
                    return SUtil.getSkullStack("§a#" + (((page - 1) * pagedLeaderboard.getElementsPerPage()) + finalI + 1) + " §8- " + Bukkit.getOfflinePlayer(score.getKey()).getName(), Bukkit.getOfflinePlayer(score.getKey()).getName(), 1, "§7Time: §f" + new SimpleDateFormat("mm:ss.SSS").format(SwoftyParkour.getPlugin().getSql().getTimesForPlayer(score.getKey()).get(parkour)), "§b", "§eClick to view top scores");
                }
            });
        }
    }

    public TopGUI(Parkour parkour, String query) {
        this(parkour, query, 1);
    }

    public TopGUI(Parkour parkour, int page) {
        this(parkour, "", page);
    }

    public TopGUI(Parkour parkour) {
        this(parkour, 1);
    }

    @Override
    public void onBottomClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }
}
