
package me.ichmagomaskekse.de.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.PlayerAtlas;
import me.ichmagomaskekse.de.ProfileManager;
import me.ichmagomaskekse.de.filesystem.FileManager;
import me.ichmagomaskekse.de.permissions.PermissionManager;

public class FlightCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch(args.length) {
		case 0:
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(cmd.getName().equalsIgnoreCase("fly") && PermissionManager.hasPermission(sender, "fly")){	
					if(ProfileManager.getProfile(p).toggleFlight()) CivilCraft.sendInfo(p, "", "Du kannst nun fliegen");
					else CivilCraft.sendInfo(p, "", "Du kannst nichtmehr fliegen");
				}
			}
			break;
		case 1:
			if(cmd.getName().equalsIgnoreCase("fly") && PermissionManager.hasPermission(sender, "fly other")) {
				UUID uuid = PlayerAtlas.getUUIDbyName(args[0]);
				if(uuid != null) {
					if(ProfileManager.getProfile(uuid).toggleFlight()) CivilCraft.sendInfo(sender, "", "§aDu hast §f"+args[0]+" §adas Fliegen ermöglicht");
					else CivilCraft.sendInfo(sender, "", "§f"+args[0]+" §akann nichtmehr länger fliegen");
				} else CivilCraft.sendErrorInfo(sender, "", FileManager.player_not_found);
			}
			break;
		}
		return false;
	}
	
}