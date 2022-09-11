package net.swofty.parkour.plugin.command.subtypes;

import net.swofty.parkour.plugin.command.CommandCooldown;
import net.swofty.parkour.plugin.command.CommandParameters;
import net.swofty.parkour.plugin.command.CommandSource;
import net.swofty.parkour.plugin.command.ParkourCommand;
import net.swofty.parkour.plugin.parkour.ParkourRegistry;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandParameters()
public class subCommand_debug extends ParkourCommand implements CommandCooldown {

    @Override
    public void run(CommandSource sender, String[] args) {
        System.out.println("Registry;");
        ParkourRegistry.parkourRegistry.forEach(parkour -> {
            System.out.println(parkour.serialize());
        });
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
