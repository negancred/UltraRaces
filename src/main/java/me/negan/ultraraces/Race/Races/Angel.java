package me.negan.ultraraces.Race.Races;

import me.negan.ultraraces.Race.Race;
import me.negan.ultraraces.Utils.Methods;
import me.negan.ultraraces.UltraRaces;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;

public class Angel extends Race {
    private static final double HEAL_RADIUS = 6.0;

    public Angel(UltraRaces plugin) {
        super(plugin);
    }

    @Override
    public String getRaceName() {
        return "angel";
    }

    @Override
    public void ActivateActiveSkill(Player player) {

    }

    @Override
    public void onDamageDealt(Player damager, Entity victim, UltraRaces plugin) {
        if (Methods.isOnCooldown(damager, plugin, "Healing light", true)) return;
        activateHealingLight(damager, plugin);
    }

    private void activateHealingLight(Player player, Plugin plugin) {
        Location center = player.getLocation();
        World world = player.getWorld();

        spawnAndSoundEffects(world, center);
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

    private void spawnAndSoundEffects(World world, Location center) {
        world.spawnParticle(Particle.HAPPY_VILLAGER, center, 50, 6, 1, 6, 0.2);
        world.spawnParticle(Particle.HEART, center, 30, 6, 2, 6, 0.1);
        world.playSound(center, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 1.5f, 1f);
    }
}
