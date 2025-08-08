package me.negan.ultraraces.Race.Races;

import me.negan.ultraraces.Race.Race;
import me.negan.ultraraces.UltraRaces;
import org.bukkit.entity.Player;
import org.bukkit.potion.*;

public class Merman extends Race {
    public Merman(UltraRaces plugin) {
        super(plugin);
    }

    @Override
    public String getRaceName() {
        return "merman";
    }

    @Override
    public void ContinuousPassiveEffect(Player player) {
        if (player.getRemainingAir() < player.getMaximumAir() || player.getLocation().getBlock().isLiquid()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 60, 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 100, 0, true, false));
        }
    }
    @Override
    public void ActivateActiveSkill(Player player) {

    }
}
