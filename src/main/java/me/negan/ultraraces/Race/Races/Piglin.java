package me.negan.ultraraces.Race.Races;

import me.negan.ultraraces.UltraRaces;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Piglin {

    public static void applyEffect(Player player, UltraRaces plugin) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60, 0, true, false));
    }

    public static void handleFire(Player player) {
        player.setFireTicks(0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60, 0, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1, true, false));
    }
}
