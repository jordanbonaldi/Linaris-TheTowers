package net.neferett.linaris.towers.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;
import net.neferett.linaris.towers.handler.Step;
import net.neferett.linaris.towers.handler.Team;

public class PlayerPickupItem extends TowersListener {
    public PlayerPickupItem(final TowersPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
        if (Step.isStep(Step.LOBBY) || Team.getPlayerTeam(event.getPlayer()) == null) {
            event.setCancelled(true);
        }
    }
}
