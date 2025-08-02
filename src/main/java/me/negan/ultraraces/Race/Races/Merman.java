package me.negan.ultraraces.Race.Races;

import org.bukkit.entity.Player;
import org.bukkit.potion.*;

public class Merman {
    public static void applyEffect(Player player) {
        if (player.getRemainingAir() < player.getMaximumAir() || player.getLocation().getBlock().isLiquid()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 60, 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 100, 0, true, false));
        }
    }
}
