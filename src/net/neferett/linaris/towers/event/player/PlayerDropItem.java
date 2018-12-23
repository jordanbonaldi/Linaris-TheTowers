package net.neferett.linaris.towers.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;
import net.neferett.linaris.towers.handler.Step;

public class PlayerDropItem extends TowersListener {
    public PlayerDropItem(final TowersPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        if (Step.isStep(Step.LOBBY)) {
            event.setCancelled(true);
        }
    }
}
