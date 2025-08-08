package me.negan.ultraraces.Race.Races;

import me.negan.ultraraces.Race.Race;
import me.negan.ultraraces.Utils.Methods;
import me.negan.ultraraces.UltraRaces;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Sentinel extends Race {
    private static final double PASSIVE_RADIUS = 10.0;

    @Override
    public String getRaceName() {
        return "sentinel";
    }
    public Sentinel(UltraRaces plugin) {
        super(plugin);
    }

    @Override
    public void onBlock(Player player, EntityDamageByEntityEvent event) {
        handleBlock(player, plugin);
    }

    public static void handleBlock(Player player, UltraRaces plugin) {
        if (Methods.isOnCooldown(player, plugin, "Bulwark", false)) return;
        ResistanceToAllies(player);

        player.getWorld().spawnParticle(Particle.ASH, player.getLocation(), 20, 1, 1, 1);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1, 1);
        player.sendActionBar(Component.text("§aBulwark activated!"));
    }

    private static void ResistanceToAllies(Player sentinel) {
        Location center = sentinel.getLocation();
        World world = sentinel.getWorld();

        sentinel.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 240, 1, true, false));
        for (Entity entity : world.getNearbyEntities(center, PASSIVE_RADIUS, PASSIVE_RADIUS, PASSIVE_RADIUS)) {
            if (entity instanceof Player nearby && !nearby.equals(sentinel)) {
                nearby.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200, 1, true, false));
            }
        }
    }



    @Override
    public void ActivateActiveSkill(Player player) {

    }
}
