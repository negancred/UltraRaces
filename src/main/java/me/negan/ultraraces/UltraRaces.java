package me.negan.ultraraces;

import me.negan.ultraraces.Race.RaceCommand;
import me.negan.ultraraces.Race.RaceDescriptionManager;
import me.negan.ultraraces.Race.RaceListener;
import me.negan.ultraraces.Race.Races.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public final class UltraRaces extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        RaceDescriptionManager raceDescriptionManager = new RaceDescriptionManager(this);
        Objects.requireNonNull(getCommand("race")).setExecutor(new RaceCommand(this, raceDescriptionManager));
        getServer().getPluginManager().registerEvents(new RaceListener(this), this);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String race = getConfig().getString("races." + player.getUniqueId());
                    if (race == null) continue;

                    switch (race.toLowerCase()) {
                        case "goblin" -> Goblin.applyEffect(player);
                        case "merman" -> Merman.applyEffect(player);
                        case "human" -> Human.applyEffect(player);
                        case "werewolf" -> Werewolf.applyEffect(player);
                        case "undead" -> Undead.applyEffect(player);
                        case "vampire" -> Vampire.applyEffect(player);
                        case "piglin" -> Piglin.applyEffect(player, UltraRaces.this);
                        case "cosmic", "angel", "sentinel", "howler", "marionette", "goddess", "assassin" -> {}
                    }
                }
            }
        }.runTaskTimer(this, 0L, 10L);
    }

    @Override
    public void onDisable() {
        getLogger().info("UltraRaces disabled.");
    }

    public void sendPrefixed(CommandSender sender, String message) {
        sender.sendMessage("§8[§6UltraRaces§8]§r " + message);
    }
}
