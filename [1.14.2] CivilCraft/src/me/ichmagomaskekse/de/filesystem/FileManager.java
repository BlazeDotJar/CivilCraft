package me.ichmagomaskekse.de.filesystem;

import java.io.File;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.ichmagomaskekse.de.lobby.ServerType;

public class FileManager {
	
	//Lobby
	public static int lobby_max_slots = 0;
	public static int lobby_amount = 0;
	
	//Misc
	public static String MOTD = "MOTD NOT LOADED";
	public static String join_message = "§7{USER} §ahat das Spiel betreten";
	public static String leave_message = "§7{USER} §chat das Spiel verlassen";
	public static int server_slots = 1;
	
	//SYSTEM
	public static String server_prefix = "CC";
	public static final String config_path = "plugins/CivilCraft/civilcraft.yml";
	private static FileConfiguration cfg = null;
	private static File config_file = null;
	
	public FileManager() {
		config_file = new File(config_path);
		cfg = YamlConfiguration.loadConfiguration(config_file);
		loadData();
	}
	
	//Läd die Daten aus der CivilCraft Config in die Variablen
	private static boolean loadData() {
		config_file = new File(config_path);
		cfg = YamlConfiguration.loadConfiguration(config_file);
		//LOBBY
		FileManager.lobby_max_slots = cfg.getInt("Lobby.max players");
		FileManager.lobby_amount = cfg.getInt("Lobby.max lobby");
		
		//MISC
		FileManager.MOTD = cfg.getString("Misc.MOTD").replace("&", "§").replace("%n", "\n");
		FileManager.join_message = cfg.getString("Lobby.join message").replace("&", "§");
		FileManager.leave_message = cfg.getString("Lobby.leave message").replace("&", "§");
		FileManager.server_slots = cfg.getInt("Misc.server slots");
		
		//SYSTEM
		FileManager.server_prefix = cfg.getString("Misc.server prefix").replace("&", "§");
		return true;
	}
	
	public static boolean reloadData() {
		return loadData();
	}
	
public static class PlayerProfile {
		
		public ServerType current_server = ServerType.UNKNOWN;
		public Player player = null;
		public UUID uuid = null;
		
		public PlayerProfile(Player player) {
			this.player = player;
			this.uuid = player.getUniqueId();
		}
		
	}
	
}
