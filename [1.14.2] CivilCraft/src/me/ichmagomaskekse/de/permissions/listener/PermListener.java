package me.ichmagomaskekse.de.permissions.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.permissions.events.PermGroupCreatedEvent;
import me.ichmagomaskekse.de.permissions.events.PermPlayerGroupSetEvent;

public class PermListener implements Listener {
	
	public PermListener() {
		CivilCraft.getInstance().getServer().getPluginManager().registerEvents(this, CivilCraft.getInstance());
	}
	
	@EventHandler
	public void onGroupCreate(PermGroupCreatedEvent e) {
		Bukkit.broadcastMessage("§bEVENT: §fEine PermGroup wurde erstellt");
	}
	@EventHandler
	public void onPlayerGroupSet(PermPlayerGroupSetEvent e) {
		if(Bukkit.getPlayer(e.getTargetName()) != null)	{
			CivilCraft.getInstance().csb.setNewScoreboard(Bukkit.getPlayer(e.getTargetName()));
			CivilCraft.getInstance().ctl.setTablist(Bukkit.getPlayer(e.getTargetName()));
		}
		Bukkit.broadcastMessage("§bEVENT: §fEine PermGroup wurde aktualisiert");
	}
	
}
