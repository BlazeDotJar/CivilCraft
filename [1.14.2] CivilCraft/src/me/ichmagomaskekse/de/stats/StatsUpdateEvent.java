package me.ichmagomaskekse.de.stats;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StatsUpdateEvent extends Event {
	
	private static HandlerList handlers = new HandlerList();
	private Player p = null;
	private String field_name = "";
	
	public StatsUpdateEvent(Player p, String field_name) {
		this.p = p;
		this.field_name = field_name;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public String getStatPath() {
		return field_name;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
}
