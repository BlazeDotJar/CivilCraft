package me.ichmagomaskekse.de.commands;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.filesystem.FileManager;
import me.ichmagomaskekse.de.permissions.PermissionList;

public class CadminCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(p.hasPermission(PermissionList.getPermission("cadmin"))) {
				if(args.length == 0) {
					sendCadminInfo(p);					
				}else if(args.length == 1) {
					if(args[0].equals("reload")) {
						if(!p.hasPermission(PermissionList.getPermission("cadmin reload"))) return false;
						CivilCraft.sendInfo(p, "", "Lade Daten neu");
						if(FileManager.reloadData()) {
							CivilCraft.sendInfo(p, "", "Daten wurden neu geladen!");
						}
					}else if(args[0].equals("permissions") || args[0].equals("perms")) {
						if(!p.hasPermission(PermissionList.getPermission("cadmin permissions"))) return false;
						p.sendMessage("");
						p.sendMessage("§6CivilCraft Permissions:");
						HashMap<String, String> perms = PermissionList.getPermissions();
						for(String c : perms.keySet()) {
							p.sendMessage(" - "+c+" §a"+perms.get(c));
						}
					}
				}
			}
		} else {
//			CivilCraft.sendErrorInfo(sender, "", "Dieser Befehl ist bisher nur für Spieler vorgesehen");
			if(args.length == 0) {
				sendCadminInfo(sender);					
			}else if(args.length == 1) {
				if(args[0].equals("reload")) {
					CivilCraft.sendInfo(sender, "", "Lade Daten neu");
					if(FileManager.reloadData()) {
						CivilCraft.sendInfo(sender, "", "Daten wurden neu geladen!");
					}
				}
			}
		}
		
		return false;
	}
	
	public void sendCadminInfo(CommandSender sender) {
		sender.sendMessage("§f/cadmin §aHauptbefehl für "+FileManager.server_prefix);
		sender.sendMessage("§f/cadmin reload §aLade alle Daten neu");
		sender.sendMessage("§f/cadmin permissions §aLade alle Daten neu");
		
	}
	
}
