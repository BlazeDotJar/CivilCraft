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
import me.ichmagomaskekse.de.permissions.PermissionList;
import me.ichmagomaskekse.de.permissions.PermissionManager;
import me.ichmagomaskekse.de.permissions.PermissionManager.PermGroup;

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
		saveResource("permissions.yml", false);
	}
	public void init() {
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
		PermissionList.addPermission("cadmin", "civilcraft.cadmin");
		PermissionList.addPermission("cadmin reload", "civilcraft.cadmin.reload");
		PermissionList.addPermission("cadmin permissions", "civilcraft.cadmin.permissions");
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
	
}
