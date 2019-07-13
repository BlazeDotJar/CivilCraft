package me.ichmagomaskekse.de.filesystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.ichmagomaskekse.de.CivilCraft;

public class FileManager2 {
	
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
	
	public FileManager2(boolean createCivilConfig) {
		//Läd die Daten aus der Resource Config in die CivilCraft Config,
		//wenn die civilcraft.yml nicht existiert
		if(createCivilConfig) {
			try {
				Scanner scanner = new Scanner(new File("plugins/CivilCraft/config.yml"));
				String line = "";
				String content = "";
				try (PrintWriter out = new PrintWriter(config_path)) {
					while(scanner.hasNext()) {
						line = scanner.nextLine();
						if(content.equals("")) content = line;
						else content += "\n"+line;
					}
					out.print(content);
					out.close();
					
				}
				scanner.close();
				System.out.println("Config File 'CivilCraft.yml' wurde erstellt");
//					loadData();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			config_file = new File(config_path);
			cfg = YamlConfiguration.loadConfiguration(config_file);
		}
		try {
			File config = new File("plugins/CivilCraft/config.yml");
			config.delete();
		}catch(Exception ex) {}
	}
	
	//Läd die Daten aus der CivilCraft Config in die Variablen
	private static boolean loadData() {
		if(config_file == null) {
			CivilCraft.sendBroadcast("", "Config File = null");
			return false;
		}
		if(cfg == null) {
			cfg = YamlConfiguration.loadConfiguration(config_file);
		}
		if(cfg == null) {
			CivilCraft.sendBroadcast("", "CFG = null");
			return false;			
		}
//		lobby_max_slots = cfg.getInt("Lobby.max players");
		lobby_amount = cfg.getInt("Lobby.max lobby");
//		MOTD = cfg.getString("Misc.MOTD");
		join_message = cfg.getString("Lobby.join message").replace("&", "§");
		leave_message = cfg.getString("Lobby.leave message").replace("&", "§");
		server_prefix = cfg.getString("Misc.server prefix").replace("&", "§");
		
		return true;
	}
	
	public static boolean reloadData() {
		return loadData();
	}
	
}
