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

@CommandParameters(permission = "parkour.admin.top")
public class subCommand_settop extends ParkourCommand implements CommandCooldown {

    @Override
    public void run(CommandSource sender, String[] args) {
        if (args.length < 2) {
            send(SUtil.variableize(SUtil.translateColorWords(SwoftyParkour.getPlugin().messages.getString("messages.command.usage-command")), Arrays.asList(Map.entry("$USAGE", "/parkour settop <parkour>"))));
            return;
        }

        String name = args[1];

        if (ParkourRegistry.getFromName(name) == null) {
            send(SUtil.variableize(SUtil.translateColorWords(SwoftyParkour.getPlugin().messages.getString("messages.command.parkour-not-found")), Arrays.asList(Map.entry("$NAME", name))));
            return;
        }

        Parkour parkour = ParkourRegistry.getFromName(name);
        parkour.setTop(sender.getPlayer().getLocation());
        ParkourRegistry.updateParkour(parkour.getName(), parkour);
        ParkourRegistry.saveParkour(parkour, SwoftyParkour.getPlugin().getParkours());
        send(SUtil.variableize(SUtil.translateColorWords(SwoftyParkour.getPlugin().messages.getString("messages.command.parkour-set-top")), Arrays.asList(Map.entry("$NAME", name))));
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