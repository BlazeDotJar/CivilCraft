package me.ichmagomaskekse.de.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.filesystem.FileManager;
import me.ichmagomaskekse.de.permissions.PermissionManager;

public class CadminCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player) {
			Player p = (Player) sender;
			//DEBUG
			if(args.length == 1 && args[0].equals("check")) {
				p.sendMessage(PermissionManager.getPermPlayer(p.getUniqueId()).getAttachment().getPermissions().entrySet().toString());
			}
			//DEBUG
			if(PermissionManager.hasPermission(p, "cadmin")) {
				if(args.length == 0) {
					sendCadminInfo(p);					
				}else if(args.length == 1) {
					if(args[0].equals("reload")) {
						if(!PermissionManager.hasPermission(p, "cadmin reload")) return false;
						CivilCraft.reload(sender);
					}
				}
				if(args.length >= 1) {
					if(args[0].equals("perms")) {
						CPermFunctions.computeCommand(sender, cmd, label, args);
					}
				}
			}
		} else {
//			CivilCraft.sendErrorInfo(sender, "", "Dieser Befehl ist bisher nur für Spieler vorgesehen");
			if(args.length == 0) {
				sendCadminInfo(sender);					
			}else if(args.length == 1) {
				if(args[0].equals("reload")) {
					CivilCraft.reload(sender);
				}
			}
			if(args.length >= 1) {
				if(args[0].equals("perms")) {
					CPermFunctions.computeCommand(sender, cmd, label, args);
				}
			}
		}
		
		return false;
	}
	
	public void sendCadminInfo(CommandSender sender) {
		sender.sendMessage("§f/cadmin §aHauptbefehl für "+FileManager.server_prefix);
		sender.sendMessage("§f/cadmin reload §aLade alle Daten neu");
		sender.sendMessage("§f/cadmin perms §aCivilCraft Permission-System");
	}
	
}
