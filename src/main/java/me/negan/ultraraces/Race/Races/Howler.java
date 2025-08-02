package me.negan.ultraraces.Race.Races;

import me.negan.ultraraces.Utils.Methods;
import me.negan.ultraraces.UltraRaces;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.*;

public class Howler {

    public static void ActivateActiveSkill(Player player, UltraRaces plugin) {
        if (Methods.isOnCooldown(player, plugin, "Howl", false)) return;
        tauntNearbyMobs(player);
        Sound[] tauntSounds = {
                Sound.ITEM_GOAT_HORN_SOUND_0,
                Sound.ITEM_GOAT_HORN_SOUND_1,
                Sound.ITEM_GOAT_HORN_SOUND_2
        };
        Random random = new Random();
        Sound LuckySound = tauntSounds[random.nextInt(tauntSounds.length)];
        player.getWorld().playSound(player.getLocation(), LuckySound, 1.2f, 0.8f);
    }

    private static void tauntNearbyMobs(Player player) {
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
}
