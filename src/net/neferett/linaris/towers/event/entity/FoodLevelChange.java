package net.neferett.linaris.towers.event.entity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;
import net.neferett.linaris.towers.handler.Step;

public class FoodLevelChange extends TowersListener {
    public FoodLevelChange(final TowersPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onFoodLevelChange(final FoodLevelChangeEvent event) {
        if (Step.isStep(Step.LOBBY)) {
            event.setCancelled(true);
        }
    }
}
