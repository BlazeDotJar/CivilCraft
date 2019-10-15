package me.ichmagomaskekse.de.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.ichmagomaskekse.de.CivilCraft;

public class PlayerRespawnListener implements Listener {
	
	public PlayerRespawnListener() {
		CivilCraft.getInstance().getServer().getPluginManager().registerEvents(this, CivilCraft.getInstance());
	}
	
	@EventHandler
	public void onDeath(PlayerRespawnEvent e) {
		e.setRespawnLocation(CivilCraft.getInstance().getSpawnLocation());
	}

}
