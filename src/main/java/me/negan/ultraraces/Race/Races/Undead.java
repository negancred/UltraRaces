package me.negan.ultraraces.Race.Races;

import me.negan.ultraraces.Race.Race;
import me.negan.ultraraces.UltraRaces;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Undead extends Race {
    public Undead(UltraRaces plugin) {
        super(plugin);
    }
    @Override
    public String getRaceName() {
        return "undead";
    }
    public void ContinuousPassiveEffect(Player player){
        World world = player.getWorld();
        long time = world.getTime();
        if (time >= 13000 && time <= 23000) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 320, 1, true, false));
        } else {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }
    @Override
    public void ActivateActiveSkill(Player player) {

    }
    public void onMobTarget(Player player, EntityTargetLivingEntityEvent event) {
        if (event.getEntity() instanceof Zombie || event.getEntity() instanceof Skeleton){
            event.setCancelled(true);
        }
    }

}
