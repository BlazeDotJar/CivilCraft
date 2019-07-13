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
	private static File permission_file = null;
	private static FileConfiguration cfg = null;
	private static ArrayList<String> registered_group_names = new ArrayList<String>();
	
	/*                     String = Groupname */
	private static HashMap<String, PermGroup> loaded_groups = new HashMap<String, PermGroup>();
	private static HashMap<UUID, PermPlayer> permPlayers = new HashMap<UUID, PermPlayer>();
	
	
	
	public PermissionManager() {
		permission_file = new File(PermissionManager.permission_path);
		cfg = YamlConfiguration.loadConfiguration(PermissionManager.permission_file);
		loadGroupNames();
		loadGroups();
		loadGroupInherits();
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
			permPlayers.put(p.getUniqueId(), new PermPlayer(p.getName()));
		}
	}
	//Ladet die PermPlayer Daten
	public static void loadPermPlayer(Player player) {
		permPlayers.put(player.getUniqueId(), new PermPlayer(player.getName()));
	}
	
	public static boolean hasPermission(Player p, String command) {
		//TODO: MUSS GEUPDATET WERDEN; INHERITS PRÜFEN; PLAYER PRÜFEN;
		return p.hasPermission(PermissionList.getPermission(command));
	}
	
	public static class PermPlayer {
		//TODO:
		public UUID uuid = null;
		public String playername = "";
		public ArrayList<PermGroup> inherit = new ArrayList<PermGroup>();
		public PermGroup permGroup = null;		
		public PermPlayer(UUID uuid) {
			this.uuid = uuid;
			try{
				this.playername = PermissionManager.cfg.getString("Players."+uuid.toString());
			}catch(Exception ex) {
				if(this.uuid == null || this.playername == null) registerPlayer(playername);
			}
			loadData();
		}
		public PermPlayer(String playername) {
			this.playername = playername;
			try{
				this.uuid = UUID.fromString(PermissionManager.cfg.getString("Players."+playername));
			}catch(Exception ex) {
				if(this.uuid == null || this.playername == null) registerPlayer(playername);
			}
			loadData();
		}
		
		//Ein Spieler wird in den Player Index eingetragen
		public void registerPlayer(UUID uuid) {
			FileConfiguration c = PermissionManager.cfg;
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(p.getUniqueId().toString().equals(uuid.toString())) {
					c.set("Players."+uuid.toString(), p.getName());
					c.set("Players."+p.getName(), uuid.toString());
					this.playername = p.getName();
					this.uuid = p.getUniqueId();
					break;
				}
			}
			ArrayList<String> groups = new ArrayList<String>();
			groups.add("none");
			c.set("Players.groups", groups);
			
			ArrayList<String> perms = new ArrayList<String>();
			perms.add("none");
			c.set("Players.permissions", perms);
			try {
				c.save(PermissionManager.permission_file);
				CivilCraft.sendBroadcast("PermPlayer", "Registered!");
			} catch (IOException e) {
				CivilCraft.sendErrorInfo(Bukkit.getConsoleSender(), "PermPlayer", "Der Spieler '"+playername+"' konnte nicht registriert werden.");
				e.printStackTrace();
			}
		}
		public void registerPlayer(String playername) {
			FileConfiguration c = PermissionManager.cfg;
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(p.getName().equals(playername)) {
					c.set("Players."+p.getUniqueId().toString(), p.getName());
					c.set("Players."+p.getName(), p.getUniqueId().toString());
					this.playername = p.getName();
					this.uuid = p.getUniqueId();
					break;
				}
			}
			ArrayList<String> groups = new ArrayList<String>();
			groups.add("player");
			c.set("Players.groups", groups);
			
			ArrayList<String> perms = new ArrayList<String>();
			perms.add("none");
			c.set("Players.permissions", perms);
			try {
				c.save(PermissionManager.permission_file);
				CivilCraft.sendBroadcast("PermPlayer", "Registered!");
			} catch (IOException e) {
				CivilCraft.sendErrorInfo(Bukkit.getConsoleSender(), "PermPlayer", "Der Spieler '"+playername+"' konnte nicht registriert werden.");
				e.printStackTrace();
			}
		}
		
		public void loadData() {
			this.permGroup = new PermGroup(PermissionManager.cfg.getString("Players.group"));
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
