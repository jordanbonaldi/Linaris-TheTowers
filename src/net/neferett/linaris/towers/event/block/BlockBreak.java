package net.neferett.linaris.towers.event.block;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;
import net.neferett.linaris.towers.handler.Step;
import net.neferett.linaris.towers.handler.Team;

public class BlockBreak extends TowersListener {
    public BlockBreak(final TowersPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Team team = Team.getPlayerTeam(player);
        if (Step.isStep(Step.LOBBY) || team == null) {
            if (!player.isOp()) {
                event.setCancelled(true);
            }
        } else if (event.getBlock().getType().equals(Material.CHEST)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Vous ne pouvez pas casser ce coffre !");
        }
    }
}
