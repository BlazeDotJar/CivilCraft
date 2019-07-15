package me.ichmagomaskekse.de.permissions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import me.ichmagomaskekse.de.CivilCraft;

public class PermissionManager {
	
	private static String groups_path = "plugins/CivilCraft/groups.yml";
	private static String player_data_path = "plugins/CivilCraft/players.yml";
	private static File groups_file = null;
	private static File player_data_file = null;
	private static FileConfiguration groups_cfg = null;
	private static FileConfiguration player_data_cfg = null;
	public static ArrayList<String> registered_group_names = new ArrayList<String>();
	
	/*                     String = Groupname */
	private static HashMap<String, PermGroup> loaded_groups = new HashMap<String, PermGroup>();
	private static HashMap<UUID, PermPlayer> permPlayers = new HashMap<UUID, PermPlayer>();
	
	
	
	public PermissionManager() {
		reloadData();
	}
	
	//Prozess: Gruppen werden geladen
	public static void loadGroupNames() {
		PermissionManager.registered_group_names.clear();
		for(String s : PermissionManager.groups_cfg.getStringList("Permissions.groups")) PermissionManager.registered_group_names.add(s);
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
		PermissionManager.groups_file = new File(PermissionManager.groups_path);
		PermissionManager.player_data_file = new File(PermissionManager.player_data_path);
		PermissionManager.groups_cfg = YamlConfiguration.loadConfiguration(PermissionManager.groups_file);
		PermissionManager.player_data_cfg= YamlConfiguration.loadConfiguration(PermissionManager.player_data_file);
		loadGroupNames();
		loadGroups();
		loadGroupInherits();
		loadPermPlayers();
		CivilCraft.sendInfo(Bukkit.getConsoleSender(), "Permission Manager", "Es wurden folgende Gruppen geladen: "+PermissionManager.registered_group_names.toString());
		CivilCraft.sendInfo(Bukkit.getConsoleSender(), "Permission Manager", "Es wurden folgende Spieler geladen: "+PermissionManager.permPlayers.toString());
	}
	//Ladet die PermPlayer Daten
	public static void loadPermPlayer(Player p) {
		permPlayers.put(p.getUniqueId(), new PermPlayer(p.getUniqueId()));
		for(UUID id : permPlayers.keySet()) p.sendMessage(id.toString());
	}
	
	public static boolean hasPermission(CommandSender sender, String command) {
		//TODO: MUSS GEUPDATET WERDEN; INHERITS PRÜFEN; PLAYER PRÜFEN;
		boolean bypass = false;
		bypass = sender.hasPermission(PermissionList.getPermission(command));
		if(bypass == false) CivilCraft.sendInfo(sender, "", "§cDu hast nicht das Recht dazu");
		return bypass;
	}
	
	public static PermPlayer getPermPlayer(UUID uuid) {
		if(permPlayers.containsKey(uuid)) return permPlayers.get(uuid);
		else return new PermPlayer(uuid);
	}
	public static PermGroup getPermGroup(String groupname) {
		return loaded_groups.get(groupname);
	}
	
	public static class PermPlayer {
		public UUID uuid = null;
		public Player player = null;
		public PermGroup group; //Es kann nur 1ne Gruppe zugeteilt werden, damit es keine Probleme beim Anzeigen des Prefixes gibt
		public ArrayList<String> permissions = new ArrayList<String>();
		private PermissionAttachment attachment = null;
		public PermPlayer(UUID uuid) {
			this.uuid = uuid;
			initPlayer();
			registerPlayer(uuid, false);
			loadData();
			CivilCraft.sendInfo(Bukkit.getConsoleSender(), "PermPlayer Loading", "§6"+uuid.toString()+" wurde geladen");
		}
		
		public void initPlayer() {
			try{
				this.player = Bukkit.getPlayer(uuid);
				attachment = player.addAttachment(CivilCraft.getInstance());
			}catch(Exception ex) {}
		}
		
		//Ein Spieler wird in den Player Index eingetragen
		public void registerPlayer(UUID uuid, boolean overwrite) {
			FileConfiguration player_data_cfg = YamlConfiguration.loadConfiguration(PermissionManager.player_data_file);
			boolean bypass = false;
			if(overwrite == false) {
				if(player_data_cfg.getString("Players."+uuid.toString()+".group") == null) bypass = true;
			}
			if(bypass || overwrite) {				
				ArrayList<String> list = new ArrayList<String>();
				player_data_cfg.set("Players."+uuid.toString()+".group", "player");
				player_data_cfg.set("Players."+uuid.toString()+".permissions", list);
				try {
					player_data_cfg.save(PermissionManager.player_data_file);
				} catch (IOException e) {e.printStackTrace();}
			}
		}

		public boolean loadData() {
			if(PermissionManager.player_data_cfg.getString("Players."+this.uuid.toString()+".group") == null) registerPlayer(this.uuid, true);
			
			String name = PermissionManager.player_data_cfg.getString("Players."+this.uuid.toString()+".group");
			if(name == null) CivilCraft.sendErrorInfo(Bukkit.getConsoleSender(), "", "name ======== NULL");
			group = new PermGroup(name);
			
			for(String s : PermissionManager.player_data_cfg.getStringList("Players."+this.uuid.toString()+".permissions")) {
				if(s == null) break;
				else permissions.add(s);
			}
			
			//Permissions werden gesetzt
			if(attachment != null) {				
				for(String s : permissions) attachment.setPermission(s, true);
				for(String s : group.permissions) attachment.setPermission(s, true);
				for(String s : group.anti_permissions) attachment.setPermission(s, false);
			}else initPlayer();
			return true;
		}


		public void addPermission(String permission) {
			permissions.add(permission);
			if(attachment == null) initPlayer();
			attachment.setPermission(permission, true);
			saveData(true);
		}
		public void removePermission(String permission) {
			permissions.remove(permission);
			if(attachment == null) initPlayer();
			attachment.setPermission(permission, false);
			saveData(true);
		}
		
		public void refreshPermissions(PermGroup sender) {
			if(sender.groupname.equals(group.groupname)) {
				for(String s : group.permissions) attachment.setPermission(s, true);
				for(String s : group.anti_permissions) attachment.setPermission(s, false);
			}
		}
		
		public boolean saveData(boolean overwrite) {
			PermissionManager.player_data_cfg.set("Players."+this.uuid.toString()+".permissions",permissions);
			try {
				PermissionManager.player_data_cfg.save(PermissionManager.player_data_file);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
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
			FileConfiguration c = PermissionManager.groups_cfg;
			this.prefix = c.getString("Permissions."+groupname+".prefix");
			this.suffix = c.getString("Permissions."+groupname+".suffix");
			
			this.permissions = new ArrayList<String>();
			//Anti-Permissions werden aus den Permissions rausgefiltert
			for(String s : c.getStringList("Permissions."+groupname+".permissions")) {
				if(s.startsWith("-")) this.anti_permissions.add(s);
				else this.permissions.add(s);
			}
			/*
			 * Inherits werden extra geladen
			 */
			CivilCraft.sendInfo(Bukkit.getConsoleSender(), "PermGroup", "Gruppe '"+groupname+"' wurde geladen");
		}
		public void loadInherits() {
			ArrayList<String> in = (ArrayList<String>) PermissionManager.groups_cfg.getStringList("Permissions."+groupname+".inherit");
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

		public void addPermission(String perm) {
			if(perm.startsWith("-")) anti_permissions.add(perm);
			else permissions.add(perm);
			for(UUID uuid : permPlayers.keySet()) PermissionManager.getPermPlayer(uuid).refreshPermissions(this);
			saveData(true);
		}
		public void removePermission(String perm) {
			if(perm.startsWith("-")) anti_permissions.remove(perm);
			else permissions.remove(perm);
			for(UUID uuid : permPlayers.keySet()) PermissionManager.getPermPlayer(uuid).refreshPermissions(this);
			saveData(true);
		}
		public boolean saveData(boolean overwrite) {
			@SuppressWarnings("unchecked")
			ArrayList<String> list = (ArrayList<String>) permissions.clone();
			list.addAll(anti_permissions);
			PermissionManager.groups_cfg.set("Permissions."+groupname+".permissions", list);
			try {
				PermissionManager.groups_cfg.save(PermissionManager.groups_file);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
	}
}
