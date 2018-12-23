package net.neferett.linaris.towers.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;
import net.neferett.linaris.towers.handler.Step;

public class PlayerLogin extends TowersListener {
    public PlayerLogin(final TowersPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerLogin(final PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        if (Step.canJoin() && event.getResult() == Result.KICK_FULL && player.hasPermission("games.vip")) {
            event.allow();
        } else if (!Step.canJoin() && !player.hasPermission("games.join")) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Step.getMOTD());
        }
    }
}
