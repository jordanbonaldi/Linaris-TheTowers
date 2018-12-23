package net.neferett.linaris.towers.event.block;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;
import net.neferett.linaris.towers.handler.Step;

public class BlockPlace extends TowersListener {
    public BlockPlace(final TowersPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        if (Step.isStep(Step.LOBBY)) {
            if (!player.isOp()) {
                event.setCancelled(true);
            }
        }
    }
}
