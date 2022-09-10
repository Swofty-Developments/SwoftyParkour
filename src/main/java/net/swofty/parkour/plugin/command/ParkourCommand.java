package net.swofty.parkour.plugin.command;

import net.swofty.parkour.plugin.SwoftyParkour;
import net.swofty.parkour.plugin.utilities.Message;
import net.swofty.parkour.plugin.utilities.SUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class ParkourCommand implements CommandExecutor, TabCompleter {
    private static final Map<UUID, HashMap<ParkourCommand, Long>> CMD_COOLDOWN = new HashMap<>();
    public static final String COMMAND_SUFFIX = "Command";

    private final CommandParameters params;
    private final String name;
    private final String description;
    private final String usage;
    private final List<String> aliases;
    private final String permission;

    private CommandSource sender;

    protected ParkourCommand() {
        this.params = this.getClass().getAnnotation(CommandParameters.class);
        this.name = this.getClass().getSimpleName().replace(COMMAND_SUFFIX, "").toLowerCase();
        this.description = this.params.description();
        this.usage = this.params.usage();
        this.aliases = Arrays.asList(this.params.aliases().split(","));
        this.permission = this.params.permission();
    }

    public abstract void run(CommandSource sender, String[] args);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    public static void register() {
        SwoftyParkour.getPlugin().commandMap.register("", new ParkourCommandHandler());
    }

    public static class ParkourCommandHandler extends Command {

        public ParkourCommand parkourCommand;

        public ParkourCommandHandler() {
            super("parkour", "Access all of your parkouring needs", "", new ArrayList<>());
        }

        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            if (args.length == 0) {
                SwoftyParkour.getPlugin().messages.getStringList("messages.usage").forEach(s -> {
                    sender.sendMessage(SUtil.translateColorWords(s));
                });
                return false;
            }

            CommandLoader.commands.forEach(parkourCommand1 -> {
                if (parkourCommand1.name.equals(args[0]) || parkourCommand1.aliases.contains(args[0])) {
                    this.parkourCommand = parkourCommand1;
                    parkourCommand.sender = new CommandSource(sender);

                    if (!sender.hasPermission(parkourCommand.permission)) {
                        sender.sendMessage(SUtil.translateColorWords(SwoftyParkour.getPlugin().messages.getString("messages.command.no-permission")));
                        return;
                    }

                    if (parkourCommand instanceof CommandCooldown) {
                        HashMap<ParkourCommand, Long> cooldowns = new HashMap<>();
                        if (CMD_COOLDOWN.containsKey(((Player) sender).getUniqueId())) {
                            cooldowns = CMD_COOLDOWN.get(((Player) sender).getUniqueId());
                            if (cooldowns.containsKey(parkourCommand)) {
                                if (System.currentTimeMillis() - cooldowns.get(parkourCommand) < ((CommandCooldown) parkourCommand).getCooldown()) {
                                    sender.sendMessage(SUtil.translateColorWords(SwoftyParkour.getPlugin().messages.getString("messages.command.cooldown").replace("$SECONDS", String.valueOf((System.currentTimeMillis() - cooldowns.get(parkourCommand)) / 1000))));
                                    return;
                                }
                            }
                        }
                        cooldowns.put(parkourCommand, System.currentTimeMillis() + ((CommandCooldown) parkourCommand).getCooldown());
                        CMD_COOLDOWN.put(((Player) sender).getUniqueId(), cooldowns);
                    }

                    parkourCommand.run(parkourCommand.sender, args);
                }
            });
            return false;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
            if (args.length == 0) {
                List<String> list = new ArrayList<>();
                CommandLoader.commands.stream().forEach(entry -> list.add(entry.name));
                return list;
            } else {
                return parkourCommand.tabCompleters(sender, alias, args);
            }
        }
    }

    public abstract List<String> tabCompleters(CommandSender sender, String alias, String[] args);

    public void send(String message, CommandSource sender) {
        sender.send(ChatColor.GRAY + message.replace("&", "§"));
    }

    public void send(String message) {
        send(message.replace("&", "§"), sender);
    }

    public void send(Message msg) {
        msg.send(sender.getPlayer());
    }

    public void sound(Sound sound, float pitch) {
        if (sender.getSender() instanceof Player) {
            sender.getPlayer().playSound(sender.getPlayer().getLocation(), sound, 1, pitch);
        }
    }

    public void send(String message, Player player) {
        player.sendMessage(ChatColor.GRAY + message.replace("&", "§"));
    }
}