package me.negan.ultraraces;

import me.negan.ultraraces.Race.*;
import me.negan.ultraraces.Race.Races.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class UltraRaces extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        DescriptionManager descriptionManager = new DescriptionManager(this);
        Objects.requireNonNull(getCommand("race")).setExecutor(new RaceCommand(this, descriptionManager));
        getServer().getPluginManager().registerEvents(new RaceListener(this), this);

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String raceName = getConfig().getString("races." + player.getUniqueId());
                assert raceName != null;
                Race race = RaceRegistry.getRace(raceName);
                if (race != null) {
                    race.ContinuousPassiveEffect(player);
                }
            }
        }, 0L, 40L);

        RaceRegistry.register("goddess", new Goddess(this));
        RaceRegistry.register("angel", new Angel(this));
        RaceRegistry.register("assassin", new Assassin(this));
        RaceRegistry.register("cosmic", new Cosmic(this));
        RaceRegistry.register("ghost", new Ghost(this));
        RaceRegistry.register("goblin", new Goblin(this));
        RaceRegistry.register("howler", new Howler(this));
        RaceRegistry.register("marionette", new Marionette(this));
        RaceRegistry.register("merman", new Merman(this));
        RaceRegistry.register("undead", new Undead(this));
        RaceRegistry.register("piglin", new Piglin(this));
        RaceRegistry.register("sentinel", new Sentinel(this));
        RaceRegistry.register("snake", new Snake(this));
        RaceRegistry.register("vampire", new Vampire(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("UltraRaces disabled.");
    }

    public void sendPrefixed(CommandSender sender, String message) {
        sender.sendMessage("§8[§6UltraRaces§8]§r " + message);
    }
}
