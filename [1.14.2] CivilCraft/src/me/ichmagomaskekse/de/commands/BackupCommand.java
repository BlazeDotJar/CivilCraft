package me.ichmagomaskekse.de.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.permissions.PermissionManager;

public class BackupCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			switch(args.length) {
			case 0:
				if(cmd.getName().equalsIgnoreCase("backup") && PermissionManager.hasPermission(sender, "backup")){	
					CivilCraft.getInstance().backupManager.createBackup();
					CivilCraft.sendInfo(sender, "§eBackup Manager", "§7Ein Backup wurde erstellt");
				}
				break;
			case 1:
				if(cmd.getName().equalsIgnoreCase("backup") && PermissionManager.hasPermission(sender, "backup")) {
					CivilCraft.getInstance().backupManager.createBackup(args[0]);
					CivilCraft.sendInfo(sender, "§eBackup Manager", "§7Ein Backup wurde erstellt.", "§7Mit dem Namen: §f"+args[0]);
				}
				break;
			}
		return false;
	}
	
}
