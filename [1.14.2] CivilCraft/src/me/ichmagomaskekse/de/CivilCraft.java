package me.ichmagomaskekse.de;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.ichmagomaskekse.de.commands.BackupCommand;
import me.ichmagomaskekse.de.commands.CadminCommand;
import me.ichmagomaskekse.de.commands.FlightCommand;
import me.ichmagomaskekse.de.commands.GamemodeCommand;
import me.ichmagomaskekse.de.commands.GlobalMuteCommand;
import me.ichmagomaskekse.de.commands.GodmodeCommand;
import me.ichmagomaskekse.de.commands.PermissionCheckerCommand;
import me.ichmagomaskekse.de.commands.WhoCommand;
import me.ichmagomaskekse.de.commands.healing.FeedCommand;
import me.ichmagomaskekse.de.commands.healing.HealCommand;
import me.ichmagomaskekse.de.commands.teleporting.SpawnCommand;
import me.ichmagomaskekse.de.filesystem.BackupManager;
import me.ichmagomaskekse.de.filesystem.FileManager;
import me.ichmagomaskekse.de.listener.BlockBreakListener;
import me.ichmagomaskekse.de.listener.ChatListener;
import me.ichmagomaskekse.de.listener.DamageListener;
import me.ichmagomaskekse.de.listener.PermListener;
import me.ichmagomaskekse.de.listener.ServerJoinAndLeaveListener;
import me.ichmagomaskekse.de.lobby.Lobby;
import me.ichmagomaskekse.de.permissions.PermissionList;
import me.ichmagomaskekse.de.permissions.PermissionManager;
import me.ichmagomaskekse.de.scoreboard.CScoreboard;
import me.ichmagomaskekse.de.scoreboard.CTablist;

public class CivilCraft extends JavaPlugin {
	
	
	private static CivilCraft ccraft = null;
	public static CivilCraft getInstance() { return ccraft; }
	public static boolean global_mute = false;
	
	//Instanzen: Manager, Handler, etc
	public FileManager filemanager = null;
	public PermissionManager permissionmanager = null;
	public PlayerAtlas playeratlas = null;
	public ProfileManager profileManager = null;
	public static SpawnCommand command_spawn = null;
	public BackupManager backupManager = null;
	public CScoreboard csb = null;
	public CTablist ctl = null;
	
	public static Lobby mainLobby = new Lobby(1);
	
	public CivilCraft() {}
	
	@Override
	public void onEnable() {
		ccraft = this;
		preInit();
		init();
		postInit();
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		// TODO:
		PermissionManager.disable();
		ProfileManager.disable();
		super.onDisable();
	}
	
	public void preInit() {
		//Dateien werden aus dem Resource Ordner in den Plugins Ordner gespeichert
		File groups = new File("plugins/CivilCraft/Permissions/groups.yml");
		File players = new File("plugins/CivilCraft/Permissions/players.yml");
										saveResource("civilcraft.yml", false);
		if(groups.exists() == false)	saveResource("Permissions/groups.yml", false);
		if(players.exists() == false)	saveResource("Permissions/players.yml", false);
										saveResource("player_atlas.yml", false);
										saveResource("Teleportation/spawn.yml", false);
	}
	public void init() {
		playeratlas = new PlayerAtlas();
		filemanager = new FileManager();
		permissionmanager = new PermissionManager();
		profileManager = new ProfileManager();
		backupManager = new BackupManager();
		backupManager.createBackup();
		csb = new CScoreboard();
		ctl = new CTablist();
		
		command_spawn = new SpawnCommand();
	}
	public void postInit() {
		//Events
		new ServerJoinAndLeaveListener();
		new ChatListener();
		new BlockBreakListener();
		new DamageListener();
		new PermListener();
		
		//Commands
		getCommand("backup").setExecutor(new BackupCommand());
		getCommand("cadmin").setExecutor(new CadminCommand());
		getCommand("feed").setExecutor(new FeedCommand());
		getCommand("fly").setExecutor(new FlightCommand());
		getCommand("gamemode").setExecutor(new GamemodeCommand());
		getCommand("globalmute").setExecutor(new GlobalMuteCommand());
		getCommand("godmode").setExecutor(new GodmodeCommand());
		getCommand("heal").setExecutor(new HealCommand());
		getCommand("mute").setExecutor(new GlobalMuteCommand());
		getCommand("spawn").setExecutor(command_spawn);
		getCommand("setspawn").setExecutor(new SpawnCommand());
		getCommand("who").setExecutor(new WhoCommand());
		getCommand("permcheck").setExecutor(new PermissionCheckerCommand());
		
		//Permissions
		//Es können nur dann 2 Permissions für 1nen Command verteilt werden, wenn eine Permission davon
		//eine OP-Permission ist. Sprich: mit einem '.*' endet
		PermissionList.addPermission("backup", "civilcraft.admin.backup");
		PermissionList.addPermission("permcheck", "civilcraft.admin.permcheck");
		PermissionList.addPermission("chatcolor", "civilcraft.chat.colored");
		PermissionList.addPermission("godmode", "civilcraft.godmode");
		PermissionList.addPermission("godmode", "civilcraft.godmode.other");
		PermissionList.addPermission("who", "civilcraft.who");
		PermissionList.addPermission("fly", "civilcraft.fly");
		PermissionList.addPermission("fly", "civilcraft.fly.other");
		PermissionList.addPermission("mute", "civilcraft.chat.mute.other");
		PermissionList.addPermission("mute bypass", "civilcraft.chat.mute.bypass");
		PermissionList.addPermission("globalmute", "civilcraft.chat.mute");
		PermissionList.addPermission("globalmute bypass", "civilcraft.chat.mute.bypass");
		PermissionList.addPermission("modifyworld:everywhere", "civilcraft.modifyworld.everywhere");
		PermissionList.addPermission("interact_block:everything", "civilcraft.interact.block.everything");
		PermissionList.addPermission("interact_item:everything", "civilcraft.interact.item.everything");
		PermissionList.addPermission("cadmin", "civilcraft.admin");
		PermissionList.addPermission("cadmin reload", "civilcraft.admin.reload");
		PermissionList.addPermission("cadmin perms", "civilcraft.admin.permissions");
		PermissionList.addPermission("cadmin perms list", "civilcraft.admin.permissions.list");
		PermissionList.addPermission("cadmin perms <PLAYER>", "civilcraft.admin.permissions.playerinfos");
		PermissionList.addPermission("cadmin perms <PLAYER> add", "civilcraft.admin.permissions.player.add");
		PermissionList.addPermission("cadmin perms <PLAYER> remove", "civilcraft.admin.permissions.player.remove");
		PermissionList.addPermission("cadmin perms groups", "civilcraft.admin.permissions.groups");
		PermissionList.addPermission("cadmin perms <GROUP>", "civilcraft.admin.permissions.groupinfos");
		PermissionList.addPermission("cadmin perms <GROUP> add", "civilcraft.admin.permissions.group.add");
		PermissionList.addPermission("cadmin perms <GROUP> remove", "civilcraft.admin.permissions.group.remove");
		PermissionList.addPermission("cadmin perms <GROUP> create", "civilcraft.admin.permissions.group.create");
		PermissionList.addPermission("cadmin perms <GROUP> prefix set", "civilcraft.admin.permissions.group.prefix.set");
		PermissionList.addPermission("cadmin perms <GROUP> suffix set", "civilcraft.admin.permissions.group.suffix.set");
		PermissionList.addPermission("spawn", "civilcraft.spawn");
		PermissionList.addPermission("setspawn", "civilcraft.setspawn");
		PermissionList.addPermission("heal", "civilcraft.heal");
		PermissionList.addPermission("heal other", "civilcraft.heal.other");
		PermissionList.addPermission("feed", "civilcraft.feed");
		PermissionList.addPermission("feed other", "civilcraft.feed.other");
		PermissionList.addPermission("gamemode", "civilcraft.gamemode");
		PermissionList.addPermission("gamemode other", "civilcraft.gamemode.other");
		
		//Permission Profile laden
//		PermissionManager.loadPermPlayers();
		//Spawn Location laden
		command_spawn.loadSpawnLocation();
		
		for(Player all : Bukkit.getOnlinePlayers()) {
			csb.setNewScoreboard(all);
			ctl.setTablist(all);
		}
	}
	
	//Registriert die Events bei Bukkit
	public static void registerEvents(Listener listener) {
		ccraft.getServer().getPluginManager().registerEvents(listener, getInstance());
	}
	
	//Sende Nachrichten mit Prefix an einem oder alle Spieler
	public static void sendInfo(CommandSender sender, String prefix, String... message) {
		if(prefix.equals("null") || prefix.equals("")) prefix = FileManager.server_prefix;
		for(String msg : message) {
			sender.sendMessage("§7["+prefix+"§7] §f"+msg);
		}
	}
	public static void sendErrorInfo(CommandSender sender, String prefix, String... message) {
		if(prefix.equals("null") || prefix.equals("")) prefix = FileManager.server_prefix;
		for(String msg : message) {
			sender.sendMessage("§7["+prefix+"§7] §c"+msg);
		}
	}
	public static void sendBroadcast(String prefix, String... message) {
		if(prefix.equals("null") || prefix.equals("")) prefix = FileManager.server_prefix;
		for(String msg : message) {
			for(Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage("§7["+prefix+"§7] §c"+msg);
			}
			Bukkit.getConsoleSender().sendMessage("§7["+prefix+"§7] §c"+msg);
		}
	}
	
	public static void sendDeveloperInformation(String prefix, String... message) {
		if(prefix.equals("null") || prefix.equals("")) prefix = FileManager.server_prefix;
		for(String msg : message) {
			try{Bukkit.getPlayer("IchMagOmasKekse").sendMessage("§7["+prefix+"§7] [§9DEV§7] §c"+msg);}catch(NullPointerException ex){}
			Bukkit.getConsoleSender().sendMessage("§7["+prefix+"§7] [§3DEV§7] §c"+msg);
		}
	}

	public static void reload(CommandSender sender) {
		//Datein neu erstellen, falls sie nichtmehr existieren
		CivilCraft.sendInfo(sender, "", "[1/3] Lade Daten neu...");
		CivilCraft.sendInfo(sender, "", "[2/3] Auslesen...");
		FileManager.reloadData();
		PlayerAtlas.registerOnlinePlayers();
		PermissionManager.reloadData();
		ProfileManager.reloadData();
		command_spawn.loadSpawnLocation();
		CivilCraft.sendInfo(sender, "", "[3/3] Fertig!");
	}
	
	public static String getTimeInString() {
		SimpleDateFormat formatter= new SimpleDateFormat("HH-mm-ss");
		Date date = new Date(System.currentTimeMillis());
		return formatter.format(date);
	}
	public static String getDateInString() {
		SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date(System.currentTimeMillis());
		
		return formatter.format(date);
	}

	
}
