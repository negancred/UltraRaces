package me.negan.ultraraces.Race.Races;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Undead {
    public static void applyEffect(Player player) {
        World world = player.getWorld();
        long time = world.getTime();
        if (time >= 13000 && time <= 23000) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 320, 1, true, false));
        } else {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }
}
