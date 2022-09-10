package net.swofty.parkour.plugin.command;

import java.util.ArrayList;
import java.util.List;

public class CommandLoader {
    public static List<ParkourCommand> commands;

    public CommandLoader() {
        this.commands = new ArrayList<>();
    }

    public void register(ParkourCommand command) {
        commands.add(command);
    }

    public int getCommandAmount() {
        return commands.size();
    }
}