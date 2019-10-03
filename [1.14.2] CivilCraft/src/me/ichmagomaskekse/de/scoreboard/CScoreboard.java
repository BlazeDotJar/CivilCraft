package me.ichmagomaskekse.de.scoreboard;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import me.ichmagomaskekse.de.filesystem.FileManager;
import me.ichmagomaskekse.de.permissions.PermissionManager;
import me.ichmagomaskekse.de.permissions.PermissionManager.PermPlayer;

public class CScoreboard {
	
	public CScoreboard() {
		
	}
	
	public void setNewScoreboard(Player p) {
		Scoreboard sb = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		PermPlayer pp = PermissionManager.getPermPlayer(p.getUniqueId());
		sb.registerNewObjective("aaa", "bbb", FileManager.server_prefix);
		Objective obj = sb.getObjective("aaa");
		
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.getScore("§6Rang§7: §e"+pp.group.groupname).setScore(100);
		obj.getScore("§6Kills§7: §e0").setScore(99);
		obj.getScore("§6Deaths§7: §e0").setScore(98);
		obj.getScore("§6Coins§7: §e0").setScore(97);
		obj.getScore("§8§u-------------").setScore(96);
		obj.getScore("§6Area§7: §eWildnis").setScore(95);
		
		p.setScoreboard(sb);
	}
	
}
