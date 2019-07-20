package me.ichmagomaskekse.de.filesystem;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;

import com.google.common.io.Files;

import me.ichmagomaskekse.de.CivilCraft;

public class BackupManager {
	
	private String backup_path = "plugins/CivilCraft/Backup/";
	private HashMap<String, String> paths = new HashMap<String, String>();
	
	public BackupManager() {
		paths.put("plugins/CivilCraft/civilcraft.yml",          backup_path+"{DATE}/civilcraft.yml");
		paths.put("plugins/CivilCraft/player_atlas.yml",        backup_path+"{DATE}/player_atlas.yml");
		paths.put("plugins/CivilCraft/profiles.yml",            backup_path+"{DATE}/profiles.yml");
		paths.put("plugins/CivilCraft/Permissions/groups.yml",  backup_path+"{DATE}/Permissions/groups.yml");
		paths.put("plugins/CivilCraft/Permissions/players.yml", backup_path+"{DATE}/Permissions/players.yml");
		paths.put("plugins/CivilCraft/Teleportation/spawn.yml", backup_path+"{DATE}/Teleportation/spawn.yml");
	}
	
	public void createBackup(String target_name) {
		String date = "";
		if(target_name.equals("")) date = (CivilCraft.getDateInString()+" "+CivilCraft.getTimeInString()).replace(":", "-");
		else date = target_name.replace("?", "").replace("/", "")
				.replace("'\'", "").replace(":", "")
				.replace("?", "").replace("*", "")
				.replace("<", "").replace(">", "")
				.replace("|", "").replaceAll("%", " ");
		new File(backup_path+date).mkdirs();
		
		String[] splits = null;
		String raw_path = "";
		String splitted_path = "";
		for(String s : paths.keySet()) {
			File file = new File(s);
			if(file.exists()) {				
				try {
					splits = (paths.get(s).replace("{DATE}", date)).split("/");
					raw_path = (paths.get(s).replace("{DATE}", date));
					splitted_path = raw_path.substring(0, raw_path.length()-(splits[splits.length-1]).length());
					File to = new File(splitted_path);
					to.mkdir();
					to = new File((paths.get(s).replace("{DATE}", date)));
					CivilCraft.sendInfo(Bukkit.getConsoleSender(), "§eBackup Manager", "§eKopiere §f"+s+" §enach §f"+to.getPath());
					Files.copy(new File(s), to);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				try {
//					copyFileTo(s, (paths.get(s).replace("{DATE}", date)));
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
			}else CivilCraft.sendInfo(Bukkit.getConsoleSender(), "", "File: "+s+" existiert nicht");
		}
	}
	public void createBackup() {
		createBackup("");
	}
}
