package net.swofty.parkour.plugin.command.subtypes;

import net.md_5.bungee.api.ChatColor;
import net.swofty.parkour.plugin.command.CommandCooldown;
import net.swofty.parkour.plugin.command.CommandParameters;
import net.swofty.parkour.plugin.command.CommandSource;
import net.swofty.parkour.plugin.command.ParkourCommand;
import net.swofty.parkour.plugin.parkour.ParkourRegistry;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

@CommandParameters(permission = "parkour.admin.create")
public class subCommand_create extends ParkourCommand implements CommandCooldown {

    @Override
    public void run(CommandSource sender, String[] args) {
        String name = args[0];

        send("Test");

        if (ParkourRegistry.getFromName(name) != null) {
            return;
        }
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
