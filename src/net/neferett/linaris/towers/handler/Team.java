package net.neferett.linaris.towers.handler;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import net.neferett.linaris.towers.TowersPlugin;
import net.neferett.linaris.towers.util.Cuboid;
import net.neferett.linaris.towers.util.ItemBuilder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

@SuppressWarnings("deprecation")
public enum Team {
    BLUE("blue", "Bleue", Material.INK_SACK, DyeColor.BLUE.getDyeData(), ChatColor.BLUE, Color.BLUE),
    RED("red", "Rouge", Material.INK_SACK, DyeColor.RED.getDyeData(), ChatColor.RED, Color.RED);

    public static Team getPlayerTeam(final Player player) {
        if (player == null) return null;
        else if (!player.hasMetadata("team")) {
            for (final Team team : Team.values()) {
                if (team.craftTeam.getPlayers().contains(player)) return team;
            }
        } else {
            final String teamName = player.getMetadata("team").get(0).asString();
            for (final Team team : Team.values()) {
                if (team.name.equals(teamName)) return team;
            }
        }
        return null;
    }

    public static Team getRandomTeam() {
        return Team.BLUE.getOnlinePlayers().size() < Team.RED.getOnlinePlayers().size() ? Team.BLUE : Team.RED;
    }

    public static Team getTeam(final String name) {
        for (final Team team : Team.values()) {
            if (team.craftTeam != null && team.craftTeam.getName().equalsIgnoreCase(name)) return team;
        }
        return null;
    }

    public static Team getTeam(final ChatColor color) {
        for (final Team team : Team.values()) {
            if (team.color == color) return team;
        }
        return null;
    }

    @Getter
    private String name;
    @Getter
    private final String displayName;
    @Getter
    private ItemStack icon;
    @Getter
    private final ChatColor color;
    @Getter
    private final Color leatherColor;
    private org.bukkit.scoreboard.Team craftTeam;
    @Getter
    @Setter
    private Location spawnLocation;
    @Getter
    @Setter
    private Location goalLocation;
    @Getter
    private int points;
    @Getter
    @Setter
    private Cuboid goal;

    private Team(final String name, final String displayName, final Material material, final short durability, final ChatColor color, final Color leatherColor) {
        this.name = name;
        this.displayName = displayName;
        if (material != null) {
            this.icon = new ItemBuilder(material, 1, durability).setTitle(color + "Rejoindre l'équipe " + displayName).build();
        }
        this.color = color;
        this.leatherColor = leatherColor;
    }

    public void addPoint() {
        final Score score = this.getScore();
        score.setScore(this.points + 1);
        this.points += 1;
    }

    public void addPlayer(final Player player) {
        player.setMetadata("team", new FixedMetadataValue(TowersPlugin.i, this.name));
        this.craftTeam.addPlayer(player);
        if (Step.isStep(Step.LOBBY)) {
            final Score score = this.getScore();
            score.setScore(score.getScore() + 1);
        }
    }

    public void removePlayer(final Player player) {
        player.removeMetadata("team", TowersPlugin.i);
        this.craftTeam.removePlayer(player);
        if (Step.isStep(Step.LOBBY)) {
            final Score score = this.getScore();
            score.setScore(score.getScore() - 1);
        }
    }

    public Score getScore() {
        final Score objScore = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("teams").getScore(this.color + "Equipe " + this.displayName);
        return objScore;
    }

    public void setScore(final int score) {
        final Score objScore = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("teams").getScore(this.color + "Equipe " + this.displayName);
        if (score == 0) {
            objScore.setScore(1);
        }
        objScore.setScore(score);
    }

    public Set<Player> getOnlinePlayers() {
        final Set<Player> players = new HashSet<>();
        for (final OfflinePlayer offline : this.craftTeam.getPlayers()) {
            if (offline instanceof Player) {
                players.add((Player) offline);
            }
        }
        return players;
    }

    public void broadcastMessage(final String msg) {
        for (final Player player : this.getOnlinePlayers()) {
            player.sendMessage(msg);
        }
    }

    public void createTeam(final Scoreboard scoreboard) {
        this.craftTeam = scoreboard.getTeam(this.name);
        if (this.craftTeam == null) {
            this.craftTeam = scoreboard.registerNewTeam(this.name);
        }
        this.craftTeam.setPrefix(this.color.toString());
        this.craftTeam.setDisplayName(this.name);
        this.craftTeam.setAllowFriendlyFire(false);
    }
}
