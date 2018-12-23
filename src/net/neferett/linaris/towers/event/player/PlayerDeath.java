package net.neferett.linaris.towers.event.player;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;
import net.neferett.linaris.towers.handler.Step;
import net.neferett.linaris.towers.handler.Team;

public class PlayerDeath extends TowersListener {
    public PlayerDeath(final TowersPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        if (!Step.isStep(Step.IN_GAME)) {
            event.setDeathMessage(null);
            event.getDrops().clear();
            event.setDroppedExp(0);
        } else {
            final Player player = event.getEntity();
            final Player killer = player.getKiller();
            final Team killerTeam = Team.getPlayerTeam(killer);
            if (killer != null) {
                this.plugin.getData(killer).addCoins(0.25);
            }
            for (final ItemStack item : new ArrayList<>(event.getDrops())) {
                if (item.getType().name().contains("LEATHER_") || item.getType().name().contains("CHAINMAIL_")) {
                    event.getDrops().remove(item);
                }
            }
            event.setDeathMessage(TowersPlugin.prefix + Team.getPlayerTeam(player).getColor() + player.getName() + ChatColor.GRAY + " " + (killer == null ? "a succombé." : "a été tué par " + killerTeam.getColor() + killer.getName()));
        }
    }
}
