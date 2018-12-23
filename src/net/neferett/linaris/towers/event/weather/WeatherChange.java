package net.neferett.linaris.towers.event.weather;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;

public class WeatherChange extends TowersListener {
    public WeatherChange(final TowersPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onWeatherChange(final WeatherChangeEvent event) {
        final World world = event.getWorld();
        if (!world.isThundering() && !world.hasStorm()) {
            event.setCancelled(true);
        }
    }
}
