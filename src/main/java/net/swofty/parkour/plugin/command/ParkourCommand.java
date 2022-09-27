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
    private static final Map<UUID, HashMap<String, Long>> CMD_COOLDOWN = new HashMap<>();
    public static final String COMMAND_SUFFIX = "subCommand_";

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

    public abstract void run(CommandSource sender, String[] args, SwoftyParkour plugin);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    public static void register(SwoftyParkour plugin) {
        plugin.commandMap.register("", new ParkourCommandHandler(plugin));
    }

    public static class ParkourCommandHandler extends Command {

        public ParkourCommand parkourCommand;
        public SwoftyParkour plugin;

        public ParkourCommandHandler(SwoftyParkour plugin) {
            super("parkour", "Access all of your parkouring needs", "", new ArrayList<>());
            this.plugin = plugin;
        }

        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            if (!(sender instanceof Player)) {
                System.out.println("Console senders cannot use commands");
                return false;
            }

            if (args.length == 0) {
                plugin.messages.getStringList("messages.command.usage-overall").forEach(s -> {
                    sender.sendMessage(SUtil.translateColorWords(s));
                });
                return false;
            }

            for (ParkourCommand parkourCommand1 : CommandLoader.commands) {
                if (parkourCommand1.name.equals(args[0]) || parkourCommand1.aliases.contains(args[0])) {
                    this.parkourCommand = parkourCommand1;
                }
            }

            if (this.parkourCommand == null) {
                plugin.messages.getStringList("messages.command.usage-overall").forEach(s -> {
                    sender.sendMessage(SUtil.translateColorWords(s));
                });
                return false;
            }

            parkourCommand.sender = new CommandSource(sender);

            if (!parkourCommand.permission.equals("") && !sender.hasPermission(parkourCommand.permission)) {
                sender.sendMessage(SUtil.translateColorWords(plugin.messages.getString("messages.command.no-permission")));
                return false;
            }

            if (parkourCommand instanceof CommandCooldown) {
                HashMap<String, Long> cooldowns = new HashMap<>();
                if (CMD_COOLDOWN.containsKey(((Player) sender).getUniqueId())) {
                    cooldowns = CMD_COOLDOWN.get(((Player) sender).getUniqueId());
                    if (cooldowns.containsKey(parkourCommand.name)) {
                        if (System.currentTimeMillis() - cooldowns.get(parkourCommand.name) < ((CommandCooldown) parkourCommand).getCooldown()) {
                            sender.sendMessage(SUtil.translateColorWords(plugin.messages.getString("messages.command.cooldown").replace("$SECONDS", String.valueOf((double) (System.currentTimeMillis() - cooldowns.get(parkourCommand)) / 1000))));
                            return false;
                        }
                    }
                }
                cooldowns.put(parkourCommand.name, System.currentTimeMillis() + ((CommandCooldown) parkourCommand).getCooldown());
                CMD_COOLDOWN.put(((Player) sender).getUniqueId(), cooldowns);
            }

            parkourCommand.run(parkourCommand.sender, args, plugin);
            return false;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
            if (args.length <= 1) {
                List<String> list = new ArrayList<>();
                CommandLoader.commands.stream().forEach(entry -> list.add(entry.name));
                return list;
            } else {
                for (ParkourCommand parkourCommand1 : CommandLoader.commands) {
                    if (parkourCommand1.name.equals(args[0]) || parkourCommand1.aliases.contains(args[0])) {
                        this.parkourCommand = parkourCommand1;
                        return parkourCommand.tabCompleters(sender, alias, args);
                    }
                }

                this.parkourCommand = null;
                return new ArrayList<>();
            }
        }
    }

    public abstract List<String> tabCompleters(CommandSender sender, String alias, String[] args);

    public void send(String message, CommandSource sender) {
        sender.send(ChatColor.GRAY + message.replace("&", "§"));
    }

    public void send(String message) {
        send(SUtil.translateColorWords(message), sender);
    }

    public void send(List<String> message) {
        SUtil.translateColorWords(message).forEach(message2 -> {
            sender.send(message2);
        });
    }
}