package me.negan.ultraraces.Race.Races;

import me.negan.ultraraces.Race.Race;
import me.negan.ultraraces.UltraRaces;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.potion.*;

public class Goblin extends Race {

    public Goblin(UltraRaces plugin) {
        super(plugin);
    }

    @Override
    public String getRaceName() {
        return "goblin";
    }

    @Override
    public void ActivateActiveSkill(Player player) {

    }

    @Override
    public void ContinuousPassiveEffect(Player player) {
        Biome biome = player.getLocation().getBlock().getBiome();
        if (biome.toString().toLowerCase().contains("forest")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, true, false));
        }
    }
}
