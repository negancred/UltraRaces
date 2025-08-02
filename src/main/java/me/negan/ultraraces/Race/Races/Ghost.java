package me.negan.ultraraces.Race.Races;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.*;

public class Ghost {
    private static final Map<UUID, Long> lastMoved = new HashMap<>();
    private static final Map<UUID, Boolean> isGhostActivated = new HashMap<>();
    private static final long IDLE_TIME_MS = 3000;
    private static final int EFFECT_DURATION_TICKS = 100;

    public static void handleMovement(Player player) {
        lastMoved.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public static void checkIdle(Player player) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        long lastMove = lastMoved.getOrDefault(uuid, now);
        boolean isSneaking = player.isSneaking();
        boolean idleLongEnough = (now - lastMove) >= IDLE_TIME_MS;

        if (isSneaking && idleLongEnough && !isGhostActivated.getOrDefault(uuid, false)) {
            isGhostActivated.put(uuid, true);
            player.sendActionBar(Component.text("You are now a ghost"));
        }

        if (isGhostActivated.getOrDefault(uuid, false)) {
            if (isSneaking) { applyInvisibility(player); }
            else {
                isGhostActivated.put(uuid, false);
                if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    removeInvisibility(player);
                }
                player.sendActionBar(Component.text("You are no longer a ghost"));
            }
        }
    }

    public static void handleAttack(Player player){
        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            removeInvisibility(player);
        }
    }

    private static void applyInvisibility(Player player) {
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.INVISIBILITY,
                EFFECT_DURATION_TICKS,
                0,
                true,
                false
        ));
    }

    private static void removeInvisibility(Player player){
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
    }
}
