package me.ichmagomaskekse.de.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.PlayerAtlas;
import me.ichmagomaskekse.de.filesystem.FileManager;
import me.ichmagomaskekse.de.permissions.PermissionList;
import me.ichmagomaskekse.de.permissions.PermissionManager;
import me.ichmagomaskekse.de.permissions.PermissionManager.PermPlayer;

public class CPermFunctions {
	//Diese Methode wird nur aufgerufen, wenn das 1te Argument eines Command 'perms' lautet
	public static boolean computeCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player) {
			Player p = (Player)sender;
			if(args.length == 1) {
				//Das 1te Argument MUSS und kann nur 'perms' sein
				if(!PermissionManager.hasPermission(p, "cadmin perms")) return false;
				sendPermsInfo(sender);
			}
			if(args.length == 2) {
				if(args[1].equals("list")) {
					if(!PermissionManager.hasPermission(p, "cadmin perm list")) return false;
					listPermissions(sender);
				} else {
					String arg = args[1];
					for(Player t : Bukkit.getOnlinePlayers()) {
						if(t.getName().equals(arg)) {
							if(!PermissionManager.hasPermission(p, "cadmin perms <PLAYER>")) return false;
							sendPlayerInfo(sender, arg);
						}
					}
				}
			}
		}
		
		return false;
	}
	
	private static void sendPermsInfo(CommandSender sender) {
		sender.sendMessage("");
		sender.sendMessage("         §6"+FileManager.server_prefix+"s Permission-System");
		sender.sendMessage(" /cadmin perms [...]");
		sender.sendMessage("  list §aListet alle verfügbaren Permissions auf");
		sender.sendMessage("  <Spielername> §aZeigt die alle Perms-Daten eines Spielers");
	}
	
	private static void listPermissions(CommandSender sender) {
		sender.sendMessage("");
		sender.sendMessage("§6"+FileManager.server_prefix+" Permissions:");
		HashMap<String, String> perms = PermissionList.getPermissions();
		for(String c : perms.keySet()) {
			sender.sendMessage(" - " + c+" §a"+perms.get(c));
		}
	}
	
	//Sendet Perm-Infos eines Spielers
	private static ArrayList<String> registered_perms = new ArrayList<String>();
	private static ArrayList<String> registered_anti_perms = new ArrayList<String>();
	private static void sendPlayerInfo(CommandSender sender, String playername) {
		UUID tar_uuid = PlayerAtlas.getUUIDbyName(playername);
		PermPlayer pPlayer = PermissionManager.getPermPlayer(tar_uuid);
		sender.sendMessage("");
		CivilCraft.sendInfo(sender, "Perms", "Daten von §7"+playername);
		CivilCraft.sendInfo(sender, "Perms", "UUID: §7"+tar_uuid.toString());
		CivilCraft.sendInfo(sender, "Perms", "Gruppe: §6"+pPlayer.group.groupname);
		for(String s : pPlayer.group.permissions) if(registered_perms.contains(s) == false) registered_perms.add(s);
		for(String s : pPlayer.group.anti_permissions) if(registered_anti_perms.contains(s) == false) registered_perms.add(s);
		
		CivilCraft.sendInfo(sender, "Perms", "Permissions & Anti-Permissions:");
		for(String s : pPlayer.permissions) sender.sendMessage(" - §f"+s);
		for(String s : registered_perms) sender.sendMessage(" - §6"+s);
		for(String s : registered_anti_perms) sender.sendMessage(" - §c-§6"+s);
	}
	
}
