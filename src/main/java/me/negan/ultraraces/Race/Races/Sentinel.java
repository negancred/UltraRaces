package me.negan.ultraraces.Race.Races;

import me.negan.ultraraces.Utils.Methods;
import me.negan.ultraraces.UltraRaces;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Sentinel {
    private static final double PASSIVE_RADIUS = 10.0;

    public static void handleBlock(Player player, UltraRaces plugin) {
        if (Methods.isOnCooldown(player, plugin, "Scatter", false)) return;
        ResistanceToAllies(player);

        player.getWorld().spawnParticle(Particle.ASH, player.getLocation(), 20, 1, 1, 1);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1, 1);
        player.sendActionBar(Component.text("§aBulwark activated!"));
    }

    private static void ResistanceToAllies(Player sentinel) {
        Location center = sentinel.getLocation();
        World world = sentinel.getWorld();

        sentinel.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 240, 1, true, false));
        for (Entity entity : world.getNearbyEntities(center, PASSIVE_RADIUS, PASSIVE_RADIUS, PASSIVE_RADIUS)) {
            if (entity instanceof Player nearby && !nearby.equals(sentinel)) {
                nearby.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200, 1, true, false));
            }
        }
    }

}
