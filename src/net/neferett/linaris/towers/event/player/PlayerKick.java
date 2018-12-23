package net.neferett.linaris.towers.event.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerKickEvent;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;
import net.neferett.linaris.towers.handler.Step;
import net.neferett.linaris.towers.handler.Team;

public class PlayerKick extends TowersListener {
    public PlayerKick(final TowersPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerKick(final PlayerKickEvent event) {
        final Player player = event.getPlayer();
        player.getInventory().clear();
        final Team team = Team.getPlayerTeam(player);
        event.setLeaveMessage((team == null ? ChatColor.GRAY : team.getColor()) + (!Step.isStep(Step.LOBBY) ? player.getName() + ChatColor.WHITE + " a quitté le jeu." : event.getPlayer().getName() + ChatColor.YELLOW + " a quitté " + ChatColor.DARK_PURPLE + "(" + (Bukkit.getOnlinePlayers().length - 1) + "/" + Bukkit.getMaxPlayers() + ")"));
        this.plugin.removePlayer(player);
    }
}
