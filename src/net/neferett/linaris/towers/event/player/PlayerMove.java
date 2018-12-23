package net.neferett.linaris.towers.event.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;
import net.neferett.linaris.towers.handler.Step;
import net.neferett.linaris.towers.handler.Team;

public class PlayerMove extends TowersListener {
    public PlayerMove(final TowersPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Location from = event.getFrom();
        final Location to = event.getTo();
        final int y = to.getBlockY();
        if (from.getBlockY() != y) {
            Team playerTeam = null;
            if (!Step.isStep(Step.IN_GAME) || (playerTeam = Team.getPlayerTeam(player)) == null) {
                if (y <= 0) {
                    player.teleport(Step.isStep(Step.LOBBY) || playerTeam == null ? this.plugin.lobbyLocation : playerTeam.getSpawnLocation());
                }
            } else {
                for (final Team team : Team.values()) {
                    if (team == playerTeam) {
                        continue;
                    }
                    if (team.getGoal().contains(to)) {
                        this.plugin.playerAddPoint(player);
                    }
                }
            }
        }
    }
}
