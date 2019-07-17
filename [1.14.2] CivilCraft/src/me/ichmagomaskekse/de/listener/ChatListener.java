package me.ichmagomaskekse.de.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.permissions.PermissionManager;
import me.ichmagomaskekse.de.permissions.PermissionManager.PermGroup;

public class ChatListener implements Listener {
	
	public ChatListener() {
		CivilCraft.registerEvents(this); 
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if(PermissionManager.hasPermission(p, "chat:colored", false)) {
			e.setMessage(e.getMessage().replace("&", "§"));
		}
		PermGroup group = PermissionManager.getPermPlayer(p.getUniqueId()).group;
		e.setFormat(group.prefix+" "+p.getName()+""+group.suffix+" "+e.getMessage());
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		if(e.getMessage().startsWith("/perms")) e.setMessage(e.getMessage().replace("/perms", "/cadmin perms"));
	}
	
}
