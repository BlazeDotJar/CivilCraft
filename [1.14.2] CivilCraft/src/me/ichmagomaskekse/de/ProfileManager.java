package me.ichmagomaskekse.de;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class ProfileManager {
	
	private static HashMap<UUID, PlayerProfile> profiles = new HashMap<UUID, PlayerProfile>();
	private static String profile_path = "plugins/CivilCraft/profiles.yml";
	
	public ProfileManager() {}
	
	public static void registerProfiles() {
		disable();
		profiles.clear();
		for(Player p : Bukkit.getOnlinePlayers())
			if(profiles.containsKey(p.getUniqueId()) == false) profiles.put(p.getUniqueId(), new PlayerProfile(p));
	}
	public static void registerProfile(Player p) {
		if(profiles.containsKey(p.getUniqueId()) == false) profiles.put(p.getUniqueId(), new PlayerProfile(p));
	}
	public static PlayerProfile getProfile(Player p) {
		if(profiles.containsKey(p.getUniqueId()) == false) registerProfiles(); 
		return getProfile(p.getUniqueId());
	}
	public static PlayerProfile getProfile(UUID uuid) {
		if(profiles.containsKey(uuid) == false) registerProfiles();
		return profiles.get(uuid);
	}
	public static void unregisterProfile(Player p) {
		unregisterProfile(p.getUniqueId());
	}
	public static void unregisterProfile(UUID uuid) {
		profiles.remove(uuid);
	}
	public static void disable() {		
		for(PlayerProfile p : profiles.values()) p.saveData();
	}
	public static void reloadData() {
		for(PlayerProfile p : profiles.values()) p.saveData();
		registerProfiles();
	}
	
	public static class PlayerProfile {
		
		public boolean chat_mute = false;
		public boolean allow_fly = false;
		public boolean godmode = false;
		public String player_ip = "";
		public Location last_location = null;
		public GameMode current_gamemode = GameMode.ADVENTURE;
		
		public Player player = null;
		public UUID uuid = null;
		public String playername = "";
		
		public PlayerProfile(Player player) {
			this.player = player;
			this.uuid = player.getUniqueId();
			this.playername = player.getName();
			this.player_ip = player.getAddress().toString();
			try{loadData();}catch(Exception ex){saveData();}
			if(last_location == null) last_location = player.getLocation();
			current_gamemode = player.getGameMode();
			loadData();
		}
		
		public void saveData() {
			if(isLoaded == false) return;
			File file = new File(profile_path);
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			cfg.set(uuid.toString()+".chat mute", chat_mute);
			cfg.set(uuid.toString()+".allow fly", allow_fly);
			cfg.set(uuid.toString()+".godmode", godmode);
			cfg.set(uuid.toString()+".last ip", player_ip);
			cfg.set(uuid.toString()+".last location.World", last_location.getWorld().getName());
			cfg.set(uuid.toString()+".last location.X", last_location.getX());
			cfg.set(uuid.toString()+".last location.Y", last_location.getY());
			cfg.set(uuid.toString()+".last location.Z", last_location.getZ());
			cfg.set(uuid.toString()+".last location.Yaw", last_location.getYaw());
			cfg.set(uuid.toString()+".last location.Pitch", last_location.getPitch());
			cfg.set(uuid.toString()+".current gamemode", current_gamemode.toString());
			try {
				cfg.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		private boolean isLoaded = false;
		public void loadData() {
			File file = new File(profile_path);
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			if(file.exists() == false) saveData();
			
			String world = cfg.getString(uuid.toString()+".last location.World");
			if(world == null) return;
			double x = cfg.getDouble(uuid.toString()+".last location.X");
			double y = cfg.getDouble(uuid.toString()+".last location.Y");
			double z = cfg.getDouble(uuid.toString()+".last location.Z");
			float yaw =   (float)cfg.getInt(uuid.toString()+".last location.Yaw");
			float pitch = (float) cfg.getInt(uuid.toString()+".last location.Pitch");
			
			this.chat_mute = cfg.getBoolean(uuid.toString()+".chat mute");
			this.allow_fly = cfg.getBoolean(uuid.toString()+".allow fly");
			this.godmode = cfg.getBoolean(uuid.toString()+".godmode");
			this.last_location = new Location(Bukkit.getWorld(world),x,y,z,yaw,pitch);
			this.last_location = player.getLocation();
			this.current_gamemode = GameMode.valueOf(cfg.getString(uuid.toString()+".current gamemode"));
			
			isLoaded = true;
		}
		
		public boolean mute() {
			this.chat_mute = !this.chat_mute;
			saveData();
			return chat_mute;
		}
		
		public boolean toggleFlight() {
			this.allow_fly = !this.allow_fly;
			this.player.setAllowFlight(this.allow_fly);
			saveData();
			return allow_fly;
		}
		
		public boolean toggleGodmode() {
			this.godmode = !this.godmode;
			saveData();
			return godmode;
		}
		
	}
	
}
