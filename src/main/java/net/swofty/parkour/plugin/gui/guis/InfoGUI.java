package net.swofty.parkour.plugin.gui.guis;

import net.swofty.parkour.plugin.gui.GUI;
import net.swofty.parkour.plugin.gui.guiitem.GUIClickableItem;
import net.swofty.parkour.plugin.gui.guiitem.GUIItem;
import net.swofty.parkour.plugin.parkour.Parkour;
import net.swofty.parkour.plugin.utilities.PaginationList;
import net.swofty.parkour.plugin.utilities.SUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InfoGUI extends GUI {

    private final String query;
    private int page;
    private Parkour parkour;

    private static final int[] INTERIOR = new int[]{
            11, 12, 13, 14, 15,
    };

    public InfoGUI(Parkour parkour, String q, int page) {
        super("", 27);
        PaginationList<Location> pagedLocations = new PaginationList<>(5);
        try {
            pagedLocations.addAll(parkour.getCheckpoints());
        } catch (Exception e) {}

        this.title = "Parkour Information | Page " + page + "/" + pagedLocations.getPageCount();
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
                return SUtil.getStack("§aMenu Navigation", Material.WARPED_SIGN, (short) 0, 1, "§e", "§7This menu displays all of the checkpoints", "§7inside of the §f" + parkour.getName() + " §7parkour in pages.", "§7With pagination controls being found at the", "§7bottom of this GUI.", "§e ", "§7To the sides of the GUI are the §aStart §7pressure", "§7plates and the §cEnd §7pressure plates.");
            }
        });

        set(new GUIClickableItem() {
            @Override
            public int getSlot() {
                return 10;
            }

            @Override
            public ItemStack getItem() {
                return SUtil.getStack("§aStart Pressure Plate", Material.HEAVY_WEIGHTED_PRESSURE_PLATE, (short) 0, 1, "§fX: " + parkour.getStartLocation().getBlockX() + " Y: " + parkour.getStartLocation().getBlockY() + " Z: " + parkour.getStartLocation().getBlockZ(), "§e ", "§eClick to teleport");
            }

            @Override
            public void run(InventoryClickEvent e) {
                ((Player) e.getWhoClicked()).performCommand("parkour teleport " + parkour.getName());
            }
        });

        set(new GUIItem() {
            @Override
            public int getSlot() {
                return 16;
            }

            @Override
            public ItemStack getItem() {
                return SUtil.getStack("§aEnd Pressure Plate", Material.HEAVY_WEIGHTED_PRESSURE_PLATE, (short) 0, 1, "§fX: " + parkour.getEndLocation().getBlockX() + " Y: " + parkour.getEndLocation().getBlockY() + " Z: " + parkour.getEndLocation().getBlockZ());
            }
        });

        set(GUIClickableItem.getCloseItem(22));

        try {
            List<Location> loc = pagedLocations.getPage(page);
            for (int i = 0; i < loc.size(); i++) {
                int slot = INTERIOR[i];
                Location mainLocation = loc.get(i);
                int finalI = i;
                set(new GUIClickableItem() {
                    @Override
                    public void run(InventoryClickEvent e) {
                        Player player = (Player) e.getWhoClicked();
                        player.performCommand("parkour teleport " + parkour.getName() + " " + (((page - 1) * pagedLocations.getElementsPerPage()) + finalI + 1));
                    }

                    @Override
                    public int getSlot() {
                        return slot;
                    }

                    @Override
                    public ItemStack getItem() {
                        return SUtil.getStack("§aCheckpoint #" + (((page - 1) * pagedLocations.getElementsPerPage()) + finalI + 1), Material.LIGHT_WEIGHTED_PRESSURE_PLATE, (short) 0, 1, "§fX: " + mainLocation.getBlockX() + " Y: " + mainLocation.getBlockY() + " Z: " + mainLocation.getBlockZ(), "§e ", "§eClick to teleport");
                    }
                });
            }

            if (page != pagedLocations.getPageCount()) {
                set(new GUIClickableItem() {
                    @Override
                    public void run(InventoryClickEvent e) {
                        new InfoGUI(parkour, page + 1).open((Player) e.getWhoClicked());
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
                        new InfoGUI(parkour, page - 1).open((Player) e.getWhoClicked());
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
        } catch (Exception e) {
            fill(SUtil.createNamedItemStack(Material.RED_WOOL, "§cNo Checkpoints"), 11, 15);
        }
    }

    public InfoGUI(Parkour parkour, String query) {
        this(parkour, query, 1);
    }

    public InfoGUI(Parkour parkour, int page) {
        this(parkour, "", page);
    }

    public InfoGUI(Parkour parkour) {
        this(parkour, 1);
    }

    @Override
    public void onBottomClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }
}
