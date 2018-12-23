package net.neferett.linaris.towers.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;
import net.neferett.linaris.towers.handler.Step;
import net.neferett.linaris.towers.handler.Team;
import net.neferett.linaris.towers.scheduler.BeginCountdown;

public class PlayerRespawn extends TowersListener {
    public PlayerRespawn(final TowersPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        final Team team = Team.getPlayerTeam(player);
        if (Step.isStep(Step.LOBBY) || team == null) {
            event.setRespawnLocation(this.plugin.lobbyLocation);
        } else {
            event.setRespawnLocation(team.getSpawnLocation());
            new BukkitRunnable() {
                @Override
                public void run() {
                    BeginCountdown.resetPlayer(player, team.getLeatherColor());
                }
            }.runTaskLater(this.plugin, 1l);
        }
    }
}
