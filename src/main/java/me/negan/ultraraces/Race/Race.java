package me.negan.ultraraces.Race;

import me.negan.ultraraces.UltraRaces;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public abstract class Race {
    protected final UltraRaces plugin;

    public Race(UltraRaces plugin) {
        this.plugin = plugin;
    }

    public abstract String getRaceName();

    public void onDamageTaken(Player player, Entity damager, EntityDamageByEntityEvent event) {
        // Optional to override
    }

    public void onDamageDealt(Player player, Entity target, UltraRaces event) {
        // Optional to override
    }

    public abstract void ActivateActiveSkill(Player player);

    public boolean ShouldActivateItemSkill(Player player) {
        return false;
    }
    public void onMobTarget(Player player, EntityTargetLivingEntityEvent event){

    }

    public boolean isPlayerThisRace(Player player) {
        return plugin.getConfig().getString("races." + player.getUniqueId(), "").equalsIgnoreCase(getRaceName());
    }

    public boolean isOnCooldown(Player player, String skillName, boolean notify) {
        return me.negan.ultraraces.Utils.Methods.isOnCooldown(player, plugin, skillName, notify);
    }

    public void ContinuousPassiveEffect(Player player) {
        // Optional to override
    }

    public void onFireDamage(Player player, EntityDamageEvent event){

    }
    public void onFireTick(Player player){

    }
    public void onBlock(Player player, EntityDamageByEntityEvent event) {

    }
    public void onKill(Player player, Entity target){

    }
}

