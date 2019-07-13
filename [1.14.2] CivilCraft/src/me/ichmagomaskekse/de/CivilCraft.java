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

public class CivilCraft extends JavaPlugin {
	
	/*
	 *  Dies ist die Lobby Klasse
	 */
	private static CivilCraft ccraft = null;
	public static CivilCraft getInstance() { return ccraft; }
	
	//Instanzen: Manager, Handler, etc
	public FileManager filemanager = null;
	
	public static Lobby mainLobby = new Lobby(1);
	
	public CivilCraft() {}
	
	@Override
	public void onEnable() {
		ccraft = this;
		saveDefaultConfig(); // config.yml wird in den Plugins Ordner gespeichert
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
		filemanager = new FileManager(true);
	}
	public void init() {
		
	}
	public void postInit() {
		new ServerJoinAndLeaveListener();
		new ChatListener();
		
		getCommand("cadmin").setExecutor(new CadminCommand());
		
		PermissionList.addPermission("cadmin", "civilcraft.cadmin");
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
