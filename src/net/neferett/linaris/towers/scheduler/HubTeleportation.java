package net.neferett.linaris.towers.scheduler;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.neferett.linaris.towers.TowersPlugin;

public class HubTeleportation extends BukkitRunnable {
    private int timeUntilTeleporation = 10;
    private final TowersPlugin plugin;
    private final Player player;

    public HubTeleportation(final TowersPlugin plugin, final Player player) {
        this.plugin = plugin;
        this.player = player;
        this.runTaskTimer(plugin, 0l, 20l);
    }

    @Override
    public void run() {
        if (this.timeUntilTeleporation == 0) {
            this.cancel();
            this.plugin.teleportToLobby(this.player);
            return;
        }
        this.timeUntilTeleporation--;
    }
}
