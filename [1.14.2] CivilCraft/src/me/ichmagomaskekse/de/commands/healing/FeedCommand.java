package me.ichmagomaskekse.de.commands.healing;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.filesystem.FileManager;
import me.ichmagomaskekse.de.permissions.PermissionManager;

public class FeedCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch(args.length) {
		case 0:
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(cmd.getName().equalsIgnoreCase("feed")) {
					if(PermissionManager.hasPermission(sender, "feed")) {
						p.setFoodLevel(20);
					}
				}				
			}
			break;
		case 1:
			if(cmd.getName().equalsIgnoreCase("feed")) {
				if(PermissionManager.hasPermission(sender, "feed") && PermissionManager.hasPermission(sender, "feed other")) {
					Player target = Bukkit.getPlayer(args[0]);
					if(target == null) CivilCraft.sendErrorInfo(sender, "", FileManager.player_not_found);
					else target.setFoodLevel(20);
				}
			}
			break;
		}
		return false;
	}
	
}
