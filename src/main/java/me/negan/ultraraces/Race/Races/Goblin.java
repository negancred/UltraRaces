package me.negan.ultraraces.Race.Races;

import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.potion.*;

public class Goblin {
    public static void applyEffect(Player player) {
        Biome biome = player.getLocation().getBlock().getBiome();
        if (biome.toString().toLowerCase().contains("forest")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, true, false));
        }
    }
}
