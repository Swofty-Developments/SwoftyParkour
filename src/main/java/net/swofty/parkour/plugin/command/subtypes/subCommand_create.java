package net.swofty.parkour.plugin.command.subtypes;

import net.md_5.bungee.api.ChatColor;
import net.swofty.parkour.plugin.SwoftyParkour;
import net.swofty.parkour.plugin.command.CommandCooldown;
import net.swofty.parkour.plugin.command.CommandParameters;
import net.swofty.parkour.plugin.command.CommandSource;
import net.swofty.parkour.plugin.command.ParkourCommand;
import net.swofty.parkour.plugin.parkour.Parkour;
import net.swofty.parkour.plugin.parkour.ParkourRegistry;
import net.swofty.parkour.plugin.utilities.SUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@CommandParameters(permission = "parkour.admin.create")
public class subCommand_create extends ParkourCommand implements CommandCooldown {

    @Override
    public void run(CommandSource sender, String[] args) {
        if (args.length == 1) {
            send(SUtil.variableize(SUtil.translateColorWords(SwoftyParkour.getPlugin().messages.getString("messages.command.usage-command")), Arrays.asList(Map.entry("$USAGE", "/parkour create <name>"))));
            return;
        }

        String name = args[1];

        if (ParkourRegistry.getFromName(name) != null) {
            send(SUtil.variableize(SwoftyParkour.getPlugin().messages.getString("messages.command.name-already-taken"), Arrays.asList(Map.entry("$NAME", name))));
            return;
        }

        Location originalLoc = sender.getPlayer().getLocation();
        Location loc = new Location(originalLoc.getWorld(), originalLoc.getBlockX(), originalLoc.getBlockY(), originalLoc.getBlockZ());

        Parkour parkour = new Parkour();
        parkour.setStartLocation(loc);
        parkour.setName(name);

        loc.getWorld().getBlockAt(loc).setType(SUtil.getPlate(SUtil.PlateType.START));

        ParkourRegistry.parkourRegistry.add(parkour);
        ParkourRegistry.saveParkour(parkour, SwoftyParkour.getPlugin().parkours);

        send(SUtil.variableize(SwoftyParkour.getPlugin().messages.getStringList("messages.command.creation-message"), Arrays.asList(Map.entry("$NAME", name))));
    }

    @Override
    public List<String> tabCompleters(CommandSender sender, String alias, String[] args) {
        return null;
    }

    @Override
    public long cooldownSeconds() {
        return 1;
    }
}
