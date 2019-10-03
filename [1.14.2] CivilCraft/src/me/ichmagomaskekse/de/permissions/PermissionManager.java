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
		CivilCraft.sendErrorInfo(Bukkit.getConsoleSender(), "Permission Manager", "///////////////////////////////////////////////////////////////",
				"CivilCraft coded by IchMagOmasKekse", "///////////////////////////////////////////////////////////////");
		reloadData();
		CivilCraft.sendErrorInfo(Bukkit.getConsoleSender(), "Permission Manager", "Registered Groups: "+loaded_groups.size(), "Registered Players: "+permPlayers.size());
	}
	
	//Prozess: Gruppen werden geladen
	public static void loadGroupNames() {
		PermissionManager.registered_group_names.clear();
		for(String s : PermissionManager.groups_cfg.getStringList("Permissions.groups")) PermissionManager.registered_group_names.add(s);
	}
	//Lädt eine Gruppe und gibt sie zurück
	public static PermGroup loadGroup(String groupname) {
		loadGroupNames();
		boolean success = true;
		if(registered_group_names.contains(groupname)) {
			if(loaded_groups.containsKey(groupname)) {
				PermGroup pgroup = loaded_groups.get(groupname);
				pgroup.loadData();
				return pgroup;
			}else success = false;
		}else success = false;
		
		if(success == false) {			
			PermGroup pgroup = new PermGroup(groupname);
			loaded_groups.put(groupname, pgroup);
			return pgroup;
		}else {
			return null;
		}
	}
	public static void loadGroupInherits() {
		for(PermGroup pg : PermissionManager.loaded_groups.values()) pg.loadInherits();
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
		permPlayers.clear();
		PermissionManager.groups_file = new File(PermissionManager.groups_path);
		PermissionManager.player_data_file = new File(PermissionManager.player_data_path);
		PermissionManager.groups_cfg = YamlConfiguration.loadConfiguration(PermissionManager.groups_file);
		PermissionManager.player_data_cfg= YamlConfiguration.loadConfiguration(PermissionManager.player_data_file);
		loadGroupNames();
//		loadGroups(); UNNÖTIG
//		loadGroupInherits(); UNNÖTIG
		loadPermPlayers();
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
		if(loaded_groups.containsKey(groupname) == false) loadGroup(groupname);
		return loaded_groups.get(groupname);
	}
	
	public static class PermPlayer {
		public UUID uuid = null;
		public Player player = null;
		public PermGroup group = null; //Es kann nur 1ne Gruppe zugeteilt werden, damit es keine Probleme beim Anzeigen des Prefixes gibt
		public int amount_permissions = 0;//Zum Überprüfen, ob ein Speichervorgang nötig ist
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
			CivilCraft.sendInfo(Bukkit.getConsoleSender(), "PermPlayer Loading", "§e"+uuid.toString()+" wurde geladen");
			CivilCraft.sendInfo(Bukkit.getConsoleSender(), "PermPlayer Loading", "§eGruppe: §8"+group.groupname);
			CivilCraft.sendInfo(Bukkit.getConsoleSender(), "PermPlayer Loading", "§ePermissions(PLAYER): §8"+permissions.toString());			
			CivilCraft.sendInfo(Bukkit.getConsoleSender(), "PermPlayer Loading", "§ePermissions(GROUP): §8"+group.permissions.toString());			
			CivilCraft.sendInfo(Bukkit.getConsoleSender(), "PermPlayer Loading", "§ePermissions(GROUP-ANTI): §8"+group.anti_permissions.toString());			
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
			PermissionManager.player_data_cfg = YamlConfiguration.loadConfiguration(PermissionManager.player_data_file);
			if(PermissionManager.player_data_cfg.getString("Players."+this.uuid.toString()+".group") == null) registerPlayer(this.uuid, true);
			
			String group_name = PermissionManager.player_data_cfg.getString("Players."+this.uuid.toString()+".group");
			group = loadGroup(group_name);
			
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
		public void unsetPermissions() {
			for(String s : permissions.keySet()) attachment.setPermission(s, false);
			for(String s : group.permissions) attachment.setPermission(s, false);
			for(String s : group.anti_permissions) attachment.setPermission(s.substring(1, s.length()), false);
		}
		public boolean setPermissions() {
			if(attachment == null) return false;
			for(String s : permissions.keySet()) attachment.setPermission(s, true);
			for(String s : group.permissions) attachment.setPermission(s, true);
			for(String s : group.anti_permissions) attachment.setPermission(s.substring(1, s.length()), false);
			return true;
		}
		public void setGroup(String groupname) {
			unsetPermissions();
			group = loadGroup(groupname);
			PermissionManager.player_data_file = new File(PermissionManager.player_data_path);
			PermissionManager.player_data_cfg = YamlConfiguration.loadConfiguration(PermissionManager.player_data_file);
			PermissionManager.player_data_cfg.set("Players."+uuid.toString()+".group", group.groupname);
			try {
				PermissionManager.player_data_cfg.save(PermissionManager.player_data_file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			refreshGroupData(group);
		}
		
		public void refreshGroupData(PermGroup sender) {
			if(sender == group || sender.groupname.equals(group.groupname)) {
				group = sender;
				unsetPermissions();
				for(String s : group.permissions) permissions.put(s, PermissionOrigin.GROUP);
				for(String s : group.anti_permissions) permissions.put(s, PermissionOrigin.GROUP);
				if(group.inherit.isEmpty() == false) for(PermGroup pg : group.inherit) {
					for(String s : pg.permissions) if(pg != null) permissions.put(s, PermissionOrigin.GROUP);
				}
				group.prefix = sender.prefix;
				group.suffix = sender.suffix;
				setPermissions();
			}
		}
		
		public boolean saveData(boolean overwrite) {
			if(overwrite || (amount_permissions != permissions.size())) {				
				PermissionManager.player_data_file = new File(PermissionManager.player_data_path);
				ArrayList<String> perms = new ArrayList<String>();
				for(String s : permissions.keySet()) if(permissions.get(s) == PermissionOrigin.OWN) perms.add(s);				
				PermissionManager.player_data_cfg.set("Players."+this.uuid.toString()+".permissions",perms);
				try {
					PermissionManager.player_data_cfg.save(PermissionManager.player_data_file);
					amount_permissions = permissions.size();
					return true;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
			return true;
		}

	}
	
	public static class PermGroup {
		
		public String groupname = "{FEHLER}";
		public String prefix = "";
		public String suffix = "";
		public int amount_permissions = 0;
		public int amount_anti_permissions = 0;
		public int amount_inherit = 0;
		public ArrayList<String> permissions = new ArrayList<String>();
		public ArrayList<String> anti_permissions = new ArrayList<String>();
		public ArrayList<PermGroup> inherit = new ArrayList<PermGroup>();
		
		public PermGroup(String groupname)  {
			if(PermissionManager.registered_group_names.contains(groupname)) {
				this.groupname = groupname;
				loadData();
				loadInherits();
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
			PermissionManager.groups_cfg = YamlConfiguration.loadConfiguration(PermissionManager.groups_file);
			FileConfiguration c = PermissionManager.groups_cfg;
			this.prefix = c.getString("Permissions."+groupname+".prefix").replace("&", "§");
			this.suffix = c.getString("Permissions."+groupname+".suffix").replace("&", "§");
			
			this.permissions = new ArrayList<String>();
			this.anti_permissions = new ArrayList<String>();
			this.inherit = new ArrayList<PermGroup>();
			//Anti-Permissions werden aus den Permissions rausgefiltert
			for(String s : c.getStringList("Permissions."+groupname+".permissions")) {
				if(s.startsWith("-")) this.anti_permissions.add(s);
				else this.permissions.add(s);
			}
			/*
			 * Inherits werden extra geladen
			 */
			amount_permissions = permissions.size();
			amount_anti_permissions = anti_permissions.size();
			amount_inherit = inherit.size();
			CivilCraft.sendInfo(Bukkit.getConsoleSender(), "PermGroup", "§aGruppe '"+groupname+"' wurde geladen");
			CivilCraft.sendInfo(Bukkit.getConsoleSender(), "PermGroup", "§aPermissions size: "+amount_permissions);
		}
		public void loadInherits() {
			PermissionManager.groups_cfg = YamlConfiguration.loadConfiguration(PermissionManager.groups_file);
//			ArrayList<String> in = (ArrayList<String>) PermissionManager.groups_cfg.getStringList("Permissions."+groupname+".inherit");
//			inherit.clear();
//			for(String s : in) this.inherit.add(loadGroup(s));
			for(PermGroup pg : this.inherit) {
				if(pg == null) {
					CivilCraft.sendErrorInfo(Bukkit.getConsoleSender(), "PermGroup", "Inherit = NULL; List.Size: "+this.inherit.size());
					break;
				}
				pg.loadData();
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
			saveData(true);
			for(UUID uuid : permPlayers.keySet()) PermissionManager.getPermPlayer(uuid).refreshGroupData(this);
		}
		public boolean saveData(boolean overwrite) {
			if((amount_permissions != permissions.size() || amount_anti_permissions != anti_permissions.size() || amount_inherit != inherit.size())) {
				PermissionManager.groups_cfg = YamlConfiguration.loadConfiguration(PermissionManager.groups_file);
				PermissionManager.groups_cfg.set("Permissions."+groupname+".prefix", PermissionManager.groups_cfg.getString("Permissions."+groupname+".prefix"));
				PermissionManager.groups_cfg.set("Permissions."+groupname+".suffix", PermissionManager.groups_cfg.getString("Permissions."+groupname+".suffix"));
				
				ArrayList<String> list = new ArrayList<String>();
				for(PermGroup pg : inherit) list.add(pg.groupname);
				PermissionManager.groups_cfg.set("Permissions."+groupname+".inherit", list.clone());
				list.clear();
				list = permissions;
				list.addAll(anti_permissions);
				PermissionManager.groups_cfg.set("Permissions."+groupname+".permissions", list.clone());
				try {
					PermissionManager.groups_cfg.save(PermissionManager.groups_file);
					amount_permissions = permissions.size();
					amount_anti_permissions = anti_permissions.size();
					amount_inherit = inherit.size();
					return true;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}				
			}
			return true;
		}
	}
	
	public enum PermissionOrigin {
		
		OWN, //OWN = Wenn die Permission aus der Player.yml Datei ausgelesen wurde
		GROUP; //GROUP = Wenn er die Permission durch eine Gruppe bekommen hat
		
	}
}
