package me.negan.ultraraces.Race.Races;

import me.negan.ultraraces.Helpers.Methods;
import me.negan.ultraraces.UltraRaces;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;


public class Angel {
    private static final double HEAL_RADIUS = 6.0;

    public static void handleAttack(Player player, UltraRaces plugin) {
        if (Methods.isOnCooldown(player, plugin, "Healing light", true)) return;
        ActivateActiveSkill(player, plugin);
    }

    private static void ActivateActiveSkill(Player player, Plugin plugin) {


        Location center = player.getLocation();
        World world = player.getWorld();
        world.spawnParticle(Particle.HAPPY_VILLAGER, center, 50, 6, 1, 6, 0.2);
        world.spawnParticle(Particle.HEART, center, 30, 6, 2, 6, 0.1);
        world.playSound(center, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 1.5f, 1f);
        player.sendActionBar(Component.text("§b§oHealing light is activated..."));

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (!player.isOnline() || player.isDead()) {
                    cancel();
                    return;
                }
                for (Entity entity : center.getWorld().getNearbyEntities(center, HEAL_RADIUS, HEAL_RADIUS, HEAL_RADIUS)) {
                    if (entity instanceof Player nearbyPlayer && nearbyPlayer.isOnline() && !nearbyPlayer.isDead()) {
                        nearbyPlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1, true, false));
                    }
                }
                if (++ticks >= 5) cancel();
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
