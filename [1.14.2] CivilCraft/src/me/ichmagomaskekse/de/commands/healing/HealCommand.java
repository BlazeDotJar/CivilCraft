package me.ichmagomaskekse.de.commands.healing;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.filesystem.FileManager;
import me.ichmagomaskekse.de.permissions.PermissionManager;
import me.teamdream.de.server.AccountManager;

public class HealCommand implements CommandExecutor {
	int healing_price = 20;
	
	Random r = new Random();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch(args.length) {
		case 0:
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(cmd.getName().equalsIgnoreCase("heal")) {
					if(PermissionManager.hasPermission(sender, "heal")) {
//						AccountManager.addMoney(p.getUniqueId(), healing_price);
						p.setHealth(p.getHealthScale());
//						p.sendMessage("§aFür's Heilen, hast du jetzt "+healing_price+" Gold blechen müssen");
//						p.sendMessage("§aDein Kontostand: §7"+AccountManager.getMoney(p.getUniqueId()));
						AccountManager.sendMoney(UUID.fromString("e93f14bb-71c1-4379-bcf8-6dcc0a409ed9"), UUID.fromString("c3643210-81d3-429e-9535-646e57e36710"), r.nextInt(200));
					}
				}				
			}
			break;
		case 1:
			if(cmd.getName().equalsIgnoreCase("heal")) {
				if(PermissionManager.hasPermission(sender, "heal") && PermissionManager.hasPermission(sender, "heal other")) {
					Player target = Bukkit.getPlayer(args[0]);
					if(target == null) CivilCraft.sendErrorInfo(sender, "", FileManager.player_not_found);
					else target.setHealth(target.getHealthScale());
				}
			}
			break;
		}
		return false;
	}
	
}
