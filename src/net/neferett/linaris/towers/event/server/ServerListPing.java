package net.neferett.linaris.towers.event.server;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;
import net.neferett.linaris.towers.handler.Step;
import net.neferett.linaris.towers.handler.Team;

public class ServerListPing extends TowersListener {
    public ServerListPing(final TowersPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onServerListPing(final ServerListPingEvent event) {
        if (Step.isStep(Step.IN_GAME)) {
            event.setMotd(ChatColor.BLUE + "" + Team.BLUE.getPoints() + ChatColor.DARK_GRAY + ChatColor.BOLD + " - " + ChatColor.RED + Team.RED.getPoints());
        } else {
            event.setMotd(Step.getMOTD());
        }
    }
}
