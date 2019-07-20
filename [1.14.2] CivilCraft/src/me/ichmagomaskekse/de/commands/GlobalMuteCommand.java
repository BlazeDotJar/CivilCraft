package me.ichmagomaskekse.de.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.PlayerAtlas;
import me.ichmagomaskekse.de.ProfileManager;
import me.ichmagomaskekse.de.filesystem.FileManager;
import me.ichmagomaskekse.de.permissions.PermissionManager;

public class GlobalMuteCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
//		if(sender instanceof Player) {
//	}
//			Player p = (Player) sender;
			switch(args.length) {
			case 0:
				if(cmd.getName().equalsIgnoreCase("globalmute") && PermissionManager.hasPermission(sender, "globalmute")){	
					CivilCraft.global_mute = !CivilCraft.global_mute;
					if(CivilCraft.global_mute) CivilCraft.sendBroadcast(sender.getName(), "",
							"                §eGLOBAL MUTE",
							"        §cDer Chat wurde gemutet.");
					else CivilCraft.sendBroadcast(sender.getName(), "",
							"                §eGLOBAL MUTE",
							"  §aDer Chat wurde wieder frei gegeben");
				} else if(cmd.getName().equalsIgnoreCase("mute") && PermissionManager.hasPermission(sender, "mute")) {
					sender.sendMessage("§f/mute [Spieler] §aChat-Rechte erteilen/entziehen");
				}
				break;
			case 1:
				if(cmd.getName().equalsIgnoreCase("mute") && PermissionManager.hasPermission(sender, "mute")) {
					UUID uuid = PlayerAtlas.getUUIDbyName(args[0]);
					if(uuid != null) {
						if(args[0].equals(sender.getName())) {CivilCraft.sendInfo(sender, "", "lol.", "Du wolltest dich gerade selbst muten"); return false;}
						if(ProfileManager.getProfile(uuid).mute()) CivilCraft.sendInfo(sender, "", "§aDu hast §f"+args[0]+" §aden Mund zugenäht");
						else CivilCraft.sendInfo(sender, "", "§f"+args[0]+" §akann nun wieder reden");
					} else CivilCraft.sendErrorInfo(sender, "", FileManager.player_not_found);
				}
				break;
			}
		return false;
	}
	
}
