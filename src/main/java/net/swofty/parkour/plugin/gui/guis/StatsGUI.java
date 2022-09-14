package net.swofty.parkour.plugin.gui.guis;

import net.swofty.parkour.plugin.SwoftyParkour;
import net.swofty.parkour.plugin.gui.GUI;
import net.swofty.parkour.plugin.gui.guiitem.GUIClickableItem;
import net.swofty.parkour.plugin.gui.guiitem.GUIItem;
import net.swofty.parkour.plugin.parkour.Parkour;
import net.swofty.parkour.plugin.parkour.ParkourRegistry;
import net.swofty.parkour.plugin.utilities.PaginationList;
import net.swofty.parkour.plugin.utilities.SUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

public class StatsGUI extends GUI {

    private final String query;
    private int page;
    private UUID player;

    private static final int[] INTERIOR = new int[]{
            10, 11, 12, 13, 14, 15, 16
    };

    public StatsGUI(UUID player, String q, int page) {
        super("", 27);
        PaginationList<Parkour> pagedParkours = new PaginationList<>(5);
        pagedParkours.addAll(ParkourRegistry.parkourRegistry);

        this.title = "Player Information | Page " + page + "/" + pagedParkours.getPageCount();
        this.query = q;
        this.page = page;
        this.player = player;

        border(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        set(new GUIItem() {
            @Override
            public int getSlot() {
                return 4;
            }

            @Override
            public ItemStack getItem() {
                return SUtil.getStack("§aMenu Navigation", Material.WARPED_SIGN, (short) 0, 1, "§e", "§7This menu displays all of the", "§7active parkour courses and the best time", "§7that the player you've selected has had", "§7on it.");
            }
        });

        if (page != pagedParkours.getPageCount()) {
            set(new GUIClickableItem() {
                @Override
                public void run(InventoryClickEvent e) {
                    new StatsGUI(player, page + 1).open((Player) e.getWhoClicked());
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
                    new StatsGUI(player, page - 1).open((Player) e.getWhoClicked());
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

        List<Parkour> loc = pagedParkours.getPage(page);
        for (int i = 0; i < loc.size(); i++) {
            int slot = INTERIOR[i];
            Parkour mainParkour = loc.get(i);
            set(new GUIClickableItem() {
                @Override
                public void run(InventoryClickEvent e) {
                    Player player = (Player) e.getWhoClicked();
                    player.performCommand("parkour info " + mainParkour.getName());
                }

                @Override
                public int getSlot() {
                    return slot;
                }

                @Override
                public ItemStack getItem() {
                    return SUtil.getStack("§aParkour §e" + mainParkour.getName(), Material.ENDER_PEARL, (short) 0, 1, "§fX: " + mainParkour.getStartLocation().getBlockX() + " Y: " + mainParkour.getStartLocation().getBlockY() + " Z: " + mainParkour.getStartLocation().getBlockZ(),
                            "§e ",
                            "§fTime: §7" + (!SwoftyParkour.getPlugin().getSql().getParkourTop(mainParkour).containsKey(player) ?
                                            "§cHas not finished the course" : new SimpleDateFormat("mm:ss.SSS").format(SwoftyParkour.getPlugin().getSql().getParkourTop(mainParkour).get(player))),
                            "§e ",
                            "§eClick for more information");
                }
            });
        }
    }

    public StatsGUI(UUID player, String query) {
        this(player, query, 1);
    }

    public StatsGUI(UUID player, int page) {
        this(player, "", page);
    }

    public StatsGUI(UUID player) {
        this(player, 1);
    }

    @Override
    public void onBottomClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }
}
