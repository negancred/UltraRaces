package me.negan.ultraraces.Race.Races;

import me.negan.ultraraces.Race.Race;
import me.negan.ultraraces.Utils.Methods;
import me.negan.ultraraces.UltraRaces;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Cosmic extends Race {

    private static final int CD = 10_000;
    private static final float INCREASED_SPEED = 0.4f;

    private final Set<UUID> activatedSkillPlayers = new HashSet<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Float> originalSpeeds = new HashMap<>();

    public Cosmic(UltraRaces plugin) {
        super(plugin);
    }

    @Override
    public String getRaceName() {
        return "cosmic";
    }
    @Override
    public boolean ShouldActivateItemSkill(Player player) {
        return player.getInventory().getItemInMainHand().getType() == Material.ECHO_SHARD;
    }
    @Override
    public void onDamageTaken(Player player, Entity damager, EntityDamageByEntityEvent event) {
        UUID uuid = player.getUniqueId();
        if (activatedSkillPlayers.contains(uuid)) return;

        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(uuid) && now - cooldowns.get(uuid) < CD) return;

        cooldowns.put(uuid, now);
        activatePhantomState(player);
    }

    @Override
    public void onDamageDealt(Player player, Entity target, UltraRaces event) {
        if (activatedSkillPlayers.contains(player.getUniqueId())) {
            endVoidreaperState(player);
        }
    }

    @Override
    public void ActivateActiveSkill(Player player) {
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

    private void activatePhantomState(Player player) {
        UUID uuid = player.getUniqueId();
        activatedSkillPlayers.add(uuid);
        originalSpeeds.put(uuid, player.getWalkSpeed());

        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 50, 0.3, 0.5, 0.3, 0.01);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 40, 1, false, false));

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.hidePlayer(plugin, player);
        }

        player.setWalkSpeed(INCREASED_SPEED);
        player.sendActionBar(Component.text("§5Phantom State is activated"));

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!player.isOnline() || player.isDead()) {
                    endVoidreaperState(player);
                    cancel();
                    return;
                }

                if (ticks++ >= 40) {
                    endVoidreaperState(player);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void endVoidreaperState(Player player) {
        UUID uuid = player.getUniqueId();
        activatedSkillPlayers.remove(uuid);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer(plugin, player);
        }

        if (originalSpeeds.containsKey(uuid)) {
            player.setWalkSpeed(originalSpeeds.get(uuid));
            originalSpeeds.remove(uuid);
        } else {
            player.setWalkSpeed(0.2f);
        }

        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation(), 50, 0.3, 0.5, 0.3, 0.01);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
    }
}
