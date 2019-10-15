package me.ichmagomaskekse.de.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.PlayerAtlas;
import me.ichmagomaskekse.de.ProfileManager;
import me.ichmagomaskekse.de.filesystem.FileManager;
import me.ichmagomaskekse.de.permissions.PermissionManager;

public class ServerJoinAndLeaveListener implements Listener {
	
	public ServerJoinAndLeaveListener() {
		CivilCraft.registerEvents(this);
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		/* Spieler Profile wird registriert */
		PlayerAtlas.registerPlayer(e.getPlayer());
		/* Die Lobby wird gefragt, ob der Spieler joinen darf */
		if(CivilCraft.mainLobby.requestJoin(e.getPlayer()) == false) {
			e.disallow(Result.KICK_OTHER, "§cKeine freie Lobby gefunden!\nVersuche es später erneut");
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		/* Permission Profile des Spielers wird geladen */
		PermissionManager.loadPermPlayer(e.getPlayer());
		/* SpielerProfile werden geladen */
		ProfileManager.registerProfile(e.getPlayer());
		ProfileManager.getProfile(e.getPlayer()).saveData();
		/* Statistiken Profile wird geladen */
		CivilCraft.getInstance().cstats.registerStats(e.getPlayer());
		/* Display-Technische Logiken */
		CivilCraft.getInstance().csb.setNewScoreboard(e.getPlayer());
		CivilCraft.getInstance().ctl.setTablist(e.getPlayer());
		/* Gamemode setzen */
		e.getPlayer().setGameMode(ProfileManager.getProfile(e.getPlayer()).current_gamemode);
		/* Join Nachricht wirdangepasst */
		e.setJoinMessage(FileManager.join_message.replace("{USER}", e.getPlayer().getName()));
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		/* Spieler Profil wird entladen*/
		CivilCraft.mainLobby.unloadPlayer(e.getPlayer());
		ProfileManager.unregisterProfile(e.getPlayer());
		/* Leave Nachricht wird angepasst */
		e.setQuitMessage(FileManager.leave_message.replace("{USER}", e.getPlayer().getName()));
	}
	
	@EventHandler
	public void onMOTD(ServerListPingEvent e) {
		e.setMotd(FileManager.MOTD);
		e.setMaxPlayers(FileManager.server_slots);
	}
	
	
}
