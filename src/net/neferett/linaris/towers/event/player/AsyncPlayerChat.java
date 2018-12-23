package net.neferett.linaris.towers.event.player;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;
import net.neferett.linaris.towers.handler.Team;

public class AsyncPlayerChat extends TowersListener {
    public AsyncPlayerChat(final TowersPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final Team playerTeam = Team.getPlayerTeam(player);
        event.setFormat((playerTeam != null ? playerTeam.getColor() : ChatColor.GRAY) + player.getName() + ChatColor.WHITE + ": " + event.getMessage());
    }
}
