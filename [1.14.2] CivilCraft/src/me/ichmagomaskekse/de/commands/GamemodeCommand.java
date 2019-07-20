package me.ichmagomaskekse.de.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.filesystem.FileManager;
import me.ichmagomaskekse.de.permissions.PermissionManager;

public class GamemodeCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			switch(args.length) {
			case 0:
				if(sender instanceof Player) {
					if(cmd.getName().equalsIgnoreCase("gamemode") && PermissionManager.hasPermission(sender, "gamemode"))
						sendCommandInfo(sender);
				}
				break;
			case 1:
				if(sender instanceof Player) {
					Player p = (Player) sender;
					if(cmd.getName().equalsIgnoreCase("gamemode") && PermissionManager.hasPermission(sender, "gamemode")) {
						if(args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("s")) p.setGameMode(GameMode.SURVIVAL);
						else if(args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("c")) p.setGameMode(GameMode.CREATIVE);
						else if(args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("adventure") || args[0].equalsIgnoreCase("a")) p.setGameMode(GameMode.ADVENTURE);
						else if(args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("spectator") || args[0].equalsIgnoreCase("sp")) p.setGameMode(GameMode.SPECTATOR);
						else sendCommandInfo(sender);
					}
				}
				break;
			case 2:
				if(cmd.getName().equalsIgnoreCase("gamemode") && PermissionManager.hasPermission(sender, "gamemode") && PermissionManager.hasPermission(sender, "gamemode other")) {
					Player target = Bukkit.getPlayer(args[1]);
					if(target == null) CivilCraft.sendErrorInfo(sender, "", FileManager.player_not_found);
					else {
						if(args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("s")) target.setGameMode(GameMode.SURVIVAL);
						else if(args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("c")) target.setGameMode(GameMode.CREATIVE);
						else if(args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("adventure") || args[0].equalsIgnoreCase("a")) target.setGameMode(GameMode.ADVENTURE);
						else if(args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("spectator") || args[0].equalsIgnoreCase("sp")) target.setGameMode(GameMode.SPECTATOR);
						else sendCommandInfo(sender);
					}
				}
				break;
			}
		return false;
	}
	
	public void sendCommandInfo(CommandSender sender) {
		sender.sendMessage("§f/gm [s/c/a/sp] [Spieler] §aSetze den Gamemode");
		sender.sendMessage("§f/gm [0/1/2/3] [Spieler] §aSetze den Gamemode");
		sender.sendMessage("§f/gm [survival/creative/adventure/spectator] [Spieler] §aSetze den Gamemode");
	}
	
}
