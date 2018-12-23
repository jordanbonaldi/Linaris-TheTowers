package net.neferett.linaris.towers.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import net.neferett.linaris.towers.TowersPlugin;

import org.bukkit.event.Listener;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class TowersListener implements Listener {
    protected TowersPlugin plugin;
}
