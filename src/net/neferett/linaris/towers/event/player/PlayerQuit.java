package net.neferett.linaris.towers.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;

public class PlayerQuit extends TowersListener {
    public PlayerQuit(final TowersPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        event.setQuitMessage(null);
        final Player player = event.getPlayer();
        player.getInventory().clear();
        this.plugin.removePlayer(player);
    }
}
