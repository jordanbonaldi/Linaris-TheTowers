package net.neferett.linaris.towers.event.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;
import net.neferett.linaris.towers.handler.Step;

public class EntityDamage extends TowersListener {
    public EntityDamage(final TowersPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event) {
        if (Step.isStep(Step.LOBBY)) {
            event.setCancelled(true);
        }
    }
}
