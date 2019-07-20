package me.ichmagomaskekse.de.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.ProfileManager;

public class DamageListener implements Listener {
	
	public DamageListener() {
		CivilCraft.registerEvents(this); 
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player)e.getEntity();
			if(ProfileManager.getProfile(p).godmode == true) e.setCancelled(true);
		}
	}
	
}
