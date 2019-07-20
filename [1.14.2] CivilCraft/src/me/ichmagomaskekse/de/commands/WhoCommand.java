package me.ichmagomaskekse.de.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.PlayerAtlas;
import me.ichmagomaskekse.de.ProfileManager;
import me.ichmagomaskekse.de.ProfileManager.PlayerProfile;
import me.ichmagomaskekse.de.filesystem.FileManager;
import me.ichmagomaskekse.de.permissions.PermissionManager;

public class WhoCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch(args.length) {
		case 0:
			if(cmd.getName().equalsIgnoreCase("who") && PermissionManager.hasPermission(sender, "who")) {	
				CivilCraft.sendInfo(sender, "", "§f/who [Spieler] §aSehe die Profile-Daten eines Spielers");
			}
			break;
		case 1:
			if(cmd.getName().equalsIgnoreCase("who") && PermissionManager.hasPermission(sender, "who")) {
				UUID uuid = PlayerAtlas.getUUIDbyName(args[0]);
				if(uuid != null) {
					sendPlayerInfo(sender, uuid);
				} else CivilCraft.sendErrorInfo(sender, "", FileManager.player_not_found);
			}
			break;
		}
		return false;
	}
	
	public void sendPlayerInfo(CommandSender sender, UUID uuid) {
		PlayerProfile pp = ProfileManager.getProfile(uuid);
		sender.sendMessage("§eDas sind §f"+pp.playername+"s§e Daten:");
		sender.sendMessage(" UUID: §7"+uuid.toString());
		sender.sendMessage(" IP: §7"+pp.player_ip);
		sender.sendMessage(" Perm-Group: §7"+PermissionManager.getPermPlayer(uuid).group.groupname);
		sender.sendMessage(" Godmode: §7"+pp.godmode);
		sender.sendMessage(" Fly: §7"+pp.allow_fly);
		sender.sendMessage(" Chat mute: §7"+pp.chat_mute);
		sender.sendMessage(" Gamemode: §7"+pp.current_gamemode.toString());
		sender.sendMessage(" Last Location - World: §7"+pp.last_location.getWorld().getName());
		sender.sendMessage(" Last Location - X: §7"+pp.last_location.getX());
		sender.sendMessage(" Last Location - Y: §7"+pp.last_location.getY());
		sender.sendMessage(" Last Location - Z: §7"+pp.last_location.getZ());
	}
	
}
