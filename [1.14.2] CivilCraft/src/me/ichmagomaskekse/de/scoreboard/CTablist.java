package me.ichmagomaskekse.de.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.ichmagomaskekse.de.filesystem.FileManager;
import me.ichmagomaskekse.de.permissions.PermissionManager;
import me.ichmagomaskekse.de.permissions.PermissionManager.PermGroup;
import me.ichmagomaskekse.de.permissions.PermissionManager.PermPlayer;

public class CTablist {
	
	public CTablist() {
		
	}
	
	public void setTablist(Player p) {
		PermPlayer pp = PermissionManager.getPermPlayer(p.getUniqueId());
		PermGroup pg = pp.group;
		p.setPlayerListName("§8[§b"+pg.prefix+"§8] §7"+p.getName());
		p.setPlayerListHeader("§6§lWillkommen auf §6§l"+FileManager.server_prefix+" §6§l!");
		p.setPlayerListFooter("§6§lRANG§7: §e"+pg.groupname+"\n§6§lTeamSpeak³: §e"+Bukkit.getServer().getIp()+"\n§cDieser Server steht unter erheblichen Wartungsarbeiten! Bei Fragen bitte an IchMagOmasKekse wenden!");
	}
	
}
