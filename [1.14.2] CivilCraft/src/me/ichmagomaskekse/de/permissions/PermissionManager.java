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
	
	private static String groups_path = "plugins/CivilCraft/Permissions/groups.yml";
	private static String player_data_path = "plugins/CivilCraft/Permissions/players.yml";
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
	//Lädt eine Gruppe und gibt sie zurück
	public static PermGroup loadGroup(String name) {
		boolean success = true;
		CivilCraft.sendBroadcast("", "NAME: "+name);
		if(registered_group_names.contains(name)) {
			if(loaded_groups.containsKey(name)) {
				PermGroup pgroup = loaded_groups.get(name);
				pgroup.loadInherits();
				CivilCraft.sendBroadcast("", "Group "+name+" loaded and returned");
				return pgroup;
			}else success = false;
		}
		if(success == false) {			
			PermGroup pgroup = new PermGroup(name);
			pgroup.loadInherits();
			loaded_groups.put(name, pgroup);
			CivilCraft.sendBroadcast("", "Group "+name+" returned");
			return pgroup;
		}else {
			CivilCraft.sendBroadcast("", "Returning null");
			return null;
		}
	}
//	public static void loadGroups() {
//		PermissionManager.loaded_groups.clear();
//		//Gruppen werden geladen
//		for(String s : registered_group_names) {
//			loaded_groups.put(s, new PermGroup(s));
//		}
//	}
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
		for(PermPlayer pp : permPlayers.values()) pp.unsetPermissions();
		PermissionManager.groups_file = new File(PermissionManager.groups_path);
		PermissionManager.player_data_file = new File(PermissionManager.player_data_path);
		PermissionManager.groups_cfg = YamlConfiguration.loadConfiguration(PermissionManager.groups_file);
		PermissionManager.player_data_cfg= YamlConfiguration.loadConfiguration(PermissionManager.player_data_file);
		loadGroupNames();
//		loadGroups();
//		loadGroupInherits();
		loadPermPlayers();
//		CivilCraft.sendInfo(Bukkit.getConsoleSender(), "Permission Manager", "Es wurden folgende Gruppen geladen: "+PermissionManager.registered_group_names.toString());
//		CivilCraft.sendInfo(Bukkit.getConsoleSender(), "Permission Manager", "Es wurden folgende Spieler geladen: "+PermissionManager.permPlayers.toString());
	}
	public static void disable() {
		for(PermPlayer pp : permPlayers.values()) {
			pp.unsetPermissions();
			pp.saveData(true);
		}
		for(PermGroup pg : loaded_groups.values()) {
			pg.saveData(true);
		}
	}
	
	//Ladet die PermPlayer Daten
	public static void loadPermPlayer(Player p) {
		permPlayers.put(p.getUniqueId(), new PermPlayer(p.getUniqueId()));
		for(UUID id : permPlayers.keySet()) p.sendMessage(id.toString());
	}
	
	public static void refreshGroupData(PermGroup group) {
		for(PermPlayer pp : permPlayers.values()) pp.refreshGroupData(group);
	}
	
	public static boolean hasPermission(CommandSender sender, String command) {
		//TODO: MUSS GEUPDATET WERDEN; INHERITS PRÜFEN; PLAYER PRÜFEN;
		boolean bypass = false;
		if(bypass == false) bypass = sender.hasPermission(PermissionList.getPermission(command));
		if(bypass == false && PermissionList.knowsOpPermission(command)) bypass = sender.hasPermission(PermissionList.getOpPermission(command));
		if(bypass == false) bypass = sender.hasPermission("*");
		if(bypass == false == true) CivilCraft.sendInfo(sender, "", "§cDu hast nicht das Recht dazu");
		return bypass;
	}
	public static boolean hasPermission(CommandSender sender, String command, boolean sendError) {
		//TODO: MUSS GEUPDATET WERDEN; INHERITS PRÜFEN; PLAYER PRÜFEN;
		boolean bypass = false;
		if(bypass == false) bypass = sender.hasPermission(PermissionList.getPermission(command));
		if(bypass == false && PermissionList.knowsOpPermission(command)) bypass = sender.hasPermission(PermissionList.getOpPermission(command));
		if(bypass == false) bypass = sender.hasPermission("*");
		if(bypass == false && sendError == true) CivilCraft.sendInfo(sender, "", "§cDu hast nicht das Recht dazu");
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
		public PermGroup group = null; //Es kann nur 1ne Gruppe zugeteilt werden, damit es keine Probleme beim Anzeigen des Prefixes gibt
//		public ArrayList<String> permissions = new ArrayList<String>();
		public HashMap<String, PermissionOrigin> permissions = new HashMap<String, PermissionOrigin>();
		private PermissionAttachment attachment = null;
		public PermissionAttachment getAttachment() {
			return attachment;
		}
		public PermPlayer(UUID uuid) {
			this.uuid = uuid;
			initPlayer();
			registerPlayer(uuid, false);
			loadData();
			CivilCraft.sendInfo(Bukkit.getConsoleSender(), "PermPlayer Loading", "§6"+uuid.toString()+" wurde geladen");
			CivilCraft.sendInfo(Bukkit.getConsoleSender(), "PermPlayer Loading", "§6Gruppe: "+group.groupname);
			CivilCraft.sendInfo(Bukkit.getConsoleSender(), "PermPlayer Loading", "§6Permissions(PLAYER): "+permissions.toString());			
			CivilCraft.sendInfo(Bukkit.getConsoleSender(), "PermPlayer Loading", "§6Permissions(GROUP): "+group.permissions.toString());			
			CivilCraft.sendInfo(Bukkit.getConsoleSender(), "PermPlayer Loading", "§6Permissions(GROUP-ANTI): "+group.anti_permissions.toString());			
		}
		
		public void unsetPermissions() {
			for(String s : permissions.keySet()) attachment.setPermission(s, false);
			for(String s : group.permissions) attachment.setPermission(s, false);
			for(String s : group.anti_permissions) attachment.setPermission(s, false);
		}
		
		public void initPlayer() {
			try{
				this.player = Bukkit.getPlayer(uuid);
				this.attachment = player.addAttachment(CivilCraft.getInstance());
			}catch(Exception ex) {ex.printStackTrace();}
		}
		
		//Ein Spieler wird in den Player Index eingetragen
		public void registerPlayer(UUID uuid, boolean overwrite) {
			PermissionManager.player_data_file = new File(PermissionManager.player_data_path);
			PermissionManager.player_data_cfg = YamlConfiguration.loadConfiguration(PermissionManager.player_data_file);
			
			boolean bypass = false;
			if(overwrite == false) {
				if(uuid == null) Bukkit.broadcastMessage("uuid == null");
				if(PermissionManager.player_data_cfg.getString("Players."+uuid.toString()+".group") == null) bypass = true;
			}
			if(bypass || overwrite) {				
				ArrayList<String> list = new ArrayList<String>();
				list.add("beispiel.permission");
				PermissionManager.player_data_cfg.set("Players."+uuid.toString()+".group", "player");
				PermissionManager.player_data_cfg.set("Players."+uuid.toString()+".permissions", list);
				try {
					PermissionManager.player_data_cfg.save(new File(PermissionManager.player_data_path));
				} catch (IOException e) {e.printStackTrace();}
			}
		}

		public boolean loadData() {
			if(PermissionManager.player_data_cfg.getString("Players."+this.uuid.toString()+".group") == null) registerPlayer(this.uuid, true);
			
			String group_name = PermissionManager.player_data_cfg.getString("Players."+this.uuid.toString()+".group");
			if(group_name == null) CivilCraft.sendErrorInfo(Bukkit.getConsoleSender(), "", "name ======== NULL");
			group = loadGroup(group_name);
			group.loadInherits();
			
			for(String s : PermissionManager.player_data_cfg.getStringList("Players."+this.uuid.toString()+".permissions")) {
				if(s == null) break;
				else permissions.put(s, PermissionOrigin.OWN);
			}
			
			if(attachment == null) CivilCraft.sendErrorInfo(Bukkit.getConsoleSender(), "PermPlayer", "attachment == null");
			if(attachment == null) initPlayer();
			if(player == null) CivilCraft.sendErrorInfo(Bukkit.getConsoleSender(), "PermPlayer", "Player == null");
			if(player == null) initPlayer();
			//Permissions werden gesetzt
			if(attachment != null) {
				refreshGroupData(group);
				for(String s : permissions.keySet()) {
					attachment.setPermission(s, true);
				}
				for(String s : group.permissions) attachment.setPermission(s, true);
				for(String s : group.anti_permissions) attachment.setPermission(s, false);
			}
			return true;
		}


		public void addPermission(PermissionOrigin origin, String permission) {
			permissions.put(permission, origin);
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
		
		public void refreshGroupData(PermGroup sender) {
			if(sender.groupname.equals(group.groupname)) {
				for(String s : group.permissions) {
					attachment.setPermission(s, true);
					if(permissions.containsValue(s) == false) permissions.put(s, PermissionOrigin.GROUP);
				}
				for(String s : group.anti_permissions) {
					attachment.setPermission(s, false);
				}
				group.prefix = sender.prefix;
				group.suffix = sender.suffix;
			}
		}
		
		public boolean saveData(boolean overwrite) {
			ArrayList<String> perms = new ArrayList<String>();
			for(String s : permissions.keySet()) perms.add(s);
			PermissionManager.player_data_cfg.set("Players."+this.uuid.toString()+".permissions",perms);
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
		
		public String groupname = "{FEHLER}";
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
		public PermGroup(String groupname, boolean create)  {
			if(create && registered_group_names.contains(groupname) == false) {
				registered_group_names.add(groupname);
				CivilCraft.sendInfo(Bukkit.getConsoleSender(), "PermGroup", "Gruppe '§6"+groupname+"§f' wird erstellt..");
				this.groupname = groupname;
				createNewGroup();
				loadData();
				PermissionManager.groups_cfg = YamlConfiguration.loadConfiguration(PermissionManager.groups_file);
				PermissionManager.groups_cfg.set("Permissions.groups", registered_group_names);
				try {
					PermissionManager.groups_cfg.save(PermissionManager.groups_file);
					CivilCraft.sendInfo(Bukkit.getConsoleSender(), "PermGroup", "§aGruppe '§6"+groupname+"§a' wurde erstellt!");
				} catch (IOException e) {
					e.printStackTrace();
					CivilCraft.sendInfo(Bukkit.getConsoleSender(), "PermGroup", "§cGruppe '§6"+groupname+"§c' konnte nicht erstellt werden!");
				}
			}
		}
		public boolean createNewGroup() {
			PermissionManager.groups_cfg = YamlConfiguration.loadConfiguration(PermissionManager.groups_file);
			FileConfiguration c = PermissionManager.groups_cfg;
			c.set("Permissions."+groupname+".prefix", "&e"+groupname+"");
			c.set("Permissions."+groupname+".suffix", "&f");
			c.set("Permissions."+groupname+".permissions", permissions);
			c.set("Permissions."+groupname+".inherit", inherit);
			try {
				c.save(PermissionManager.groups_file);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		public void loadData() {
			CivilCraft.sendInfo(Bukkit.getConsoleSender(), "PermGroup", "§2Gruppe '"+groupname+"' wird geladen...");
			FileConfiguration c = PermissionManager.groups_cfg;
			this.prefix = c.getString("Permissions."+groupname+".prefix").replace("&", "§");
			this.suffix = c.getString("Permissions."+groupname+".suffix").replace("&", "§");
			
			this.permissions = new ArrayList<String>();
			//Anti-Permissions werden aus den Permissions rausgefiltert
			for(String s : c.getStringList("Permissions."+groupname+".permissions")) {
				if(s.startsWith("-")) this.anti_permissions.add(s);
				else this.permissions.add(s);
			}
			/*
			 * Inherits werden extra geladen
			 */
			CivilCraft.sendInfo(Bukkit.getConsoleSender(), "PermGroup", "§aGruppe '"+groupname+"' wurde geladen");
		}
		public void loadInherits() {
			ArrayList<String> in = (ArrayList<String>) PermissionManager.groups_cfg.getStringList("Permissions."+groupname+".inherit");
			for(String s : in) {
				this.inherit.add(PermissionManager.loaded_groups.get(s));
			}
			for(PermGroup pg : this.inherit) {
				if(pg == null) {
					CivilCraft.sendErrorInfo(Bukkit.getConsoleSender(), "PermGroup", "Inherit = NULL; List.Size: "+this.inherit.size());
					break;
				}
				for(String perm : pg.permissions) {
					if(permissions.contains(perm) == false) permissions.add(perm);
				}
				//Anti-Permissions werden nicht übernommen, weil das kein Sinn macht, wenn ein Admin kein /Cadmin machen darf
				//nur weil ein Player es nicht kann
			}
			CivilCraft.sendErrorInfo(Bukkit.getConsoleSender(), "PermGroup", "§f"+groupname+"§5's current permissions(+Anti):");
			for(String s : permissions) {
				CivilCraft.sendErrorInfo(Bukkit.getConsoleSender(), "PermGroup", "§f- "+s);
			}
			for(String s : anti_permissions) {
				CivilCraft.sendErrorInfo(Bukkit.getConsoleSender(), "PermGroup", "§c- §d"+s);
			}
		}
		
		public ArrayList<String> getPermissions() {
			return permissions;
		}
		public ArrayList<String> getAntiPermissions() {
			return anti_permissions;
		}

		public void addPermission(String perm) {
			if(perm.startsWith("-")) anti_permissions.add(perm);
			else permissions.add(perm);
			for(UUID uuid : permPlayers.keySet()) PermissionManager.getPermPlayer(uuid).refreshGroupData(this);
			saveData(true);
		}
		public void removePermission(String perm) {
			if(perm.startsWith("-")) anti_permissions.remove(perm);
			else permissions.remove(perm);
			for(UUID uuid : permPlayers.keySet()) PermissionManager.getPermPlayer(uuid).refreshGroupData(this);
			saveData(true);
		}
		public boolean saveData(boolean overwrite) {
			PermissionManager.groups_cfg = YamlConfiguration.loadConfiguration(PermissionManager.groups_file);
			PermissionManager.groups_cfg.set("Permissions."+groupname+".prefix", prefix.replace("§", "&"));
			PermissionManager.groups_cfg.set("Permissions."+groupname+".suffix", suffix.replace("§", "&"));
			@SuppressWarnings("unchecked")
			ArrayList<String> list = (ArrayList<String>) permissions.clone();
			list.addAll(anti_permissions);
			PermissionManager.groups_cfg.set("Permissions."+groupname+".permissions", list);
			PermissionManager.groups_cfg.set("Permissions."+groupname+".inherit", inherit);
			try {
				PermissionManager.groups_cfg.save(PermissionManager.groups_file);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}
	
	public enum PermissionOrigin {
		
		OWN, //OWN = Wenn die Permission aus der Player.yml Datei ausgelesen wurde
		GROUP; //GROUP = Wenn er die Permission durch eine Gruppe bekommen hat
		
	}
}
