package me.ichmagomaskekse.de.permissions.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PermPlayerGroupSetEvent extends Event {
	
	private static HandlerList handlers = new HandlerList();
	
	private CommandSender sender = null;
	private String target_name = "";
	private String groupname = "";
	private boolean wasSuccessful = false;
	
	public PermPlayerGroupSetEvent(CommandSender sender, String target_name, String groupname, boolean wasSuccessful) {
		this.sender = sender;
		this.target_name = target_name;
		this.groupname = groupname;
		this.wasSuccessful = wasSuccessful;
	}
	public CommandSender getCommandSender() {
		return sender;
	}
	public String getTargetName() {
		return target_name;
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
