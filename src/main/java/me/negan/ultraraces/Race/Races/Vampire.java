package me.negan.ultraraces.Race.Races;

import me.negan.ultraraces.Race.Race;
import me.negan.ultraraces.UltraRaces;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.attribute.Attribute;
import me.negan.ultraraces.Utils.AttributeMethods;
import net.kyori.adventure.text.Component;

import java.util.Objects;

public class Vampire extends Race {
    public Vampire(UltraRaces plugin) {
        super(plugin);
    }

    @Override
    public String getRaceName() {
        return "vampire";
    }
    @Override
    public void onKill(Player player, Entity target){
        LifestealOnKill(player);
    }
    @Override
    public void ContinuousPassiveEffect(Player player) {
        applyEffect(player);
    }

    public static void applyEffect(Player player) {
        World world = player.getWorld();
        long time = world.getTime();
        boolean isDay = time >= 0 && time < 12300;
        boolean isDawn = time >= 23500 || time < 0;

        if (isDawn && AttributeMethods.isInDirectSky(player)) {
            player.sendActionBar(Component.text("§c§oYou feel dizzy..."));
        }
        if (isDay && AttributeMethods.isInDirectSunlight(player)) {
            player.setFireTicks(40);
        }

        if (!isDay) {
            AttributeMethods.setAttribute(player, Attribute.MAX_HEALTH, 40.0);
            player.setWalkSpeed(0.35f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 320, 1, true, false));
            player.setHealthScale(40f);
            AttributeMethods.setAttribute(player, Attribute.ATTACK_SPEED, 4.5);
        } else {
            AttributeMethods.setAttribute(player, Attribute.MAX_HEALTH, 20.0);
            player.setWalkSpeed(0.2f);
            player.setHealthScale(20f);
        }
    }

    public void LifestealOnKill(Player player) {
        double maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue();
        double currentHealth = player.getHealth();
        double healAmount = 7.0;

        player.setHealth(Math.min(currentHealth + healAmount, maxHealth));
        player.setFoodLevel((int) (player.getFoodLevel() + 5.0));
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation(), 5);
    }



    @Override
    public void ActivateActiveSkill(Player player) {

    }
}
