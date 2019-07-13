package me.ichmagomaskekse.de.commands;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.filesystem.FileManager;
import me.ichmagomaskekse.de.permissions.PermissionList;
import me.ichmagomaskekse.de.permissions.PermissionManager;

public class CadminCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(PermissionManager.hasPermission(p, "cadmin")) {
				if(args.length == 0) {
					sendCadminInfo(p);					
				}else if(args.length == 1) {
					if(args[0].equals("reload")) {
						if(!PermissionManager.hasPermission(p, "cadmin reload")) return false;
						CivilCraft.sendInfo(p, "", "Lade Daten neu");
						if(FileManager.reloadData()) {
							CivilCraft.sendInfo(p, "", "Daten wurden neu geladen!");
							sendVariablesFromFileManager(sender);
						}
					}else if(args[0].equals("permissions") || args[0].equals("perms")) {
						if(!PermissionManager.hasPermission(p, "cadmin permissions")) return false;
						p.sendMessage("");
						p.sendMessage("§6"+FileManager.server_prefix+" Permissions:");
						HashMap<String, String> perms = PermissionList.getPermissions();
						for(String c : perms.keySet()) {
							p.sendMessage(" - " + c+" §a"+perms.get(c));
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
						sendVariablesFromFileManager(sender);
					}
				}
			}
		}
		
		return false;
	}
	
	public void sendCadminInfo(CommandSender sender) {
		sender.sendMessage("§f/cadmin §aHauptbefehl für "+FileManager.server_prefix);
		sender.sendMessage("§f/cadmin reload §aLade alle Daten neu");
	}
	public void sendVariablesFromFileManager(CommandSender sender) {
		//Zusammenfassung, was neu geladen wurde
		sender.sendMessage("Neu geladen wurde:");
		sender.sendMessage("lobby_max_slots: "+FileManager.lobby_max_slots);
		sender.sendMessage("lobby_amount: "+FileManager.lobby_amount);
		sender.sendMessage("MOTD: "+FileManager.MOTD);
		sender.sendMessage("join_message: "+FileManager.join_message);
		sender.sendMessage("leave_message: "+FileManager.leave_message);
		sender.sendMessage("server_slots: "+FileManager.server_slots);
		sender.sendMessage("server_prefix: "+FileManager.server_prefix);
	}
	
}
