package me.ichmagomaskekse.de.permissions;

import java.util.ArrayList;
import java.util.HashMap;

public class PermissionList {
	
	private static ArrayList<CommandPermission> permissions = new ArrayList<CommandPermission>();
	//OP-Permissions sind Permission mit einem *
	private static ArrayList<CommandPermission> op_permissions = new ArrayList<CommandPermission>();
	
	public static boolean addPermission(String command, String permission) {
		boolean exists = doesExist(command);
		if(exists) return false;
		else {
			if(permission.endsWith(".*")) op_permissions.add(new CommandPermission(command, permission));
			else permissions.add(new CommandPermission(command, permission));
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
	public static String getOpPermission(String command) {
		for(CommandPermission c : op_permissions) {
			if(c.cmd.equals(command)) return c.perm;
		}
		return "UNKNOWN OP-PERMISSION";
	}
	
	
	public static HashMap<String, String> getPermissions() {
		HashMap<String, String> perms = new HashMap<String, String>();
		
		for(CommandPermission c : permissions) {
			perms.put(c.cmd, c.perm);
		}
		
		return perms;
	}
	
	public static boolean knowsOpPermission(String command) {
		if(op_permissions.contains(command)) return true;
		else return false;
	}
	
	private static class CommandPermission {
		
		String cmd, perm;
		
		public CommandPermission(String command, String permission) {
			this.cmd = command;
			this.perm = permission;
		}
		
	}
}
