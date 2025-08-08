package me.negan.ultraraces.Race.Races;

import me.negan.ultraraces.Race.Race;
import me.negan.ultraraces.Utils.Methods;
import me.negan.ultraraces.UltraRaces;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Random;

public class Howler extends Race {

    public Howler(UltraRaces plugin) {
        super(plugin);
    }

    @Override
    public String getRaceName() {
        return "howler";
    }
    @Override
    public boolean ShouldActivateItemSkill(Player player) {
        return player.getInventory().getItemInMainHand().getType() == Material.BONE;
    }
    @Override
    public void ActivateActiveSkill(Player player) {
        if (Methods.isOnCooldown(player, plugin, "Howl", false)) return;
        tauntNearbyMobs(player);
        Sound[] tauntSounds = {
                Sound.ITEM_GOAT_HORN_SOUND_0,
                Sound.ITEM_GOAT_HORN_SOUND_1,
                Sound.ITEM_GOAT_HORN_SOUND_2
        };
        Random random = new Random();
        Sound luckySound = tauntSounds[random.nextInt(tauntSounds.length)];
        player.getWorld().playSound(player.getLocation(), luckySound, 1.2f, 0.8f);
    }

    private void tauntNearbyMobs(Player player) {
        World world = player.getWorld();
        Location location = player.getLocation();

        for (Entity entity : world.getNearbyEntities(location, 50, 50, 50)) {
            if (entity instanceof Monster monster) {
                monster.setTarget(player);
            } else if (entity instanceof Phantom phantom) {
                phantom.setTarget(player);
            } else if (entity instanceof Slime slime) {
                slime.setTarget(player);
            }
        }
    }

    @Override
    public void onDamageTaken(Player player, Entity damager, EntityDamageByEntityEvent event) {
        // Optional: leave empty or implement defense logic
    }

    @Override
    public void onDamageDealt(Player player, Entity target, UltraRaces event) {
        // Optional: leave empty or implement offensive logic
    }
}
