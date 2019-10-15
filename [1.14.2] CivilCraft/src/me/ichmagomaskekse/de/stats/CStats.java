package me.ichmagomaskekse.de.stats;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.ichmagomaskekse.de.CivilCraft;

public class CStats implements Listener{
	
	/*
	 * Diese Klasse wird alle Statistiken speichern und erfassen
	 */
	
	private String stats_home = "/plugins/CivilCraft/Stats/";
	private HashMap<UUID, StatsProfile> profiles = new HashMap<UUID, StatsProfile>();
	
	public CStats() {
		CivilCraft.getInstance().getServer().getPluginManager().registerEvents(this, CivilCraft.getInstance());
		for(Player all : Bukkit.getOnlinePlayers()) {
			registerStats(all);
		}
	}
	
	public boolean saveStats(StatsProfile sp) {
		File file = new File(stats_home+"stats.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		String uuid = sp.p.getUniqueId().toString();
		cfg.set("Stats."+uuid+".Hostile Kills", sp.hostile_kills);
		cfg.set("Stats."+uuid+".Friendly Kills", sp.friendly_kills);
		cfg.set("Stats."+uuid+".Player Kills", sp.player_kills);
		cfg.set("Stats."+uuid+".Deaths", sp.deaths);
		cfg.set("Stats."+uuid+".Blocks Placed", sp.blocks_placed);
		cfg.set("Stats."+uuid+".Blocks Broken", sp.blocks_broken);
		
		try {
			cfg.save(file);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean loadStats(Player p) {
		if(profiles.containsKey(p.getUniqueId())) return false;
		File file = new File(stats_home+"stats.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		String uuid = p.getUniqueId().toString();
		StatsProfile sp = new StatsProfile(p);
		sp.hostile_kills = cfg.getInt("Stats."+uuid+".Hostile Kills");
		sp.friendly_kills = cfg.getInt("Stats."+uuid+".Friendly Kills");
		sp.player_kills = cfg.getInt("Stats."+uuid+".Player Kills");
		sp.deaths = cfg.getInt("Stats."+uuid+".Deaths");
		sp.blocks_placed = cfg.getInt("Stats."+uuid+".Blocks Placed");
		sp.blocks_broken = cfg.getInt("Stats."+uuid+".Blocks Broken");
		
		profiles.put(p.getUniqueId(), sp);
		return true;
	}
	
	public class StatsProfile {
		
		public Player p = null;
		public int hostile_kills = 0;
		public int friendly_kills = 0;
		public int player_kills = 0;
		public int deaths = 0;
		public int blocks_placed = 0;
		public int blocks_broken = 0;
		
		public StatsProfile(Player p) {
			this.p = p;
		}
		
	}
	
	public StatsProfile getStats(UUID uuid) {
		if(profiles.containsKey(uuid)) return profiles.get(uuid);
		else loadStats(Bukkit.getPlayer(uuid));
		return profiles.get(uuid);
	}
	
	Player killer = null;
	StatsProfile sp = null;
	@EventHandler
	public void onKill(EntityDeathEvent e) {
		killer = e.getEntity().getKiller();
		if(killer != null) {			
			if(e.getEntity() instanceof Monster) {
				getStats(killer.getUniqueId()).hostile_kills+=1;
				Bukkit.getPluginManager().callEvent(new StatsUpdateEvent(killer, "hostile_kills"));
			}else if(e.getEntity() instanceof Animals) {
				getStats(killer.getUniqueId()).friendly_kills+=1;
				Bukkit.getPluginManager().callEvent(new StatsUpdateEvent(killer, "friendly_kills"));
			}else if(e.getEntity() instanceof Player) {
				getStats(killer.getUniqueId()).player_kills+=1;
				Bukkit.getPluginManager().callEvent(new StatsUpdateEvent(killer, "player_kills"));
			}
			CivilCraft.getInstance().csb.setNewScoreboard(killer);
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		getStats(e.getEntity().getUniqueId()).deaths+=1;
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		getStats(e.getPlayer().getUniqueId()).blocks_placed+=1;
	}
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		getStats(e.getPlayer().getUniqueId()).blocks_broken+=1;
	}

	public boolean registerStats(Player player) {
		if(profiles.containsKey(player.getUniqueId())) return false;
		else profiles.put(player.getUniqueId(), new StatsProfile(player));
		return true;
	}
	
}
