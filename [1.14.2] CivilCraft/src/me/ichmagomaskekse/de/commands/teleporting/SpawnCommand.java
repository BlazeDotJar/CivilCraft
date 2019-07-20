package me.ichmagomaskekse.de.commands.teleporting;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.permissions.PermissionManager;

public class SpawnCommand implements CommandExecutor {
	
	private String spawn_file_path = "plugins/CivilCraft/Teleportation/spawn.yml";
	private File spawn_file = null;
	private FileConfiguration cfg = null;
	private Location spawn_location = null;
	
	public SpawnCommand() {
		spawn_file = new File(spawn_file_path);
		cfg = YamlConfiguration.loadConfiguration(spawn_file);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player) {
			Player p = (Player)sender;
			cfg = YamlConfiguration.loadConfiguration(spawn_file);
			if(cmd.getName().equalsIgnoreCase("spawn")) {
				if(PermissionManager.hasPermission(p, "spawn")) {
					if(spawn_location == null) {
						CivilCraft.sendDeveloperInformation("SpawnCommand", "§cFehler im Plugin:\n§dSpawnCommand.java - §cLocation spawn_location ist §5null§c, als versucht wurde diese Location zu benutzen.");
						loadSpawnLocation();
					}
					p.teleport(spawn_location);
				}				
			}else if(cmd.getName().equalsIgnoreCase("setspawn")) {
				if(PermissionManager.hasPermission(p, "setspawn")) {
					Location loc = p.getLocation(); 
					if(loc.add(0,-1,0).getBlock().getType().isSolid() == false) CivilCraft.sendInfo(sender, "", "§aINFO: §cDu stehst nicht auf festem Boden.\nWenn dies gewollt ist, ändere das in der '§fplugins/CivilCraft/Teleportation/spawn.yml§c' File um");
					cfg.set("Spawn.X", loc.getX());
					cfg.set("Spawn.Y", loc.getY()+1);
					cfg.set("Spawn.Z", loc.getZ());
					cfg.set("Spawn.Yaw", loc.getYaw());
					cfg.set("Spawn.Pitch", loc.getPitch());
					cfg.set("Spawn.World", loc.getWorld().getName());
					try {
						cfg.save(spawn_file);
						CivilCraft.sendInfo(p, "", "Spawn wurde gesetzt");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return false;
	}
	
	public void loadSpawnLocation() {
		cfg = YamlConfiguration.loadConfiguration(spawn_file);		
		double x,y,z;
		float yaw,pitch;
		String world;
		
		x = cfg.getDouble("Spawn.X");
		y = cfg.getDouble("Spawn.Y");
		z = cfg.getDouble("Spawn.Z");
		yaw = (float) cfg.getInt("Spawn.Yaw");
		pitch = (float) cfg.getInt("Spawn.Pitch");
		world = cfg.getString("Spawn.World");
		this.spawn_location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
	}
	
}
