package me.negan.ultraraces.Race;

import me.negan.ultraraces.Utils.Methods;
import me.negan.ultraraces.Race.Races.Piglin;
import me.negan.ultraraces.UltraRaces;
import me.negan.ultraraces.Race.Races.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

public class RaceListener implements Listener {
    private final UltraRaces plugin;

    public RaceListener(UltraRaces plugin) {
        this.plugin = plugin;
        startGhostCheckTask();
        startPiglinBurnCheckTask();
        startFrenzyTimeoutTask();
    }
    private void startFrenzyTimeoutTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Werewolf.checkFrenzyTimeouts();
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        var player = event.getPlayer();
        String race = plugin.getConfig().getString("races." + player.getUniqueId());
        if (race == null) return;
        if (race.equalsIgnoreCase("ghost")) {
            Ghost.handleMovement(player);
        }
    }

    @EventHandler
    public void onMobTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getTarget() instanceof Player player)) return;

        Race race = RaceRegistry.getRace(Methods.getRace(player, plugin));
        if (race != null) {
            race.onMobTarget(player, event);
        }
    }


    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) return;

        Player player = event.getPlayer();
        if (!player.isSneaking()) return;

        String raceKey = Methods.getRace(player, plugin);
        if (raceKey == null) return;


        if (raceKey.equalsIgnoreCase("assassin")) {
            Material main = player.getInventory().getItemInMainHand().getType();
            Material off = player.getInventory().getItemInOffHand().getType();
            if (main == Material.IRON_SWORD || off == Material.IRON_SWORD) {
                Assassin.useTeleportStrike(player, plugin);
            }
            return;
        }

        Race race = RaceRegistry.getRace(raceKey);
        if (race != null && race.ShouldActivateItemSkill(player)) {
            race.ActivateActiveSkill(player);
        }
    }



    @EventHandler
    public void onDamageByFire(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause != EntityDamageEvent.DamageCause.FIRE &&
                cause != EntityDamageEvent.DamageCause.LAVA &&
                cause != EntityDamageEvent.DamageCause.FIRE_TICK) return;

        Race race = RaceRegistry.getRace(Methods.getRace(player, plugin));
        if (race != null) {
            race.onFireDamage(player, event);
        }
    }
    private void startPiglinBurnCheckTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (Methods.isRace(player, plugin, "piglin")) {
                        boolean isInLava = player.getLocation().getBlock().getType().name().contains("LAVA");
                        boolean isBurning = player.getFireTicks() > 0;

                        if (isBurning || isInLava) {
                            Piglin.handleFire(player);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damagerEntity = event.getDamager();
        Entity victimEntity = event.getEntity();

        // onDamageTaken
        if (victimEntity instanceof Player victim) {
            Race race = RaceRegistry.getRace(Methods.getRace(victim, plugin));
            if (race != null) {
                race.onDamageTaken(victim, damagerEntity, event);
            }
        }

        // onDamageDealt
        if (damagerEntity instanceof Player damager) {
            Race race = RaceRegistry.getRace(Methods.getRace(damager, plugin));
            if (race != null) {
                race.onDamageDealt(damager, victimEntity, plugin);
            }
        }
    }



    @EventHandler
    public void onCreeperTick(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof Creeper creeper)) return;
        if (!(event.getTarget() instanceof Player player)) return;

        String race = plugin.getConfig().getString("races." + player.getUniqueId());
        if (race == null) return;
        if (race.equalsIgnoreCase("undead")) {
            creeper.setTarget(null);
            creeper.setAggressive(false);
            creeper.setExplosionRadius(0);
            event.setCancelled(true);
        }
    }

    private void startGhostCheckTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (Methods.isRace(player, plugin, "ghost")) {
                        Ghost.checkIdle(player);
                    }
                });
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    @EventHandler
    public void onBlock(EntityDamageByEntityEvent event) {
        if ((!(event.getEntity() instanceof Player player))) return;

        if (player.isBlocking()) {
            String key = Methods.getRace(player, plugin);
            Race race = RaceRegistry.getRace(key);

            if (race != null) {
                race.onBlock(player, event);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
      Player player = event.getEntity().getKiller();
        if (player == null) return;

        Race race = RaceRegistry.getRace(Methods.getRace(player, plugin));
        if (race != null) {
            race.onKill(player, event.getEntity());
        }

    }

}