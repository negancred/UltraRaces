package me.negan.ultraraces.Race.Races;

import me.negan.ultraraces.Race.Race;
import me.negan.ultraraces.UltraRaces;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Piglin extends Race {
    public Piglin(UltraRaces plugin) {
        super(plugin);
    }

    @Override
    public String getRaceName() {
        return "piglin";
    }

    @Override
    public void onFireDamage(Player player, EntityDamageEvent event){
        handleFire(player);
    }

    @Override
    public void onFireTick(Player player){
        boolean InLava = player.getLocation().getBlock().getType().name().contains("LAVA");
        boolean IsBurning = player.getFireTicks() > 0;

        if (InLava || IsBurning) {
            handleFire(player);
        }

    }
    @Override
    public void ContinuousPassiveEffect(Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 80, 0, true, false));
    }

    public static void handleFire(Player player) {
        player.setFireTicks(0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60, 0, true, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1, true, false));
    }

    @Override
    public void ActivateActiveSkill(Player player) {

    }
}
