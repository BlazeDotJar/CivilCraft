package me.ichmagomaskekse.de.filesystem;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.ichmagomaskekse.de.CivilCraft;

public class FileManager {
	
	//Lobby
	public static int lobby_max_slots = 2;
	public static int lobby_amount = 0;
	
	//Misc
	public static String MOTD = "MOTD NOT LOADED";
	public static String join_message = "§7{USER} §ahat das Spiel betreten";
	public static String leave_message = "§7{USER} §chat das Spiel verlassen";
	
	//SYSTEM
	public static String server_prefix = "CivilCraft";
	public static final String config_path = "plugins/CivilCraft/civilcraft.yml";
	private static FileConfiguration cfg = null;
	private static File config_file = null;
	
	public FileManager(boolean createCivilConfig) {
		//Läd die Daten aus der Resource Config in die CivilCraft Config,
		//wenn die civilcraft.yml nicht existiert
		config_file = new File(config_path);
		if(createCivilConfig) {
//			File config = new File("plugins/CivilCraft/config.yml");
//			if(config.renameTo(config_file)) {
//				CivilCraft.sendBroadcast("", "Rename war erfolgreich");
//			}
			File old = new File("plugins/CivilCraft/config.yml");
			File neu = new File(config_path);
			
			if(old.renameTo(neu)) {
				CivilCraft.sendBroadcast("", "Erfolgreich umbenannt");
			}else CivilCraft.sendBroadcast("", "Umbenennen fehlgeschlagen");
			
			config_file = new File(config_path);
			cfg = YamlConfiguration.loadConfiguration(config_file);
			loadData();
		}
	}
	
	//Läd die Daten aus der CivilCraft Config in die Variablen
	private static boolean loadData() {
		if(config_file == null) {
			CivilCraft.sendBroadcast("", "Config File = null");
			return false;
		}
		if(cfg == null) {
			CivilCraft.sendBroadcast("", "CFG = null");
			return false;			
		}
		lobby_max_slots = cfg.getInt("Lobby.max players");
		lobby_amount = cfg.getInt("Lobby.max lobby");
		MOTD = cfg.getString("Misc.MOTD").replace("&", "§").replace("%n", "\n");
		join_message = cfg.getString("Lobby.join message").replace("&", "§");
		leave_message = cfg.getString("Lobby.leave message").replace("&", "§");
		server_prefix = cfg.getString("Misc.server prefix").replace("&", "§");
		
		return true;
	}
	
	public static boolean reloadData() {
		return loadData();
	}
	
}
