package net.swofty.parkour.plugin.command.subtypes;

import net.swofty.parkour.plugin.command.CommandCooldown;
import net.swofty.parkour.plugin.command.CommandParameters;
import net.swofty.parkour.plugin.command.CommandSource;
import net.swofty.parkour.plugin.command.ParkourCommand;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

@CommandParameters(permission = "test1")
public class subCommand_info extends ParkourCommand implements CommandCooldown {

    @Override
    public void run(CommandSource sender, String[] args) {
        sender.send("§cTest successful");
    }

    @Override
    public List<String> tabCompleters(CommandSender sender, String alias, String[] args) {
        return Arrays.asList("test1", "test2");
    }

    @Override
    public long cooldownSeconds() {
        return 1;
    }
}
