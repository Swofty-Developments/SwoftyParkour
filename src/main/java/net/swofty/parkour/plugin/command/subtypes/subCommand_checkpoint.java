package net.swofty.parkour.plugin.command.subtypes;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CommandParameters(permission = "parkour.admin.checkpoint")
public class subCommand_checkpoint extends ParkourCommand implements CommandCooldown {

    @Override
    public void run(CommandSource sender, String[] args) {
        if (args.length == 1) {
            send(SUtil.variableize(SUtil.translateColorWords(SwoftyParkour.getPlugin().messages.getString("messages.command.usage-command")), Arrays.asList(Map.entry("$USAGE", "/parkour checkpoint <parkour>"))));
            return;
        }

        String name = args[1];

        if (ParkourRegistry.getFromName(name) == null) {
            send(SUtil.variableize(SUtil.translateColorWords(SwoftyParkour.getPlugin().messages.getString("messages.command.parkour-not-found")), Arrays.asList(Map.entry("$NAME", name))));
            return;
        }

        Location originalLoc = sender.getPlayer().getLocation();
        Location loc = new Location(originalLoc.getWorld(), originalLoc.getBlockX(), originalLoc.getBlockY(), originalLoc.getBlockZ());
        Parkour parkour = ParkourRegistry.getFromName(name);

        List<Location> checkpoints = parkour.getCheckpoints();
        if (checkpoints == null) checkpoints = new ArrayList<>();
        checkpoints.add(loc);
        parkour.setCheckpoints(checkpoints);
        ParkourRegistry.updateParkour(name, parkour);
        ParkourRegistry.saveParkour(parkour, SwoftyParkour.getPlugin().parkours);

        loc.getWorld().getBlockAt(loc).setType(SUtil.getPlate(SUtil.PlateType.CHECKPOINT));

        send(SUtil.variableize(
                SwoftyParkour.getPlugin().messages.getStringList("messages.command.checkpoint-placed"),
                Arrays.asList(
                        Map.entry("$NAME", name), Map.entry("$CHECKPOINT", String.valueOf(checkpoints.size()))
                )
        ));
    }

    @Override
    public List<String> tabCompleters(CommandSender sender, String alias, String[] args) {
        if (args.length < 3)
            return ParkourRegistry.getParkourRegistry().stream().map(Parkour::getName).collect(Collectors.toList());
        return null;
    }

    @Override
    public long cooldownSeconds() {
        return 1;
    }
}