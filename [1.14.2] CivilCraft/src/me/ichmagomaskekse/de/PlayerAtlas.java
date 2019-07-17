package me.ichmagomaskekse.de;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlayerAtlas {
	//Diese Klasse ist selbsterklärend
	private static File atlas_file;
	private static FileConfiguration atlas_cfg;
	
	public PlayerAtlas() {
		PlayerAtlas.atlas_file = new File("plugins/CivilCraft/player_atlas.yml");
		PlayerAtlas.atlas_cfg = YamlConfiguration.loadConfiguration(PlayerAtlas.atlas_file);
	}
	
	public static UUID getUUIDbyName(String name) {
		UUID uuid = UUID.fromString(PlayerAtlas.atlas_cfg.getString(name));
		return uuid;
	}
	public static String getNameByUUID(UUID uuid) {
		String name = PlayerAtlas.atlas_cfg.getString(uuid.toString());
		return name;
	}
	public static void registerPlayer(Player p) {
		PlayerAtlas.atlas_cfg = YamlConfiguration.loadConfiguration(PlayerAtlas.atlas_file);
		PlayerAtlas.atlas_cfg.set(p.getUniqueId().toString(), p.getName());
		PlayerAtlas.atlas_cfg.set(p.getName(), p.getUniqueId().toString());
		try {
			PlayerAtlas.atlas_cfg.save(PlayerAtlas.atlas_file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean registerOnlinePlayers() {
		CivilCraft.sendErrorInfo(Bukkit.getConsoleSender(), "ATLAS", "REGISTER ON-Players");
		for(Player p : Bukkit.getOnlinePlayers()) {
			CivilCraft.sendErrorInfo(Bukkit.getConsoleSender(), "ATLAS", "On.Players while Reload.Name: "+p.getName());
			CivilCraft.sendErrorInfo(Bukkit.getConsoleSender(), "ATLAS", "On.Players while Reload.Name: "+p.getUniqueId().toString());
			PlayerAtlas.registerPlayer(p);
		}
		return true;
	}
}
