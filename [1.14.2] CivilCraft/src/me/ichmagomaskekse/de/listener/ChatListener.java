package me.ichmagomaskekse.de.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.ProfileManager;
import me.ichmagomaskekse.de.permissions.PermissionManager;
import me.ichmagomaskekse.de.permissions.PermissionManager.PermGroup;

public class ChatListener implements Listener {
	
	public ChatListener() {
		CivilCraft.registerEvents(this); 
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if(CivilCraft.global_mute == true) {
			if(PermissionManager.hasPermission(e.getPlayer(), "globalmute bypass", false) == false) {
				CivilCraft.sendInfo(e.getPlayer(), "", "Der Chat ist aktuell gemutet");
				e.setCancelled(true);
				return;
			}
		}
		if(ProfileManager.getProfile(e.getPlayer()).chat_mute == true && PermissionManager.hasPermission(e.getPlayer(), "mute bypass", false) == false) {
			CivilCraft.sendErrorInfo(e.getPlayer(), "", "Du bist stumgeschalten.", "Du kannst nichts in den Chat schreiben!");
			e.setCancelled(true);
			return;
		}
		Player p = e.getPlayer();
		if(PermissionManager.hasPermission(p, "chatcolor", false)) {
			e.setMessage(e.getMessage().replace("&", "§"));
		}
		PermGroup group = PermissionManager.getPermPlayer(p.getUniqueId()).group;
		e.setFormat(group.prefix+" "+p.getName()+""+group.suffix+" "+e.getMessage());
	}
	
}
