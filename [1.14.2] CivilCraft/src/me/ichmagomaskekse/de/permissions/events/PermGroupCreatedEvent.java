package me.ichmagomaskekse.de.permissions.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PermGroupCreatedEvent extends Event {
	
	private static HandlerList handlers = new HandlerList();
	
	private CommandSender sender = null;
	private String groupname = "";
	private boolean wasSuccessful = false;
	
	public PermGroupCreatedEvent(CommandSender sender, String groupname, boolean wasSuccessful) {
		this.sender = sender;
		this.groupname = groupname;
		this.wasSuccessful = wasSuccessful;
	}
	public CommandSender getCommandSender() {
		return sender;
	}
	public String getGroupName() {
		return groupname;
	}
	public boolean wasSuccessful() {
		return wasSuccessful;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
