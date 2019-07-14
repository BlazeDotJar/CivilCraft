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
	private static String index_path = "plugins/CivilCraft/players.yml";
	private static File permission_file = null;
	private static File index_file = null;
	private static FileConfiguration cfg = null;
	private static FileConfiguration index = null;
	private static ArrayList<String> registered_group_names = new ArrayList<String>();
	
	/*                     String = Groupname */
	private static HashMap<String, PermGroup> loaded_groups = new HashMap<String, PermGroup>();
	private static HashMap<UUID, PermPlayer> permPlayers = new HashMap<UUID, PermPlayer>();
	
	
	
	public PermissionManager() {
		permission_file = new File(PermissionManager.permission_path);
		index_file = new File(PermissionManager.index_path);
		cfg = YamlConfiguration.loadConfiguration(PermissionManager.permission_file);
		index = YamlConfiguration.loadConfiguration(PermissionManager.index_file);
		loadGroupNames();
		loadGroups();
		loadGroupInherits();
		loadPermPlayers();
	}
	
	
	//Prozess: Gruppen werden geladen
	public void loadGroupNames() {
		PermissionManager.registered_group_names.clear();
		for(String s : PermissionManager.cfg.getStringList("Permissions.groups")) PermissionManager.registered_group_names.add(s);
	}
	public void loadGroups() {
		//Gruppen werden geladen
		for(String s : registered_group_names) {
			loaded_groups.put(s, new PermGroup(s));
		}
	}
	public void loadGroupInherits() {
		for(String s : PermissionManager.loaded_groups.keySet()) {
			PermissionManager.loaded_groups.get(s).loadInherits();
		}
	}
	//-------
	
	public void loadPermPlayers() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			permPlayers.put(p.getUniqueId(), new PermPlayer(p.getUniqueId()));
		}
	}
	//Ladet die PermPlayer Daten
	public static void loadPermPlayer(Player p) {
		permPlayers.put(p.getUniqueId(), new PermPlayer(p.getUniqueId()));
	}
	
	public static boolean hasPermission(Player p, String command) {
		//TODO: MUSS GEUPDATET WERDEN; INHERITS PRÜFEN; PLAYER PRÜFEN;
		return p.hasPermission(PermissionList.getPermission(command));
	}
	
	public static class PermPlayer {
		//TODO:
		public UUID uuid = null;
		public ArrayList<PermGroup> inherit = new ArrayList<PermGroup>();
		public ArrayList<PermGroup> groups = new ArrayList<PermGroup>();
		public ArrayList<String> permissions = new ArrayList<String>();
		public PermPlayer(UUID uuid) {
			this.uuid = uuid;
			loadData();
		}

		
		//Ein Spieler wird in den Player Index eingetragen
		public void registerPlayer(UUID uuid) {
			FileConfiguration index = YamlConfiguration.loadConfiguration(new File("plugins/CivilCraft/players.yml"));
			
			ArrayList<String> list = new ArrayList<String>();
			list.add("player");
			index.set("Players."+uuid.toString()+".groups", list);
			list.clear();
			index.set("Players."+uuid.toString()+".permissions", list);
			try {
				index.save(new File("plugins/CivilCraft/players.yml"));
			} catch (IOException e) {e.printStackTrace();}
		}

		public void loadData() {
			if(PermissionManager.index.getStringList("Players."+this.uuid.toString()+".groups") == null)
				registerPlayer(this.uuid);
			groups.clear();
			for(String s : PermissionManager.index.getStringList("Players."+this.uuid.toString()+".groups")) {
				if(s == null) break;
				else groups.add(new PermGroup(s));
			}
			for(String s : PermissionManager.index.getStringList("Players."+this.uuid.toString()+".permissions")) {
				if(s == null) break;
				else permissions.add(s);
			}
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
