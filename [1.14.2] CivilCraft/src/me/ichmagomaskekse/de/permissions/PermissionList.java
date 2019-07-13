package me.ichmagomaskekse.de.permissions;

import java.util.ArrayList;
import java.util.HashMap;

public class PermissionList {
	
	private static ArrayList<CommandPermission> permissions = new ArrayList<CommandPermission>();
	
	public static boolean addPermission(String command, String permission) {
		boolean exists = doesExist(command);
		if(exists) return false;
		else {
			permissions.add(new CommandPermission(command, permission));
			return true;
		}
	}
	
	//Gibt zurück, ob eine Permission bereits registriert ist
	public static boolean doesExist(String command_ODER_permission) {
		for(CommandPermission c : permissions) {
			if(c.cmd.equals(command_ODER_permission)) return true;
			if(c.perm.equals(command_ODER_permission)) return true;
		}
		return false;
	}
	
	//Gibt die zugehörige Permission eines Befehls zurück
	public static String getPermission(String command) {
		for(CommandPermission c : permissions) {
			if(c.cmd.equals(command)) return c.perm;
		}
		return "UNKNOWN PERMISSION";
	}
	
	
	public static HashMap<String, String> getPermissions() {
		HashMap<String, String> perms = new HashMap<String, String>();
		
		for(CommandPermission c : permissions) {
			perms.put(c.cmd, c.perm);
		}
		
		return perms;
	}
	
	private static class CommandPermission {
		
		String cmd, perm;
		
		public CommandPermission(String command, String permission) {
			this.cmd = command;
			this.perm = permission;
		}
		
	}
}
