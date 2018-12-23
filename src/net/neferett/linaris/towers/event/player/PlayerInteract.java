package net.neferett.linaris.towers.event.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.event.TowersListener;
import net.neferett.linaris.towers.handler.Step;
import net.neferett.linaris.towers.handler.Team;
import net.neferett.linaris.towers.util.MathUtils;

public class PlayerInteract extends TowersListener {
    public PlayerInteract(final TowersPlugin plugin) {
        super(plugin);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (Step.isStep(Step.LOBBY)) {
            if (event.hasItem()) {
                final ItemStack item = event.getItem();
                if (item.getType() == Material.INK_SACK && item.hasItemMeta()) {
                    for (final Team team : Team.values()) {
                        if (item.isSimilar(team.getIcon())) {
                            final String displayName = team.getDisplayName();
                            final Team playerTeam = Team.getPlayerTeam(player);
                            if (playerTeam != team) {
                                if (Bukkit.getOnlinePlayers().length > 1 && team.getOnlinePlayers().size() >= MathUtils.ceil(Bukkit.getOnlinePlayers().length / 2)) {
                                    player.sendMessage(TowersPlugin.prefix + ChatColor.GRAY + "Impossible de rejoindre cette équipe, trop de joueurs !");
                                } else {
                                    if (playerTeam != null) {
                                        playerTeam.removePlayer(player);
                                    }
                                    team.addPlayer(player);
                                    player.sendMessage(TowersPlugin.prefix + ChatColor.GRAY + "Vous rejoignez l'équipe " + team.getColor() + displayName);
                                }
                            }
                            break;
                        }
                    }
                    player.updateInventory();
                    return;
                }
            }
            if (!player.isOp()) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
