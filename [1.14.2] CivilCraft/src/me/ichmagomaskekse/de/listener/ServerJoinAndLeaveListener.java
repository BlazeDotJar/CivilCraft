package me.ichmagomaskekse.de.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.server.ServerListPingEvent;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.filesystem.FileManager;

public class ServerJoinAndLeaveListener implements Listener {
	
	public ServerJoinAndLeaveListener() {
		CivilCraft.registerEvents(this);
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		if(CivilCraft.mainLobby.requestJoin(e.getPlayer()) == false) {
			e.disallow(Result.KICK_OTHER, "§cKeine freie Lobby gefunden!\nVersuche es später erneut");
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.setJoinMessage(FileManager.join_message.replace("{USER}", e.getPlayer().getName()));
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		e.setQuitMessage(FileManager.leave_message.replace("{USER}", e.getPlayer().getName()));
		CivilCraft.mainLobby.unloadPlayer(e.getPlayer());
	}
	
	@EventHandler
	public void onMOTD(ServerListPingEvent e) {
		e.setMotd(FileManager.MOTD);
	}
	
}
