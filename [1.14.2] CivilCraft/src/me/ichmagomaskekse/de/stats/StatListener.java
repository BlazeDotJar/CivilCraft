package me.ichmagomaskekse.de.stats;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.ichmagomaskekse.de.CivilCraft;

public class StatListener implements Listener {
	
	public StatListener() {
		CivilCraft.getInstance().getServer().getPluginManager().registerEvents(this, CivilCraft.getInstance());
	}
	
	@EventHandler
	public void onStatsUpdate(StatsUpdateEvent e) {
		CivilCraft.getInstance().csb.updateScoreboard(e.getPlayer());
	}

}
