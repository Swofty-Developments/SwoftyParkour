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

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CommandParameters(permission = "parkour.admin.delete")
public class subCommand_delete extends ParkourCommand implements CommandCooldown {

    @Override
    public void run(CommandSource sender, String[] args) {
        if (args.length < 2) {
            send(SUtil.variableize(SUtil.translateColorWords(SwoftyParkour.getPlugin().messages.getString("messages.command.usage-command")), Arrays.asList(Map.entry("$USAGE", "/parkour delete <parkour> [checkpoint]"))));
            return;
        }

        String name = args[1];

        if (ParkourRegistry.getFromName(name) == null) {
            send(SUtil.variableize(SUtil.translateColorWords(SwoftyParkour.getPlugin().messages.getString("messages.command.parkour-not-found")), Arrays.asList(Map.entry("$NAME", name))));
            return;
        }

        Parkour parkour = ParkourRegistry.getFromName(name);
        if (args.length == 2) {
            parkour.getStartLocation().getWorld().getBlockAt(parkour.getStartLocation()).setType(Material.AIR);
            parkour.getEndLocation().getWorld().getBlockAt(parkour.getStartLocation()).setType(Material.AIR);
            parkour.getCheckpoints().forEach(loc -> loc.getWorld().getBlockAt(loc).setType(Material.AIR));

            ParkourRegistry.parkourRegistry.remove(parkour);
            ParkourRegistry.removeParkour(parkour.getName(), SwoftyParkour.getPlugin().getParkours());
            send(SUtil.variableize(SUtil.translateColorWords(SwoftyParkour.getPlugin().messages.getString("messages.command.parkour-deleted")), Arrays.asList(Map.entry("$NAME", name))));

            try {
                SwoftyParkour.getPlugin().getSql().getConnection().prepareStatement("DROP TABLE `parkour_" + parkour.getName()).execute();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            String checkpoint = args[2];

            if (checkpoint.matches("-?(0|[1-9]\\d*)")) {
                Integer checkpointNumber = Integer.parseInt(checkpoint);

                if (checkpointNumber > parkour.getCheckpoints().size()) {
                    send(SUtil.variableize(SUtil.translateColorWords(SwoftyParkour.getPlugin().messages.getString("messages.command.invalid-number-input")), Arrays.asList(Map.entry("$INPUT", checkpoint))));
                } else {
                    List<Location> checkpoints = parkour.getCheckpoints();
                    parkour.getCheckpoints().get(checkpointNumber - 1).getWorld().getBlockAt(parkour.getCheckpoints().get(checkpointNumber - 1)).setType(Material.AIR);
                    checkpoints.remove(checkpointNumber - 1);
                    parkour.setCheckpoints(checkpoints);

                    ParkourRegistry.updateParkour(parkour.getName(), parkour);
                    ParkourRegistry.saveParkour(parkour, SwoftyParkour.getPlugin().getParkours());
                }
            } else {
                send(SUtil.variableize(SUtil.translateColorWords(SwoftyParkour.getPlugin().messages.getString("messages.command.invalid-number-input")), Arrays.asList(Map.entry("$INPUT", checkpoint))));
            }
        }
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