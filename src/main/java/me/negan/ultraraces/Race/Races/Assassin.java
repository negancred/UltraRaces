package me.negan.ultraraces.Race.Races;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class Assassin {

    private static final Map<UUID, Long> lastUseTime = new HashMap<>();

    public static void useTeleportStrike(Player player, Plugin plugin) {
        int cooldown = plugin.getConfig().getInt("goddess.skill_cooldown", 300000);
        double maxDistance = 10.0;
        long now = System.currentTimeMillis();
        UUID uuid = player.getUniqueId();
        List<Entity> nearby = player.getNearbyEntities(maxDistance, maxDistance, maxDistance);

        if (lastUseTime.containsKey(uuid)) {
            long lastUsed = lastUseTime.get(uuid);
            if (now - lastUsed < cooldown) {
                long secondsLeft = (cooldown - (now - lastUsed)) / 1000;
                player.sendActionBar(Component.text("§cSoru on cooldown: " + secondsLeft + "s"));
                return;
            }
        }
        lastUseTime.put(uuid, now);

        Entity bestTarget = nearby.stream()
                .filter(e -> e instanceof LivingEntity && e != player)
                .filter(player::hasLineOfSight)
                .min(Comparator.comparingDouble(e -> crosshairAngle(player, e)))
                .orElse(null);

        if (bestTarget == null) {
            player.sendActionBar(Component.text("No valid target in sight"));
            return;
        }

        Location targetLoc = bestTarget.getLocation();
        Vector backward = targetLoc.getDirection().multiply(-1).normalize();
        Location behind = targetLoc.clone().add(backward.multiply(1.5));
        behind.setY(targetLoc.getY());


        if (isLocationSafe(behind)) {
            teleportWithEffect(player, behind, bestTarget);
            return;
        }

        Vector side = backward.clone().crossProduct(new Vector(0, 1, 0)).normalize();
        Location behindLeft = behind.clone().add(side.clone().multiply(0.75));
        Location behindRight = behind.clone().subtract(side.clone().multiply(0.75));

        if (isLocationSafe(behindLeft)) {
            teleportWithEffect(player, behindLeft, bestTarget);
            return;
        }

        if (isLocationSafe(behindRight)) {
            teleportWithEffect(player, behindRight, bestTarget);
            return;
        }

        Vector forward = targetLoc.getDirection().normalize();
        Location front = targetLoc.clone().add(forward.multiply(1.5));
        front.setY(targetLoc.getY());

        if (isLocationSafe(front)) {
            teleportWithEffect(player, front, bestTarget);
            return;
        }

        player.sendActionBar(Component.text("No safe spot"));
    }


    private static boolean isLocationSafe(Location loc) {
        World world = loc.getWorld();
        if (world == null) return false;

        Material feet = world.getBlockAt(loc).getType();
        Material head = world.getBlockAt(loc.clone().add(0, 1, 0)).getType();

        return feet == Material.AIR && head == Material.AIR;
    }

    private static double crosshairAngle(Player player, Entity entity) {
        Vector playerDir = player.getEyeLocation().getDirection().normalize();
        Vector toEntity = entity.getLocation().toVector().subtract(player.getEyeLocation().toVector()).normalize();
        return playerDir.angle(toEntity);
    }
    private static void teleportWithEffect(Player player, Location teleportLocation, Entity target) {
        player.teleport(teleportLocation);

        Location playerLoc = player.getLocation();
        Vector directionToTarget = target.getLocation().toVector().subtract(playerLoc.toVector()).normalize();
        Location facingLoc = playerLoc.clone();
        facingLoc.setDirection(directionToTarget);

        facingLoc.setYaw(facingLoc.getYaw());
        facingLoc.setPitch(0);
        player.teleport(facingLoc);

        player.setVelocity(new Vector(0, 0.1, 0));

        player.getWorld().spawnParticle(Particle.CLOUD, facingLoc, 20, 0.3, 0.1, 0.3, 0.01);
        player.getWorld().playSound(facingLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1.2f);
    }
}
