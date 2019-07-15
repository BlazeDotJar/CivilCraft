package me.ichmagomaskekse.de;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.ichmagomaskekse.de.commands.CadminCommand;
import me.ichmagomaskekse.de.filesystem.FileManager;
import me.ichmagomaskekse.de.listener.ChatListener;
import me.ichmagomaskekse.de.listener.ServerJoinAndLeaveListener;
import me.ichmagomaskekse.de.lobby.Lobby;
import me.ichmagomaskekse.de.permissions.PermissionList;
import me.ichmagomaskekse.de.permissions.PermissionManager;

public class CivilCraft extends JavaPlugin {
	
	/*
	 *  Dies ist die Lobby Klasse
	 */
	private static CivilCraft ccraft = null;
	public static CivilCraft getInstance() { return ccraft; }
	
	//Instanzen: Manager, Handler, etc
	public FileManager filemanager = null;
	public PermissionManager permissionmanager = null;
	
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
		super.onDisable();
	}
	
	public void preInit() {
		//Dateien werden aus dem Resource Ordner in den Plugins Ordner gespeichert
		saveResource("civilcraft.yml", false);
		saveResource("groups.yml", false);
		saveResource("players.yml", false);
		saveResource("player_atlas.yml", false);
	}
	public void init() {
		new PlayerAtlas();
		filemanager = new FileManager();
		permissionmanager = new PermissionManager();
	}
	public void postInit() {
		//Events
		new ServerJoinAndLeaveListener();
		new ChatListener();
		
		//Commands
		getCommand("cadmin").setExecutor(new CadminCommand());
		
		//Permissions
		PermissionList.addPermission("cadmin", "civilcraft.admin");
		PermissionList.addPermission("cadmin reload", "civilcraft.admin.reload");
		PermissionList.addPermission("cadmin perms", "civilcraft.admin.permissions");
		PermissionList.addPermission("cadmin perms list", "civilcraft.admin.permissions.list");
		PermissionList.addPermission("cadmin perms <PLAYER>", "civilcraft.admin.permissions.playerinfos");
		PermissionList.addPermission("cadmin perms <PLAYER> add", "civilcraft.admin.permissions.add.player");
		PermissionList.addPermission("cadmin perms <PLAYER> remove", "civilcraft.admin.permissions.remove.player");
		PermissionList.addPermission("cadmin perms groups", "civilcraft.admin.permissions.groups");
		PermissionList.addPermission("cadmin perms <GROUP>", "civilcraft.admin.permissions.groupinfos");
		PermissionList.addPermission("cadmin perms <GROUP> add", "civilcraft.admin.permissions.add.group");
		PermissionList.addPermission("cadmin perms <GROUP> remove", "civilcraft.admin.permissions.remove.group");
		
		//Permissions
		PermissionManager.loadPermPlayers();
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

	public static void reload(CommandSender sender) {
		/*
		 * FileManager reloaden
		 * PermissionManager neu laden
		 * 
		 */
		CivilCraft.sendInfo(sender, "", "Lade Daten neu");
		FileManager.reloadData();
		PermissionManager.reloadData();
		PlayerAtlas.registerOnlinePlayers();
		CivilCraft.sendInfo(sender, "", "Daten wurden neu geladen!");
	}
	
}
