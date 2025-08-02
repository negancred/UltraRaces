package me.negan.ultraraces.Race.Races;

import me.negan.ultraraces.UltraRaces;
import me.negan.ultraraces.Helpers.Methods;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Goddess {

    private static final Map<UUID, Integer> regenStacks = new HashMap<>();
    private static final Map<UUID, Long> lastHealTime = new HashMap<>();
    private static final int REGEN_DURATION = 60;
    private static final Map<UUID, Long> COOLDOWN = new HashMap<>();

    public static void handleDamage(Player damagedPlayer, Entity damager, UltraRaces plugin) {
        for (Player goddess : Bukkit.getOnlinePlayers()) {
            if (goddess == damagedPlayer) continue;
            if (!Methods.isRace(goddess, plugin, "goddess")) continue;
            if (!goddess.getWorld().equals(damagedPlayer.getWorld())) continue;
            if (goddess.getLocation().distance(damagedPlayer.getLocation()) > 5) continue;
            if (damager instanceof Player attacker && attacker.equals(goddess)) continue;

            UUID damagedUUID = damagedPlayer.getUniqueId();
            long now = System.currentTimeMillis();
            int previousLevel = regenStacks.getOrDefault(damagedUUID, 0);
            long lastTime = lastHealTime.getOrDefault(damagedUUID, 0L);

            if (now - lastTime > 3000) { previousLevel = 0; }
            int newLevel = Math.min(previousLevel + 1, 2);
            damagedPlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, REGEN_DURATION, newLevel - 1, true, true));
            regenStacks.put(damagedUUID, newLevel);
            lastHealTime.put(damagedUUID, now);

        }
    }

    public static void ActivateActiveSkill(Player player, UltraRaces plugin) {
        if (!Methods.isRace(player, plugin, "goddess")) return;
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (Methods.isOnCooldown(player, plugin, "Banquet", false)) return;
        player.sendActionBar(Component.text("§dLet the world come alive!"));

        player.getWorld().spawnParticle(Particle.FALLING_WATER, player.getLocation(), 250, 10, 5, 10, 0.05);
        player.getWorld().spawnParticle(Particle.NAUTILUS, player.getLocation(), 250, 10, 5, 10, 0.05);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1f, 1f);
        Location baseLoc = player.getLocation().clone().add(player.getLocation().getDirection().normalize().multiply(2));
        int groundY = baseLoc.getWorld().getHighestBlockYAt(baseLoc);
        baseLoc.setY(groundY);
        
        ArmorStand conduit = (ArmorStand) player.getWorld().spawnEntity(baseLoc, EntityType.ARMOR_STAND, true);
        ItemStack helmet = new ItemStack(Material.CONDUIT);
        conduit.setVisible(false);
        conduit.setGravity(false);
        conduit.setMarker(true);
        conduit.setSilent(true);
        conduit.setCustomNameVisible(false);
        conduit.getEquipment().setHelmet(helmet, true);
        conduit.getWorld().spawnParticle(Particle.POOF, conduit.getLocation().add(0, 2, 0), 10, 0.2, 2, 0.2, 0.01);

        new BukkitRunnable() {
            int tick = 0;
            double radius = 5;
            double angleOffset = 0;

            @Override
            public void run() {
                if (tick >= 200 || conduit.isDead()) {
                    conduit.remove();
                    this.cancel();
                    return;
                }
                Location center = conduit.getLocation().clone().add(0, 1.3, 0);
                World world = center.getWorld();
                int points = 30;
                for (int i = 0; i < points; i++) {
                    double angle = angleOffset + 2 * Math.PI * i / points;
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    Location particleLoc = center.clone().add(x, 0, z);
                    world.spawnParticle(Particle.RAIN, particleLoc, 0, 0, 0, 0, 0);
                }
                if (tick % 20 == 0) {
                    for (Player nearby : world.getPlayers()) {
                        if (!nearby.getWorld().equals(world)) continue;
                        if (nearby.getLocation().distance(conduit.getLocation()) > radius) continue;
                        double maxHealth = Objects.requireNonNull(nearby.getAttribute(Attribute.MAX_HEALTH)).getValue();
                        double healAmount = maxHealth * 0.16;
                        nearby.setHealth(Math.min(nearby.getHealth() + healAmount, maxHealth));
                        nearby.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 200, 1, true, true));
                    }
                }

                angleOffset += Math.toRadians(5);
                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        for (Player nearby : player.getWorld().getPlayers()) {
            if (nearby == player) continue;
            if (!nearby.getWorld().equals(player.getWorld())) continue;
            if (player.getLocation().distance(nearby.getLocation()) > 10) continue;

            nearby.setHealth(Objects.requireNonNull(nearby.getAttribute(Attribute.MAX_HEALTH)).getValue());
            nearby.sendMessage(Component.text("§dYou received the blessing of the Goddess..."));
            nearby.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 200, 0, true, true));
            nearby.getWorld().spawnParticle(Particle.HEART, nearby.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 2);
        } COOLDOWN.put(uuid, now);
    }
}