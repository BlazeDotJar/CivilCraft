package me.ichmagomaskekse.de;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.ichmagomaskekse.de.commands.CadminCommand;
import me.ichmagomaskekse.de.filesystem.FileManager;
import me.ichmagomaskekse.de.listener.BlockBreakListener;
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
	public PlayerAtlas playeratlas = null;
	
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
		super.onDisable();
	}
	
	public void preInit() {
		//Dateien werden aus dem Resource Ordner in den Plugins Ordner gespeichert
		File groups = new File("plugins/CivilCraft/Permissions/groups.yml");
		File players = new File("plugins/CivilCraft/Permissions/players.yml");
										saveResource("civilcraft.yml", false);
		if(groups.exists() == false)	saveResource("groups.yml", false);
		if(players.exists() == false)	saveResource("players.yml", false);
										saveResource("player_atlas.yml", false);
		moveResourceFiles();
	}
	public void init() {
		playeratlas = new PlayerAtlas();
		filemanager = new FileManager();
		permissionmanager = new PermissionManager();
	}
	public void postInit() {
		//Events
		new ServerJoinAndLeaveListener();
		new ChatListener();
		new BlockBreakListener();
		
		//Commands
		getCommand("cadmin").setExecutor(new CadminCommand());
		
		//Permissions
		//Es können nur dann 2 Permissions für 1nen Command verteilt werden, wenn eine Permission davon
		//eine OP-Permission ist. Sprich: mit einem '.*' endet
		PermissionList.addPermission("chat:colored", "civilcraft.chat.colored");
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
		
		//Permission Profile laden
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
		//Datein neu erstellen, falls sie nichtmehr existieren
		CivilCraft.sendInfo(sender, "", "[1/3] Lade Daten neu...");
		CivilCraft.sendInfo(sender, "", "[2/3] Auslesen...");
		FileManager.reloadData();
		PlayerAtlas.registerOnlinePlayers();
		PermissionManager.reloadData();
		CivilCraft.sendInfo(sender, "", "[3/3] Fertig!");
	}
	
	public static void moveResourceFiles() {
		//TODO: Muss programmiert werden und die Pfade müssen in PermissionManager und FileManager aktualisiert werden
		File groups = new File("plugins/CivilCraft/groups.yml");
		File players = new File("plugins/CivilCraft/players.yml");
		groups.renameTo(new File("plugins/CivilCraft/Permissions/groups.yml"));
		players.renameTo(new File("plugins/CivilCraft/Permissions/players.yml"));
		return;
	}
	
}
