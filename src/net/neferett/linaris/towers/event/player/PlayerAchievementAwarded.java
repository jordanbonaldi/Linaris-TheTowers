package net.neferett.linaris.towers.event.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;

public class PlayerAchievementAwarded extends TowersListener {
    public PlayerAchievementAwarded(final TowersPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerAchievementArwarded(final PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }
}
