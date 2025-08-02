package me.negan.ultraraces.Race.Races;

import me.negan.ultraraces.Utils.Methods;
import me.negan.ultraraces.UltraRaces;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import me.negan.ultraraces.Utils.AttributeMethods;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.*;

public class Werewolf {
    private static final Map<UUID, Long> frenzyTimers = new HashMap<>();
    private static final Set<UUID> inFrenzy = new HashSet<>();

    public static void applyEffect(Player player) {
        World world = player.getWorld();
        long time = world.getTime();

        if (time >= 13000 && time <= 23000) {
            AttributeMethods.setAttribute(player, Attribute.MAX_HEALTH, 40.0);
            player.setHealthScale(40f);
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 220, 0, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 320, 1, true, false));
        } else {
            player.setHealthScale(20f);
            AttributeMethods.setAttribute(player, Attribute.MAX_HEALTH, 20.0);
        }
    }

    public static void handleDamage(EntityDamageByEntityEvent event, Player player, UltraRaces plugin) {
        double damage = event.getFinalDamage();
        double currentHealth = player.getHealth();
        if (currentHealth - damage <= currentHealth - 6.0) {
            triggerFrenzy(player, plugin);
        }
    }

    public static void handleAttack(EntityDamageByEntityEvent event, Player player) {
        if (!isInFrenzy(player)) return;
        Entity target = event.getEntity();
        if (target instanceof LivingEntity) {
            clearFrenzy(player);
        }
    }

    public static void triggerFrenzy(Player player, UltraRaces plugin) {
        if (Methods.isOnCooldown(player, plugin, "Frenzy", false)) return;
        UUID uuid = player.getUniqueId();
        inFrenzy.add(uuid);
        frenzyTimers.put(uuid, System.currentTimeMillis());
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 99999, 2, true, false));
        player.setWalkSpeed(0.3f);
        player.sendActionBar(Component.text("§8Frenzy is Activated"));
        player.playSound(player.getLocation(), Sound.ENTITY_WOLF_BIG_GROWL, 1f, 1f);
    }

    public static boolean isInFrenzy(Player player) {
        return inFrenzy.contains(player.getUniqueId());
    }

    public static void clearFrenzy(Player player)
    {
        UUID uuid = player.getUniqueId();
        if (!inFrenzy.contains(uuid)) return;

        inFrenzy.remove(uuid);
        frenzyTimers.remove(uuid);
        player.setWalkSpeed(0.2f);
        player.removePotionEffect(PotionEffectType.STRENGTH);
    }

    public static void checkFrenzyTimeouts()
    {
        long now = System.currentTimeMillis();
        for (UUID uuid : new HashSet<>(frenzyTimers.keySet())) {
            if (now - frenzyTimers.get(uuid) > 6000) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    clearFrenzy(player);
                }
            }
        }
    }
}
