package net.neferett.linaris.towers;

import java.io.File;
import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import lombok.SneakyThrows;
import net.neferett.linaris.towers.event.TowersListener;
import net.neferett.linaris.towers.event.block.BlockBreak;
import net.neferett.linaris.towers.event.block.BlockPlace;
import net.neferett.linaris.towers.event.entity.CreatureSpawn;
import net.neferett.linaris.towers.event.entity.EntityDamage;
import net.neferett.linaris.towers.event.entity.EntityDamageByPlayer;
import net.neferett.linaris.towers.event.entity.FoodLevelChange;
import net.neferett.linaris.towers.event.player.AsyncPlayerChat;
import net.neferett.linaris.towers.event.player.PlayerAchievementAwarded;
import net.neferett.linaris.towers.event.player.PlayerCommandPreprocess;
import net.neferett.linaris.towers.event.player.PlayerDamage;
import net.neferett.linaris.towers.event.player.PlayerDeath;
import net.neferett.linaris.towers.event.player.PlayerDropItem;
import net.neferett.linaris.towers.event.player.PlayerInteract;
import net.neferett.linaris.towers.event.player.PlayerJoin;
import net.neferett.linaris.towers.event.player.PlayerKick;
import net.neferett.linaris.towers.event.player.PlayerLogin;
import net.neferett.linaris.towers.event.player.PlayerMove;
import net.neferett.linaris.towers.event.player.PlayerPickupItem;
import net.neferett.linaris.towers.event.player.PlayerQuit;
import net.neferett.linaris.towers.event.player.PlayerRespawn;
import net.neferett.linaris.towers.event.server.ServerListPing;
import net.neferett.linaris.towers.event.weather.ThunderChange;
import net.neferett.linaris.towers.event.weather.WeatherChange;
import net.neferett.linaris.towers.handler.MySQL;
import net.neferett.linaris.towers.handler.PlayerData;
import net.neferett.linaris.towers.handler.Step;
import net.neferett.linaris.towers.handler.Team;
import net.neferett.linaris.towers.scheduler.HubTeleportation;
import net.neferett.linaris.towers.util.Cuboid;
import net.neferett.linaris.towers.util.FileUtils;
import net.neferett.linaris.towers.util.ReflectionHandler;
import net.neferett.linaris.towers.util.ReflectionHandler.PackageType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

@SuppressWarnings("unchecked")
public class TowersPlugin extends JavaPlugin {
    public static TowersPlugin i;
    public static String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "TW" + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + " ";
    public static Random random = new Random();

    private World world;
    public MySQL database;
    public Location lobbyLocation;
    private final Map<UUID, PlayerData> data = new HashMap<>();

    @SneakyThrows
    @Override
    public void onLoad() {
        Bukkit.unloadWorld("world", false);
        final File worldContainer = this.getServer().getWorldContainer();
        final File worldFolder = new File(worldContainer, "world");
        final File copyFolder = new File(worldContainer, "the-towers");
        if (copyFolder.exists()) {
            ReflectionHandler.getClass("RegionFileCache", PackageType.MINECRAFT_SERVER).getMethod("a").invoke(null);
            FileUtils.delete(worldFolder);
            FileUtils.copyFolder(copyFolder, worldFolder);
        }
    }

    @Override
    public void onEnable() {
        TowersPlugin.i = this;
        Step.setCurrentStep(Step.LOBBY);
        this.world = Bukkit.getWorlds().get(0);
        this.load();
        this.database = new MySQL(this, this.getConfig().getString("mysql.host"), this.getConfig().getString("mysql.port"), this.getConfig().getString("mysql.database"), this.getConfig().getString("mysql.user"), this.getConfig().getString("mysql.pass"));
        try {
            this.database.openConnection();
            this.database.updateSQL("CREATE TABLE IF NOT EXISTS `players` ( `id` int(11) NOT NULL AUTO_INCREMENT, `name` varchar(30) NOT NULL, `uuid` varbinary(16) NOT NULL, `coins` double NOT NULL, `sw_more_health` int(11) DEFAULT '0' NOT NULL, `sw_better_bow` int(11) DEFAULT '0' NOT NULL, `sw_better_sword` int(11) DEFAULT '0' NOT NULL, `sw_mobility` int(11) DEFAULT '0' NOT NULL, `sw_more_sheep` int(11) DEFAULT '0' NOT NULL, `created_at` datetime NOT NULL, `updated_at` datetime NOT NULL, PRIMARY KEY (`id`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;");
        } catch (ClassNotFoundException | SQLException e) {
            this.getLogger().severe("Impossible de se connecter à la base de données :");
            e.printStackTrace();
            this.getLogger().severe("Arrêt du serveur...");
            Bukkit.shutdown();
            return;
        }
        this.register(BlockBreak.class, BlockPlace.class, CreatureSpawn.class, EntityDamage.class, EntityDamageByPlayer.class, FoodLevelChange.class, AsyncPlayerChat.class, PlayerAchievementAwarded.class, PlayerCommandPreprocess.class, PlayerDamage.class, PlayerDeath.class, PlayerDropItem.class, PlayerInteract.class, PlayerJoin.class, PlayerKick.class, PlayerLogin.class, PlayerMove.class, PlayerPickupItem.class, PlayerQuit.class, PlayerRespawn.class, ServerListPing.class, ThunderChange.class, WeatherChange.class);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onDisable() {
        this.save();
    }

    @SneakyThrows
    private void register(final Class<? extends TowersListener>... classes) {
        for (final Class<? extends TowersListener> clazz : classes) {
            final Constructor<? extends TowersListener> constructor = clazz.getConstructor(TowersPlugin.class);
            Bukkit.getPluginManager().registerEvents(constructor.newInstance(this), this);
        }
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (command.getName().equalsIgnoreCase("towers")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Vous devez être un joueur.");
                return true;
            }
            final Player player = (Player) sender;
            if (args.length != 0){
                final String sub = args[0];
                if (sub.equalsIgnoreCase("help")) {
                    player.sendMessage(ChatColor.GOLD + "Aide du plugin The Towers :");
                    player.sendMessage("/towers setlobby" + ChatColor.YELLOW + " - définit le lobby du jeu");
                    player.sendMessage("/towers setspawn <couleur>" + ChatColor.YELLOW + " - définit le spawn de l'équipe <couleur>");
                    player.sendMessage("/towers setgoal <couleur>" + ChatColor.YELLOW + " - définit le but de l'équipe <couleur>");
                } else if (sub.equalsIgnoreCase("setlobby")) {
                    this.lobbyLocation = player.getLocation();
                    player.sendMessage(ChatColor.GREEN + "Vous avez défini le lobby avec succès.");
                    this.getConfig().set("lobby", this.toString(player.getLocation()));
                    this.saveConfig();
                } else if (sub.equalsIgnoreCase("setspawn")) {
                    if (!args[1].equalsIgnoreCase("red") && !args[1].equalsIgnoreCase("blue")) {
                        player.sendMessage(ChatColor.RED + "La couleur " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " n'existe pas.");
                    } else {
                        final Location location = player.getLocation();
                        final Team team = Team.getTeam(args[1]);
                        player.sendMessage(ChatColor.GREEN + "Vous avez défini avec succès le spawn de l'équipe " + team.getColor() + team.getDisplayName());
                        team.setSpawnLocation(location);
                        this.getConfig().set("teams." + args[1] + ".spawn", this.toString(location));
                        this.saveConfig();
                    }
                } else if (sub.equalsIgnoreCase("setgoal")) {
                    if (!args[1].equalsIgnoreCase("red") && !args[1].equalsIgnoreCase("blue")) {
                        player.sendMessage(ChatColor.RED + "La couleur " + ChatColor.DARK_RED + args[1] + ChatColor.RED + " n'existe pas.");
                    } else {
                        final Location location = player.getLocation();
                        final Team team = Team.getTeam(args[1]);
                        player.sendMessage(ChatColor.GREEN + "Vous avez défini avec succès le but de l'équipe " + team.getColor() + team.getDisplayName());
                        team.setGoalLocation(location);
                        this.getConfig().set("teams." + args[1] + ".goal", this.toString(location));
                        this.saveConfig();
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Mauvais arguments ou commande inexistante. Tapez " + ChatColor.DARK_RED + "/towers help" + ChatColor.RED + " pour de l'aide.");
                }
                return true;
            }
        }
        return false;
    }

    public PlayerData getData(final Player player) {
        final PlayerData data = this.data.get(player.getUniqueId());
        if (data == null) {
            player.kickPlayer(ChatColor.RED + "Erreur");
            return null;
        }
        return data;
    }

    public void loadData(final Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    final ResultSet res = TowersPlugin.this.database.querySQL("SELECT * FROM players WHERE uuid=UNHEX('" + player.getUniqueId().toString().replaceAll("-", "") + "')");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            PlayerData data = null;
                            try {
                                if (res.first()) {
                                    data = new PlayerData(player.getUniqueId(), res.getString("name"), res.getInt("sw_more_health"), res.getInt("sw_better_bow"), res.getInt("sw_better_sword"), res.getInt("sw_more_sheep"), res.getInt("sw_mobility"), 0);
                                } else {
                                    data = new PlayerData(player.getUniqueId(), player.getName(), 0, 0, 0, 0, 0, 0);
                                }
                                TowersPlugin.this.data.put(player.getUniqueId(), data);
                            } catch (final SQLException e) {
                                player.kickPlayer(ChatColor.RED + "Impossible de charger vos statistiques... :(");
                            }
                        }
                    }.runTask(TowersPlugin.this);
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(this);
    }

    private void load() {
        this.saveDefaultConfig();
        final Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team.BLUE.createTeam(scoreboard);
        Team.RED.createTeam(scoreboard);
        final ConfigurationSection teams = this.getConfig().getConfigurationSection("teams");
        if (teams != null) {
            Objective objective = scoreboard.getObjective("teams");
            if (objective == null) {
                objective = scoreboard.registerNewObjective("teams", "dummy");
            }
            objective.setDisplayName(ChatColor.DARK_GRAY + "-" + ChatColor.YELLOW + "The Towers" + ChatColor.DARK_GRAY + "-");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            for (final String key : teams.getKeys(false)) {
                final Team team = Team.getTeam(key);
                final ConfigurationSection section = teams.getConfigurationSection(key);
                if (section.isString("spawn")) {
                    team.setSpawnLocation(this.toLocation(section.getString("spawn")));
                }
                if (section.isString("goal")) {
                    final Location goal = this.toLocation(section.getString("goal"));
                    team.setGoalLocation(this.toLocation(section.getString("goal")));
                    team.setGoal(Cuboid.createFromLocationRadius(goal, 1));
                }
                team.setScore(0);
            }
        }
        this.lobbyLocation = this.toLocation(this.getConfig().getString("lobby", this.toString(this.world.getSpawnLocation())));
    }

    private void save() {
        this.getConfig().set("lobby", this.toString(this.lobbyLocation));
        for (final Team team : Team.values()) {
            final String name = team.getName();
            if (team.getSpawnLocation() != null) {
                this.getConfig().set("teams." + name + ".spawn", this.toString(team.getSpawnLocation()));
            }
            if (team.getGoalLocation() != null) {
                this.getConfig().set("teams." + name + ".box", this.toString(team.getGoalLocation()));
            }
        }
        this.saveConfig();
    }

    public void teleportToLobby(final Player player) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF("lobby");
        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }

    public void playerAddPoint(final Player player) {
        final Team team = Team.getPlayerTeam(player);
        if (team != null) {
            team.addPoint();
            player.teleport(team.getSpawnLocation());
            Bukkit.broadcastMessage(TowersPlugin.prefix + team.getColor() + player.getName() + " a marqué " + ChatColor.GOLD + "1 point");
            this.getData(player).addCoins(1.25);
            if (team.getPoints() == 10) {
                Bukkit.broadcastMessage(TowersPlugin.prefix + ChatColor.GOLD + ChatColor.BOLD + "Victoire de l'équipe " + team.getColor() + ChatColor.BOLD + team.getDisplayName() + " " + ChatColor.YELLOW + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|" + ChatColor.YELLOW + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.BOLD + " Félicitations " + ChatColor.YELLOW + ChatColor.MAGIC + " |" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|" + ChatColor.YELLOW + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|");
                this.stopGame(team);
            }
        }
    }

    public void stopGame(final Team winnerTeam) {
        Step.setCurrentStep(Step.POST_GAME);
        for (final Player online : Bukkit.getOnlinePlayers()) {
            new HubTeleportation(this, online);
        }
        for (final Entry<UUID, PlayerData> entry : this.data.entrySet()) {
            final String uuid = entry.getKey().toString().replaceAll("-", "");
            final PlayerData data = entry.getValue();
            final Player online = Bukkit.getPlayer(entry.getKey());
            if (online != null && online.isOnline()) {
                if (Team.getPlayerTeam(online) == winnerTeam) {
                    data.addCoins(10, false);
                } else {
                    data.addCoins(2, false);
                }
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        final ResultSet res = TowersPlugin.this.database.querySQL("SELECT name FROM players WHERE uuid=UNHEX('" + uuid + "')");
                        if (res.first()) {
                            TowersPlugin.this.database.updateSQL("UPDATE players SET name='" + data.getName() + "', coins=coins+" + data.getCoins() + ", updated_at=NOW() WHERE uuid=UNHEX('" + uuid + "')");
                        } else {
                            TowersPlugin.this.database.updateSQL("INSERT INTO players(name, uuid, coins, created_at, updated_at) VALUES('" + data.getName() + "', UNHEX('" + uuid + "'), " + data.getCoins() + ", NOW(), NOW())");
                        }
                    } catch (ClassNotFoundException | SQLException e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskAsynchronously(this);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (final Player online : Bukkit.getOnlinePlayers()) {
                    TowersPlugin.this.teleportToLobby(online);
                }
                Bukkit.shutdown();
            }
        }.runTaskLater(this, 300l);
    }

    public void removePlayer(final Player player) {
        if (Step.isStep(Step.LOBBY)) {
            this.data.remove(player.getUniqueId());
        }
        final Team team = Team.getPlayerTeam(player);
        if (team != null) {
            team.removePlayer(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (Step.isStep(Step.IN_GAME) && team.getOnlinePlayers().size() == 0) {
                        final Team winnerTeam = Team.BLUE == team ? Team.RED : Team.BLUE;
                        Bukkit.broadcastMessage(TowersPlugin.prefix + ChatColor.GOLD + ChatColor.BOLD + "Victoire de l'équipe " + winnerTeam.getColor() + ChatColor.BOLD + winnerTeam.getDisplayName() + " " + ChatColor.YELLOW + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|" + ChatColor.YELLOW + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.BOLD + " Félicitations " + ChatColor.YELLOW + ChatColor.MAGIC + " |" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|" + ChatColor.YELLOW + ChatColor.MAGIC + "|" + ChatColor.AQUA + ChatColor.MAGIC + "|" + ChatColor.GREEN + ChatColor.MAGIC + "|" + ChatColor.RED + ChatColor.MAGIC + "|" + ChatColor.LIGHT_PURPLE + ChatColor.MAGIC + "|");
                        TowersPlugin.this.stopGame(winnerTeam);
                    }
                }
            }.runTaskLater(TowersPlugin.this, 1);
        }
    }

    private Location toLocation(final String string) {
        final String[] splitted = string.split("_");
        World world = Bukkit.getWorld(splitted[0]);
        if (world == null || splitted.length < 6) {
            world = this.world;
        }
        return new Location(world, Double.parseDouble(splitted[1]), Double.parseDouble(splitted[2]), Double.parseDouble(splitted[3]), Float.parseFloat(splitted[4]), Float.parseFloat(splitted[5]));
    }

    private String toString(final Location location) {
        final World world = location.getWorld();
        return world.getName() + "_" + location.getX() + "_" + location.getY() + "_" + location.getZ() + "_" + location.getYaw() + "_" + location.getPitch();
    }
}
