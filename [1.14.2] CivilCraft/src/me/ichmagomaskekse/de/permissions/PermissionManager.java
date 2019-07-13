package me.ichmagomaskekse.de.permissions;

import org.bukkit.entity.Player;

public class PermissionManager {
	
	public static boolean hasPermission(Player p, String command) {
		return p.hasPermission(PermissionList.getPermission(command));
	}
	
}
