package net.swofty.parkour.plugin.command.subtypes;

import net.swofty.parkour.plugin.SwoftyParkour;
import net.swofty.parkour.plugin.command.CommandCooldown;
import net.swofty.parkour.plugin.command.CommandParameters;
import net.swofty.parkour.plugin.command.CommandSource;
import net.swofty.parkour.plugin.command.ParkourCommand;
import net.swofty.parkour.plugin.parkour.Parkour;
import net.swofty.parkour.plugin.parkour.ParkourRegistry;
import net.swofty.parkour.plugin.utilities.SUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CommandParameters(permission = "parkour.admin.teleport")
public class subCommand_teleport extends ParkourCommand implements CommandCooldown {

    @Override
    public void run(CommandSource sender, String[] args) {
        if (args.length < 2) {
            send(SUtil.variableize(SUtil.translateColorWords(SwoftyParkour.getPlugin().messages.getString("messages.command.usage-command")), Arrays.asList(Map.entry("$USAGE", "/parkour teleport <parkour> [checkpoint]"))));
            return;
        }

        String name = args[1];

        if (ParkourRegistry.getFromName(name) == null) {
            send(SUtil.variableize(SUtil.translateColorWords(SwoftyParkour.getPlugin().messages.getString("messages.command.parkour-not-found")), Arrays.asList(Map.entry("$NAME", name))));
            return;
        }

        if (ParkourRegistry.playerParkourCache.containsKey(sender.getPlayer().getUniqueId())) {
            sender.getPlayer().sendMessage(SUtil.translateColorWords(SwoftyParkour.getPlugin().getMessages().getString("messages.parkour.flew-during-course")));
            ParkourRegistry.playerParkourCache.remove(sender.getPlayer().getUniqueId());
            sender.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }

        Parkour parkour = ParkourRegistry.getFromName(name);
        if (args.length == 2) {
            sender.getPlayer().teleport(parkour.getStartLocation());
        } else {
            String checkpoint = args[2];

            if (checkpoint.matches("-?(0|[1-9]\\d*)")) {
                Integer checkpointNumber = Integer.parseInt(checkpoint);

                if (checkpointNumber > parkour.getCheckpoints().size() || checkpointNumber < 1) {
                    send(SUtil.variableize(SUtil.translateColorWords(SwoftyParkour.getPlugin().messages.getString("messages.command.invalid-number-input")), Arrays.asList(Map.entry("$INPUT", checkpoint))));
                } else {
                    sender.getPlayer().teleport(parkour.getCheckpoints().get(checkpointNumber - 1));
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