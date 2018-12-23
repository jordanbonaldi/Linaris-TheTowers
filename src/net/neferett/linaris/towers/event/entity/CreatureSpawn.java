package net.neferett.linaris.towers.event.entity;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;

public class CreatureSpawn extends TowersListener {
    public CreatureSpawn(final TowersPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Monster || event.getEntity() instanceof Animals) {
            event.setCancelled(true);
        }
    }
}
