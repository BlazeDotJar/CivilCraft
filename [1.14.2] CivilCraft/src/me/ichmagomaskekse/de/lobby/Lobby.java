package me.ichmagomaskekse.de.lobby;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.filesystem.FileManager;
import me.ichmagomaskekse.de.filesystem.FileManager.PlayerProfile;

public class Lobby {
	
	public int lobby_id = -1; //ID um die Lobby zu identifizieren
	
	@SuppressWarnings("unused")
	private static Location spawn; //Spawn Punkt der Lobby
	private static HashMap<UUID, PlayerProfile> players = new HashMap<UUID, PlayerProfile>();
//	private Area area = null;
	
	public Lobby(int id) {
		this.lobby_id = id;
		spawn = new Location(Bukkit.getWorld("lobby"), 0, 0, 0, 0f, 90f);
	}
	
	//Setzt die LobbyLocation
	public static void setLobbySpawn(Location spawn_location) {
		spawn = spawn_location;
	}
	
	//Gibt Erlaubnis zum Joinen und registriert dies
	public boolean requestJoin(Player p) {
		if(players.size() != FileManager.lobby_max_slots){
			if(players.containsKey(p.getUniqueId())) {
				PlayerProfile pProfile = players.get(p.getUniqueId());
				if(pProfile.current_server == ServerType.LOBBY){
					CivilCraft.sendErrorInfo(p, "Server", "Du bist bereits in der Lobby");
					return false;
				} else {
					players.remove(p.getUniqueId());
					pProfile.current_server = ServerType.LOBBY;
					players.put(p.getUniqueId(), pProfile);
					return true;
				}
			}else {
				PlayerProfile pProfile = new PlayerProfile(p);
				pProfile.current_server = ServerType.LOBBY;
				players.put(p.getUniqueId(), pProfile);
				return true;
			}
		}else return false;
	}
	//Gibt Erlaubnis zum Serverwechsel und registriert diesen
	public boolean requestServerChange(Player p, ServerType type) {
		if(players.containsKey(p.getUniqueId())) {
			if(players.get(p.getUniqueId()).current_server == type) return false;
			else {
				players.get(p.getUniqueId()).current_server = type;
				return true;
			}
		}else {
			PlayerProfile pProfile = new PlayerProfile(p);
			pProfile.current_server = type;
			players.put(p.getUniqueId(), pProfile);
			return true;
		}
	}
	
	public void unloadPlayer(Player p) {
		if(players.containsKey(p.getUniqueId())) {
			players.remove(p.getUniqueId());
		}
	}
	
	//Gibt den Aktuellen Server eines Spieler zurück
	public ServerType locatePlayer(Player p) {
		return locatePlayer(p.getUniqueId());
	}
	public ServerType locatePlayer(UUID uuid) {
		if(players.containsKey(uuid)) return players.get(uuid).current_server;
		else return null;

	}
	
	public static void sendLobbyBroadcast(boolean inLobbyOnly, String prefix, String... message) {
		if(prefix.equals("null") || prefix.equals("")) prefix = FileManager.server_prefix;
		for(String msg : message) {
			for(UUID uuid : players.keySet()) {
				((PlayerProfile)players.get(uuid)).player.sendMessage("§7["+prefix+"§7] §c"+msg);
			}
		}
	}
	
	
	
}
