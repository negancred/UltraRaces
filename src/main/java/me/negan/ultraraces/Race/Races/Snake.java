package me.negan.ultraraces.Race.Races;

import me.negan.ultraraces.Race.Race;
import me.negan.ultraraces.UltraRaces;
import me.negan.ultraraces.Utils.Methods;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class Snake extends Race {
    private static final Random random = new Random();

    public Snake(UltraRaces plugin) {
        super(plugin);
    }

    @Override
    public String getRaceName() {
        return "snake";
    }
    public void onDamageDealt(Player player, Entity target, UltraRaces event){
        if (!(target instanceof LivingEntity livingTarget)) return;
        if (random.nextDouble() <= 0.10) {
            livingTarget.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 0));
            target.getWorld().spawnParticle(Particle.SPORE_BLOSSOM_AIR, target.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 2);
        }
    }
    @Override
    public boolean ShouldActivateItemSkill(Player player) {
        return player.getInventory().getItemInMainHand().getType() == Material.SLIME_BALL;
    }

    @Override
    public void ActivateActiveSkill(Player player) {
        if (!Methods.isRace(player, plugin, "snake")) {
            return;
        }

        if (Methods.isOnCooldown(player, plugin, "Venom Cloud", false)) {
            return;
        }


        Location start = player.getEyeLocation();
        Block targetBlock = player.getTargetBlockExact(30);
        Location end = (targetBlock != null ? targetBlock.getLocation() : start.clone().add(player.getLocation().getDirection().multiply(30)))
                .add(0.5, 1, 0.5);
        Vector direction = end.toVector().subtract(start.toVector()).normalize();

        new BukkitRunnable() {
            final Location current = start.clone();
            int step = 0;
            final int maxSteps = 25;

            @Override
            public void run() {
                if (step++ > maxSteps) {
                    cancel();
                    return;
                }

                player.getWorld().spawnParticle(Particle.SPORE_BLOSSOM_AIR, current, 30, 1, 1, 1, 0.5);

                for (Entity entity : player.getWorld().getNearbyEntities(current, 1.5, 1.5, 1.5)) {
                    if (entity instanceof LivingEntity living && !entity.equals(player)) {
                        living.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 0, true, true));
                        living.getWorld().spawnParticle(Particle.SPORE_BLOSSOM_AIR, living.getLocation().add(0, 1, 0), 10);
                        if (entity instanceof Player target) {
                            target.sendMessage(Component.text("§2§oPoison fills your lungs..."));
                        }
                    }
                }

                current.add(direction);
            }
        }.runTaskTimer(plugin, 0, 2);
    }
}
