package net.neferett.linaris.towers.event.entity;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;
import net.neferett.linaris.towers.handler.Step;
import net.neferett.linaris.towers.handler.Team;

public class EntityDamageByPlayer extends TowersListener {
    public EntityDamageByPlayer(final TowersPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityDamageByPlayer(final EntityDamageByEntityEvent event) {
        if (!Step.isStep(Step.IN_GAME) || event.getDamager() instanceof Player && Team.getPlayerTeam((Player) event.getDamager()) == null) {
            event.setCancelled(true);
        }
    }
}
