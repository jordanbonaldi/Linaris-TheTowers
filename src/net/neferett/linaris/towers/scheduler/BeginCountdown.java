package net.neferett.linaris.towers.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.handler.Step;
import net.neferett.linaris.towers.handler.Team;
import net.neferett.linaris.towers.util.ItemBuilder;

public class BeginCountdown extends BukkitRunnable {
    public static boolean started = false;
    public static int timeUntilStart = 60;

    public BeginCountdown(final TowersPlugin plugin) {
        BeginCountdown.started = true;
        this.runTaskTimer(plugin, 0l, 20l);
    }

    @Override
    public void run() {
        if (BeginCountdown.timeUntilStart == 0) {
            this.cancel();
            if (Bukkit.getOnlinePlayers().length < 2) {
                Bukkit.broadcastMessage(TowersPlugin.prefix + ChatColor.RED + "Il n'y a pas assez de joueurs !");
                BeginCountdown.timeUntilStart = 120;
                BeginCountdown.started = false;
            } else {
                Bukkit.broadcastMessage(TowersPlugin.prefix + ChatColor.AQUA + "La partie vient de commencer, bon jeu !");
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    Team team = Team.getPlayerTeam(player);
                    if (team == null) {
                        team = Team.getRandomTeam();
                        if (team == null) {
                            player.kickPlayer(ChatColor.RED + "Impossible de trouver une équipe libre.");
                            continue;
                        }
                        team.addPlayer(player);
                    }
                    BeginCountdown.resetPlayer(player, team.getLeatherColor());
                    player.teleport(team.getSpawnLocation());
                }
                final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
                scoreboard.registerNewObjective("kills", "playerKillCount").setDisplaySlot(DisplaySlot.PLAYER_LIST);
                for (final Team team : Team.values()) {
                    team.setScore(0);
                }
                Step.setCurrentStep(Step.IN_GAME);
            }
            return;
        }
        final int remainingMins = BeginCountdown.timeUntilStart / 60 % 60;
        final int remainingSecs = BeginCountdown.timeUntilStart % 60;
        if (BeginCountdown.timeUntilStart % 30 == 0 || remainingMins == 0 && (remainingSecs % 10 == 0 || remainingSecs < 10)) {
            Bukkit.broadcastMessage(TowersPlugin.prefix + ChatColor.GOLD + "Démarrage du jeu dans " + ChatColor.YELLOW + (remainingMins > 0 ? remainingMins + " minute" + (remainingMins > 1 ? "s" : "") : "") + (remainingSecs > 0 ? (remainingMins > 0 ? " " : "") + remainingSecs + " seconde" + (remainingSecs > 1 ? "s" : "") : "") + ".");
            if (remainingMins == 0 && remainingSecs <= 10) {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), remainingSecs == 1 ? Sound.ANVIL_LAND : Sound.CLICK, 1f, 1f);
                }
            }
        }
        BeginCountdown.timeUntilStart--;
    }

    public static void resetPlayer(final Player player, final Color color) {
        player.setFireTicks(0);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExhaustion(5.0F);
        player.setFallDistance(0);
        player.setExp(0.0F);
        player.setLevel(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.closeInventory();
        for (final PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setLeatherColor(color).build());
        player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherColor(color).build());
        player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setLeatherColor(color).build());
        player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setLeatherColor(color).build());
        player.getInventory().addItem(new ItemStack(Material.BAKED_POTATO, 8));
    }
}
