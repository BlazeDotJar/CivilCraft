package me.ichmagomaskekse.de.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.PlayerAtlas;
import me.ichmagomaskekse.de.filesystem.FileManager;
import me.ichmagomaskekse.de.permissions.PermissionManager;

public class PermissionCheckerCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch(args.length) {
		case 0:
			if(cmd.getName().equalsIgnoreCase("permcheck") && PermissionManager.hasPermission(sender, "permcheck")) {	
				CivilCraft.sendInfo(sender, "", "§f/permcheck [Spieler] [Permissiom] §aEine Permission abfragen");
			}
			break;
		case 1: 
			if(cmd.getName().equalsIgnoreCase("permcheck") && PermissionManager.hasPermission(sender, "permcheck")) {	
				CivilCraft.sendInfo(sender, "", "§f/permcheck [Spieler] [Permissiom] §aEine Permission abfragen");
			}
			break;
		case 2:
			if(cmd.getName().equalsIgnoreCase("permcheck") && PermissionManager.hasPermission(sender, "permcheck")) {
				UUID uuid = PlayerAtlas.getUUIDbyName(args[0]);
				if(uuid != null) {
					if(Bukkit.getPlayer(args[0]).hasPermission(args[1])) sender.sendMessage("§a"+args[1]+" §fist vorhanden");
					else sender.sendMessage("§c"+args[1]+" §fist nicht vorhanden");
				} else CivilCraft.sendErrorInfo(sender, "", FileManager.player_not_found);
			}
			break;
		}
		return false;
	}
	
}
