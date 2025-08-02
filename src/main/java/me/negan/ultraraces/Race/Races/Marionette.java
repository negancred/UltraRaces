package me.negan.ultraraces.Race.Races;

import me.negan.ultraraces.Helpers.Methods;
import me.negan.ultraraces.UltraRaces;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Marionette {
    private static final Random random = new Random();

    public static void tryRedirectMob(EntityTargetLivingEntityEvent event, UltraRaces plugin) {
        if (!(event.getTarget() instanceof Player marionette)) return;
        if (!plugin.getConfig().getString("races." + marionette.getUniqueId(), "").equalsIgnoreCase("marionette")) return;

        if (random.nextDouble() <= 0.2) {
            List<LivingEntity> nearby = event.getEntity().getNearbyEntities(8, 8, 8).stream()
                    .filter(e -> e instanceof LivingEntity &&
                            !e.equals(marionette) &&
                            !(e instanceof ArmorStand) &&
                            !e.isDead())
                    .map(e -> (LivingEntity) e)
                    .toList();

            if (!nearby.isEmpty()) {
                LivingEntity newTarget = nearby.get(random.nextInt(nearby.size()));
                event.setTarget(newTarget);
                newTarget.getWorld().spawnParticle(Particle.RAID_OMEN, newTarget.getLocation(), 20, 0.5, 1, 0.5, 0.05);
            }
        }
    }

    public static void ActivateActiveSkill(Player player, UltraRaces plugin) {
        if (Methods.isOnCooldown(player, plugin, "Commanding Pull", false)) return;
        Entity target = player.getTargetEntity(12);
        if (!(target instanceof LivingEntity livingTarget) || target == player){
            player.sendActionBar(Component.text("§cNo valid target in sight."));
            return;
        }

        Location PlayerLoc = player.getLocation().add(0, 1, 0);
        Location TargetLoc = livingTarget.getLocation();
        Vector pullDirection = PlayerLoc.toVector().subtract(TargetLoc.toVector()).normalize().multiply(1.5);
        livingTarget.setVelocity(pullDirection);


        target.getWorld().spawnParticle(Particle.PORTAL, target.getLocation(), 30, 0.4, 0.8, 0.4, 0.01);
        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 1f, 0.7f);
        List<Creature> nearbyHostiles = player.getWorld().getNearbyEntities(player.getLocation(), 12, 12, 12).stream()
                .filter(e -> e instanceof Monster)
                .map(e -> (Creature) e)
                .toList();

        for (Creature creature : nearbyHostiles) {
            creature.setTarget(livingTarget);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!livingTarget.isValid() || livingTarget.isDead()) {
                    for (Creature creature : nearbyHostiles) {
                        if (creature.getTarget() == livingTarget) {
                            creature.setTarget(null);
                        }
                    }
                    cancel();
                } else {
                    for (Creature creature : nearbyHostiles) {
                        if (creature.getTarget() == livingTarget) {
                            creature.setTarget(null);
                        }
                    }
                }
            }
        }.runTaskLater(plugin, 160L);
        player.sendActionBar(Component.text("§5§oCommanding Pull activated!"));
    }
}
