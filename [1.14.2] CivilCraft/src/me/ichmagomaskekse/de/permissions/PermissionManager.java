package me.ichmagomaskekse.de.permissions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.ichmagomaskekse.de.CivilCraft;

public class PermissionManager {
	
	private static String permission_path = "plugins/CivilCraft/permissions.yml";
	private static String player_data_path = "plugins/CivilCraft/players.yml";
	private static File permission_file = null;
	private static File player_data_file = null;
	private static FileConfiguration cfg = null;
	private static FileConfiguration player_data_cfg = null;
	private static ArrayList<String> registered_group_names = new ArrayList<String>();
	
	/*                     String = Groupname */
	private static HashMap<String, PermGroup> loaded_groups = new HashMap<String, PermGroup>();
	private static HashMap<UUID, PermPlayer> permPlayers = new HashMap<UUID, PermPlayer>();
	
	
	
	public PermissionManager() {
		reloadData();
	}
	
	//Prozess: Gruppen werden geladen
	public static void loadGroupNames() {
		PermissionManager.registered_group_names.clear();
		for(String s : PermissionManager.cfg.getStringList("Permissions.groups")) PermissionManager.registered_group_names.add(s);
	}
	public static void loadGroups() {
		PermissionManager.loaded_groups.clear();
		//Gruppen werden geladen
		for(String s : registered_group_names) {
			loaded_groups.put(s, new PermGroup(s));
		}
	}
	public static void loadGroupInherits() {
		for(String s : PermissionManager.loaded_groups.keySet()) {
			PermissionManager.loaded_groups.get(s).loadInherits();
		}
	}
	//-------
	
	public static boolean loadPermPlayers() {
		PermissionManager.permPlayers.clear();
		for(Player p : Bukkit.getOnlinePlayers()) {
			permPlayers.put(p.getUniqueId(), new PermPlayer(p.getUniqueId()));
		}
		return true;
	}
	public static void reloadData() {
		//Ladet alle Daten neu
		PermissionManager.permission_file = new File(PermissionManager.permission_path);
		PermissionManager.player_data_file = new File(PermissionManager.player_data_path);
		PermissionManager.cfg = YamlConfiguration.loadConfiguration(PermissionManager.permission_file);
		PermissionManager.player_data_cfg= YamlConfiguration.loadConfiguration(PermissionManager.player_data_file);
		loadGroupNames();
		loadGroups();
		loadGroupInherits();
		loadPermPlayers();
		CivilCraft.sendErrorInfo(Bukkit.getConsoleSender(), "", "Test");
		CivilCraft.sendErrorInfo(Bukkit.getConsoleSender(), "Permission Manager", "Es wurden folgende Gruppen geladen: "+PermissionManager.registered_group_names.toString());
		CivilCraft.sendErrorInfo(Bukkit.getConsoleSender(), "Permission Manager", "Es wurden folgende Spieler geladen: "+PermissionManager.permPlayers.toString());
	}
	//Ladet die PermPlayer Daten
	public static void loadPermPlayer(Player p) {
		permPlayers.put(p.getUniqueId(), new PermPlayer(p.getUniqueId()));
		for(UUID id : permPlayers.keySet()) p.sendMessage(id.toString());
	}
	
	public static boolean hasPermission(Player p, String command) {
		//TODO: MUSS GEUPDATET WERDEN; INHERITS PRÜFEN; PLAYER PRÜFEN;
		boolean bypass = false;
		bypass = p.hasPermission(PermissionList.getPermission(command));
		if(bypass == false) CivilCraft.sendInfo(p, "", "§cDu hast nicht das Recht dazu");
		return bypass;
	}
	
	public static PermPlayer getPermPlayer(UUID uuid) {
		if(permPlayers.containsKey(uuid)) return permPlayers.get(uuid);
		else return new PermPlayer(uuid);
	}
	
	public static class PermPlayer {
		//TODO:
		public UUID uuid = null;
		public ArrayList<PermGroup> inherit = new ArrayList<PermGroup>();
		public PermGroup group; //Es kann nur 1ne Gruppe zugeteilt werden, damit es keine Probleme beim Anzeigen des Prefixes gibt
		public ArrayList<String> permissions = new ArrayList<String>();
		public PermPlayer(UUID uuid) {
			this.uuid = uuid;
			loadData();
		}

		
		//Ein Spieler wird in den Player Index eingetragen
		public void registerPlayer(UUID uuid) {
			FileConfiguration player_data_cfg= YamlConfiguration.loadConfiguration(PermissionManager.player_data_file);
			
			ArrayList<String> list = new ArrayList<String>();
			player_data_cfg.set("Players."+uuid.toString()+".group", "player");
			list.clear();
			player_data_cfg.set("Players."+uuid.toString()+".permissions", list.clone());
			try {
				player_data_cfg.save(PermissionManager.player_data_file);
			} catch (IOException e) {e.printStackTrace();}
			
			CivilCraft.sendBroadcast("PermPlayer Loading", "§6"+uuid.toString()+" wurde registriert");
		}

		public boolean loadData() {
			@SuppressWarnings("unused")
			PermGroup pGroup = null;
			try{
				pGroup = new PermGroup(PermissionManager.player_data_cfg.getString("Players."+this.uuid.toString()+".group"));
			}catch(Exception ex) {registerPlayer(this.uuid);}
			
			group = new PermGroup(PermissionManager.player_data_cfg.getString("Players."+this.uuid.toString()+".group"));
			for(String s : PermissionManager.player_data_cfg.getStringList("Players."+this.uuid.toString()+".permissions")) {
				if(s == null) break;
				else permissions.add(s);
			}
			return true;
		}
	}
	
	public static class PermGroup {
		
		public String groupname = "";
		public String prefix = "";
		public String suffix = "";
		public ArrayList<String> permissions = new ArrayList<String>();
		public ArrayList<String> anti_permissions = new ArrayList<String>();
		public ArrayList<PermGroup> inherit = new ArrayList<PermGroup>();
		
		public PermGroup(String groupname)  {
			if(PermissionManager.registered_group_names.contains(groupname)) {
				this.groupname = groupname;
				loadData();
			}else{
				CivilCraft.sendErrorInfo(Bukkit.getConsoleSender(), "PermGroup", "Es wurde versucht die Gruppe '"+groupname+"' zu laden," ,"obwohl sie noch nicht existiert");
				return;
			}
		}
		
		public void loadData() {
			FileConfiguration c = PermissionManager.cfg;
			this.prefix = c.getString("Permissions."+groupname+".prefix");
			this.suffix = c.getString("Permissions."+groupname+".suffix");
			
			this.permissions = (ArrayList<String>) c.getStringList("Permissions."+groupname+".permissions");
			//Anti-Permissions werden aus den Permissions rausgefiltert
			for(String s : permissions) {
				if(s.startsWith("-")) {
					permissions.remove(s);
					anti_permissions.add(s);
				}
			}
			/*
			 * Inherits werden extra geladen
			 */
			CivilCraft.sendInfo(Bukkit.getConsoleSender(), "PermGroup", "Gruppe '"+groupname+"' wurde geladen");
		}
		public void loadInherits() {
			ArrayList<String> in = (ArrayList<String>) PermissionManager.cfg.getStringList("Permissions."+groupname+".inherit");
			for(String s : in) {
				inherit.add(PermissionManager.loaded_groups.get(s));
			}
		}
		
		public ArrayList<String> getPermissions() {
			return permissions;
		}
		public ArrayList<String> getAntiPermissions() {
			return anti_permissions;
		}
		
		//Gibt zurück ob die Gruppe eine spezielle Permission hat
		public boolean hasPermissions(String permission) {
			if(anti_permissions.contains(permission)) return false;
			else return permission.contains(permission);
		}
		
	}
}
