package me.negan.ultraraces.Race.Races;

import me.negan.ultraraces.Helpers.Methods;
import me.negan.ultraraces.UltraRaces;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class Cosmic {
    private static final int CD = 10_000;
    private static final float INCREASED_SPEED = 0.4f;

    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final Set<UUID> ActivatedSkillPlayers = new HashSet<>();
    private static final Map<UUID, Float> ORIG_SPEEDS = new HashMap<>();

    public static void handleDamage(Player player, UltraRaces plugin) {
        UUID uuid = player.getUniqueId();
        if (ActivatedSkillPlayers.contains(uuid)) return;
        long currentTime = System.currentTimeMillis();
        if (cooldowns.containsKey(uuid) && currentTime - cooldowns.get(uuid) < CD) return;
        cooldowns.put(uuid, currentTime);
        ActivatePhantomState(player, plugin);
    }

    public static void ActivateActiveSkill(Player player, UltraRaces plugin) {
        if (Methods.isOnCooldown(player, plugin, "Scatter", false)) return;
        Block target = player.getTargetBlockExact(100);

        if (target == null) {
            player.sendActionBar(Component.text("§cNo valid block in sight!"));
            return;
        }
        Location targetLoc = target.getLocation().add(0.5, 1, 0.5);
        if (!targetLoc.getBlock().isPassable()) {
            player.sendActionBar(Component.text("§cTarget area is not safe!"));
            return;
        }

        targetLoc.setPitch(player.getLocation().getPitch());
        targetLoc.setYaw(player.getLocation().getYaw());
        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 30, 0.3, 0.5, 0.3, 0.01);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        player.teleport(targetLoc);
        player.getWorld().spawnParticle(Particle.PORTAL, targetLoc, 30, 0.3, 0.5, 0.3, 0.01);
        player.getWorld().playSound(targetLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        player.sendActionBar(Component.text("§5§oScatter activated!"));

        float originalSpeed = player.getWalkSpeed();
        player.setWalkSpeed(0f);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 60, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 1, false, false));

        for (Player players : Bukkit.getOnlinePlayers()) {
            players.hidePlayer(plugin, player);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.setWalkSpeed(originalSpeed);
            for (Player players : Bukkit.getOnlinePlayers()) {
                players.showPlayer(plugin, player);
            }
        }, 60L);
    }

    private static void ActivatePhantomState(Player player, Plugin plugin) {
        UUID uuid = player.getUniqueId();
        ActivatedSkillPlayers.add(uuid);
        ORIG_SPEEDS.put(uuid, player.getWalkSpeed());
        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 50, 0.3, 0.5, 0.3, 0.01);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 40, 1, false, false));
        for (Player players : Bukkit.getOnlinePlayers()) { players.hidePlayer(plugin, player); }
        player.setWalkSpeed(INCREASED_SPEED);
        player.sendActionBar(Component.text("§5Phantom State is activated"));

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!player.isOnline() || player.isDead()) {
                    endVoidreaperState(player, plugin);
                    cancel();
                    return;
                }
                if (ticks++ >= 40) {
                    endVoidreaperState(player, plugin);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public static void handleAttack(Player player, Plugin plugin) {
        if (ActivatedSkillPlayers.contains(player.getUniqueId())) {
            endVoidreaperState(player, plugin);
        }
    }

    private static void endVoidreaperState(Player player, Plugin plugin) {
        UUID uuid = player.getUniqueId();
        ActivatedSkillPlayers.remove(uuid);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        for (Player players : Bukkit.getOnlinePlayers()) { players.showPlayer(plugin, player); }

        if (ORIG_SPEEDS.containsKey(uuid)) {
            player.setWalkSpeed(ORIG_SPEEDS.get(uuid));
            ORIG_SPEEDS.remove(uuid);
        } else {
            player.setWalkSpeed(0.2f);
        }

        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 50, 0.3, 0.5, 0.3, 0.01);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
    }
}



