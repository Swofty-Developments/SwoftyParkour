package net.swofty.parkour.plugin.command.subtypes;

import lombok.SneakyThrows;
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

@CommandParameters(permission = "parkour.admin.end")
public class subCommand_end extends ParkourCommand implements CommandCooldown {

    @SneakyThrows
    @Override
    public void run(CommandSource sender, String[] args, SwoftyParkour plugin) {
        if (args.length == 1) {
            send(SUtil.variableize(SUtil.translateColorWords(plugin.messages.getString("messages.command.usage-command")), Arrays.asList(Map.entry("$USAGE", "/parkour end <parkour>"))));
            return;
        }

        String name = args[1];

        if (ParkourRegistry.getFromName(name) == null) {
            send(SUtil.variableize(SUtil.translateColorWords(plugin.messages.getString("messages.command.parkour-not-found")), Arrays.asList(Map.entry("$NAME", name))));
            return;
        }

        Location originalLoc = sender.getPlayer().getLocation();
        Location loc = new Location(originalLoc.getWorld(), originalLoc.getBlockX(), originalLoc.getBlockY(), originalLoc.getBlockZ());
        Parkour parkour = ParkourRegistry.getFromName(name);

        if (parkour.getEndLocation() != null) {
            parkour.getEndLocation().getWorld().getBlockAt(parkour.getEndLocation()).setType(Material.AIR);
        }

        parkour.setEndLocation(loc);
        parkour.setFinished(true);
        ParkourRegistry.updateParkour(name, parkour);
        ParkourRegistry.saveParkour(parkour, plugin.parkours);

        loc.getWorld().getBlockAt(loc).setType(SUtil.getPlate(SUtil.PlateType.END, plugin));

        plugin.getSql().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS `parkour_" + parkour.getName() + "` (\n" +
                "\t`uuid` TEXT,\n" +
                "\t`time` INT(64)\n" +
                ");").execute();

        send(SUtil.variableize(
                plugin.messages.getStringList("messages.command.end-placed"),
                Arrays.asList(Map.entry("$NAME", name)))
        );
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