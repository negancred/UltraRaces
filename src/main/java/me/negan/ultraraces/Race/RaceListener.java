package me.negan.ultraraces.Race;

import me.negan.ultraraces.Helpers.Methods;
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
        if (Methods.isRace(player, plugin, "undead")) {
            if (event.getEntity() instanceof Zombie || event.getEntity() instanceof Skeleton) {
                event.setCancelled(true);
            }
        }
        if (Methods.isRace(player, plugin, "marionette")) {
            Marionette.tryRedirectMob(event, plugin);
        }
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();

        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) return;
        if (!player.isSneaking()) return;

        Material mainHand = player.getInventory().getItemInMainHand().getType();
        Material offHand = player.getInventory().getItemInOffHand().getType();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (Methods.isRace(player, plugin, "howler") && mainHand == Material.BONE) {
                Howler.ActivateActiveSkill(player, plugin);
            }
            if (Methods.isRace(player, plugin, "cosmic") && mainHand == Material.ECHO_SHARD) {
                Cosmic.ActivateActiveSkill(player, plugin);
            }
            if (Methods.isRace(player, plugin, "marionette") && mainHand == Material.STRING) {
                Marionette.ActivateActiveSkill(player, plugin);
            }
            if (Methods.isRace(player, plugin, "goddess") && mainHand == Material.NAUTILUS_SHELL) {
                Goddess.ActivateActiveSkill(player, plugin);
            }
            if (Methods.isRace(player, plugin, "assassin") && (offHand == Material.IRON_SWORD || mainHand == Material.IRON_SWORD)) {
                Assassin.useTeleportStrike(player, plugin);
            }
        }
    }

    @EventHandler
    public void onDamageByFire(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause != EntityDamageEvent.DamageCause.FIRE &&
                cause != EntityDamageEvent.DamageCause.LAVA &&
                cause != EntityDamageEvent.DamageCause.FIRE_TICK) return;

        if (Methods.isRace(player, plugin, "piglin")) {
            Piglin.handleFire(player);
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
        if (event.getEntity() instanceof Player player) {
            String race = Methods.getRace(player, plugin);
            if (race == null) return;
            Goddess.handleDamage(player, event.getDamager(), plugin);

            switch (race.toLowerCase()) {
                case "undead" -> {
                    if (Methods.damagedByWho(event.getDamager(),
                            EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER,
                            EntityType.ZOMBIE_VILLAGER, EntityType.DROWNED)) {
                        event.setCancelled(true);
                    }
                }
                case "cosmic" -> Cosmic.handleDamage(player, plugin);
                case "angel" -> Angel.handleAttack(player, plugin);
                case "werewolf" -> Werewolf.handleDamage(event, player, plugin);
            }

        }

        if (event.getDamager() instanceof Player player) {
            String race = Methods.getRace(player, plugin);
            if (race == null) return;

            switch (Methods.getRace(player, plugin).toLowerCase()) {
                case "cosmic" -> Cosmic.handleAttack(player, plugin);
                case "angel" -> Angel.handleAttack(player, plugin);
                case "werewolf" -> Werewolf.handleAttack(event, player);
                case "ghost" -> Ghost.handleAttack(player);
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
        if (event.getEntity() instanceof Player player) {
            if (player.isBlocking() && Methods.isRace(player, plugin, "sentinel")) {
                Sentinel.handleBlock(player, plugin);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player player)) return;

        if (Methods.isRace(player, plugin, "vampire")) {
            Vampire vampire = new Vampire();
            vampire.LifestealOnKill(player);
        }
    }

}