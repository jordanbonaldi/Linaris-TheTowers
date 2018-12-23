package net.neferett.linaris.towers.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;
import net.neferett.linaris.towers.handler.Step;
import net.neferett.linaris.towers.handler.Team;

public class PlayerDamage extends TowersListener {
    public PlayerDamage(final TowersPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && (!Step.isStep(Step.IN_GAME) || Team.getPlayerTeam((Player) event.getEntity()) == null)) {
            event.setCancelled(true);
        }
    }
}
