package net.swofty.seniorteamapplication.plugin.utilities;

import net.swofty.seniorteamapplication.plugin.SwoftyParkour;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SUtil {
    public static ItemStack getSkull(String playerName, ItemStack stack) {
        Location loc = new Location(Bukkit.getServer().getWorlds().get(0), 1000, 200, 1000);

        loc.getBlock().setType(Material.PLAYER_HEAD);
        Skull s = (Skull) loc.getBlock().getState();
        s.setOwner(playerName);
        s.update();

        ItemStack skull1 = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sk1 = (SkullMeta)skull1.getItemMeta();
        sk1.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));
        skull1.setItemMeta(sk1);
        return stack;
    }

    public static StackResult getStack(Material material, int varaint) {
        return new StackResult(material, varaint);
    }

    public static ItemStack getStack(String name, Material material, short data, int amount, String... lore) {
        return getStack(name, material, data, amount, Arrays.asList(lore));
    }

    public static ItemStack getStack(String name, Material material, short data, int amount, String l) {
        List<String> lore = new ArrayList<>();
        for (String string : SUtil.splitByWordAndLength(l, 30, "\\s"))
            lore.add(ChatColor.GRAY + string);

        return getStack(name, material, data, amount, lore);
    }

    public static ItemStack getStack(String name, Material material, short data, int amount, List<String> lore) {
        ItemStack stack = new ItemStack(material, data);
        stack.setDurability(data);
        ItemMeta meta = stack.getItemMeta();
        if (name != null) {
            if (name.contains("glowing:"))
                meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);
            meta.setDisplayName(name.replaceAll("glowing:", ""));
        }
        stack.setAmount(amount);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack getSkullStack(String name, String skullName, int amount, String... lore) {
        ItemStack stack = getStack(name, Material.PLAYER_HEAD, (short) 3, amount, lore);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner(skullName);
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack createNamedItemStack(Material material, String name) {
        ItemStack stack = new ItemStack(material);
        if (name != null) {
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(name);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    public static void runBukkitAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(SwoftyParkour.getPlugin(), runnable);
    }

    public static void runAsync(Runnable runnable) {
        new Thread(runnable).start();
//            Bukkit.getScheduler().runTaskAsynchronously(SkyBlock.getPlugin(), runnable);
    }

    public static void runSync(Runnable runnable) {
        Bukkit.getScheduler().runTask(SwoftyParkour.getPlugin(), runnable);
    }

    public static ItemStack getSingleLoreStack(String name, Material material, short data, int amount, String lore) {
        List<String> l = new ArrayList<>();
        for (String line : SUtil.splitByWordAndLength(lore, 30, "\\s"))
            l.add(ChatColor.GRAY + line);
        return getStack(name, material, data, amount, l.toArray(new String[]{}));
    }

    public static List<String> splitByWordAndLength(String string, int splitLength, String separator) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\G" + separator + "*(.{1," + splitLength + "})(?=\\s|$)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(string);
        while (matcher.find())
            result.add(matcher.group(1));
        return result;
    }

    public static String translateColorWords(String s) {
        return color(s.replaceAll("%%black%%", "§0")
                .replaceAll("%%dark-blue%%", "§1")
                .replaceAll("%%dark-green%%", "§2")
                .replaceAll("%%dark-aqua%%", "§3")
                .replaceAll("%%dark-red%%", "§4")
                .replaceAll("%%purple%%", "§5")
                .replaceAll("%%gold%%", "§6")
                .replaceAll("%%gray%%", "§7")
                .replaceAll("%%dark-gray%%", "§8")
                .replaceAll("%%blue%%", "§9")
                .replaceAll("%%green%%", "§a")
                .replaceAll("%%aqua%%", "§b")
                .replaceAll("%%red%%", "§c")
                .replaceAll("%%pink%%", "§d")
                .replaceAll("%%yellow%%", "§e")
                .replaceAll("%%white%%", "§f")
                .replaceAll("%%clear%%", "§r")
                .replaceAll("%%magic%%", "§k")
                .replaceAll("%%bold%%", "§l")
                .replaceAll("%%strike%%", "§m")
                .replaceAll("%%underlined%%", "§n"));
    }

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
