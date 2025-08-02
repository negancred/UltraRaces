package me.negan.ultraraces.Race;

import me.negan.ultraraces.Utils.AttributeMethods;
import me.negan.ultraraces.UltraRaces;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class RaceCommand implements CommandExecutor {
    private final UltraRaces plugin;
    private final RaceDescriptionManager descManager;
    public RaceCommand(UltraRaces plugin, RaceDescriptionManager descManager) {
        this.plugin = plugin;
        this.descManager = descManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "set", "select", "choose" -> {
                if (args.length != 2) {
                    sendPrefixed(sender, "Usage: /race set <RaceName>");
                    return true;
                }

                String race = args[1].toLowerCase();
                if (!RaceInfo.isValidRace(race)) {
                    plugin.sendPrefixed(player, "Invalid race! Use /race list to see available races.");
                    return true;
                }
                AttributeMethods.resetPlayerAttributes(player);
                plugin.getConfig().set("races." + player.getUniqueId(), race);
                plugin.saveConfig();
                plugin.sendPrefixed(player, "Race set to: " + race);

            }

            case "list" -> {
                sendPrefixed(sender, "Available races:");
                for (String race : RaceInfo.getAllRaceKeys()) {
                    sender.sendMessage(" §7- " + race);
                }
            }

            case "description", "desc" -> {
                String raceKey = plugin.getConfig().getString("races." + player.getUniqueId());
                if (raceKey == null) {
                    sendPrefixed(sender, "You haven't selected a race yet.");
                    return true;
                }

                if (!descManager.hasRace(raceKey)) {
                    sendPrefixed(sender, "Race data not found!");
                    return true;
                }

                var desc = descManager.getDescription(raceKey);
                sender.sendMessage("§8=====[§6UltraRaces§8]=====");
                sender.sendMessage("§7Race: §f" + desc.name);
                sender.sendMessage("§7Passive: §b" + desc.passive);
                sender.sendMessage("§7Skill: §a" + desc.skill);
            }

            default -> sendPrefixed(sender, "Unknown subcommand. Use /race help.");
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§8[§6UltraRaces§8] §7Available Commands:");
        sender.sendMessage(" §e/race set <RaceName>§7 - Set your race.");
        sender.sendMessage(" §e/race list§7 - List all available races.");
        sender.sendMessage(" §e/race description <RaceName>§7 - Get detailed info about your race");
        sender.sendMessage(" §e/race help§7 - Show this help message.");
    }

    private void sendPrefixed(CommandSender sender, String message) {
        sender.sendMessage("§8[§6UltraRaces§8]§r " + message);
    }
}
