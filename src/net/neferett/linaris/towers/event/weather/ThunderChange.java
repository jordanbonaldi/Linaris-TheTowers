package net.neferett.linaris.towers.event.weather;

import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.ThunderChangeEvent;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;

public class ThunderChange extends TowersListener {
    public ThunderChange(final TowersPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onThunderChange(final ThunderChangeEvent event) {
        if (event.toThunderState()) {
            event.setCancelled(true);
        }
    }
}
