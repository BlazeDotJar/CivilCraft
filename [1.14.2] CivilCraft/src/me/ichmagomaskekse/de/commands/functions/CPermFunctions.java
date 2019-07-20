package me.ichmagomaskekse.de.commands.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.PlayerAtlas;
import me.ichmagomaskekse.de.filesystem.FileManager;
import me.ichmagomaskekse.de.permissions.PermissionList;
import me.ichmagomaskekse.de.permissions.PermissionManager;
import me.ichmagomaskekse.de.permissions.PermissionManager.PermGroup;
import me.ichmagomaskekse.de.permissions.PermissionManager.PermPlayer;
import me.ichmagomaskekse.de.permissions.PermissionManager.PermissionOrigin;

public class CPermFunctions {
	//Diese Methode wird nur aufgerufen, wenn das 1te Argument eines Command 'perms' lautet
	public static boolean computeCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		switch(args.length) {
		case 1:
			//Das 1te Argument MUSS und kann nur 'perms' sein
			if(!PermissionManager.hasPermission(sender, "cadmin perms")) return false;
			sendPermsInfo(sender);
			break;
		case 2:
			if(args[1].equals("list")) {
				if(!PermissionManager.hasPermission(sender, "cadmin perms list")) return false;
				listPermissions(sender);
				
			} else if(args[1].equals("groups")) {
				if(!PermissionManager.hasPermission(sender, "cadmin perms groups")) return false;
				listGroups(sender);
				
			} else if(args[1].startsWith("G:") || args[1].startsWith("g:") || args[1].startsWith("g.") || args[1].startsWith("g.")) {
				sendGroupInfos(sender, args[1].substring(2, args[1].length()));
				
			} else {
				String arg = args[1];
				if(PlayerAtlas.getUUIDbyName(arg) == null) sendPermsInfo(sender);
				else {
					if(!PermissionManager.hasPermission(sender, "cadmin perms <PLAYER>")) return false;
					sendPlayerInfo(sender, arg);
				}
			}
			break;
		case 3:
			if(args[2].equals("")) {
				
			} else if(args[1].startsWith("G:") || args[1].startsWith("g:") || args[1].startsWith("g.")) {
				String group = args[1].substring(2, args[1].length());
				if(args[2].equals("add")) {
					if(!PermissionManager.hasPermission(sender, "cadmin perms <GROUP> add")) return false;
					sender.sendMessage("§cNutze §f/cadmin perms G:"+group+" add <§7Permission§f> §cum Gruppe "+group+" eine Permission erteilen");
				}else if(args[2].equals("remove")) {
					if(!PermissionManager.hasPermission(sender, "cadmin perms <GROUP> remove")) return false;
					sender.sendMessage("§cNutze §f/cadmin perms G:"+group+" remove <§7Permission§f> §cum Gruppe "+group+" eine Permission zu entziehen");
				}else if(args[2].equals("create")) {
					if(!PermissionManager.hasPermission(sender, "cadmin perms <GROUP> create")) return false;
					new PermGroup(group, true);
					CivilCraft.sendInfo(sender, "", "Gruppe §7"+group+" §fwurde erstellt");
				}else if(args[2].equals("prefix")) {
					if(!PermissionManager.hasPermission(sender, "cadmin perms <GROUP> prefix")) return false;
					sender.sendMessage("§cNutze §f/cadmin perms "+group+" prefix §<Prefix> set §cum den Prefix zu ändern");
				}else if(args[2].equals("suffix")) {
					if(!PermissionManager.hasPermission(sender, "cadmin perms <GROUP> suffix")) return false;
					sender.sendMessage("§cNutze §f/cadmin perms "+group+" suffix §<Suffix> set §cum den Suffix zu ändern");
				}
			} else {
				//Prüfen, ob das Argument 1 ein Spielername ist
				String arg = args[1];
				if(PlayerAtlas.getUUIDbyName(arg) == null) sendPermsInfo(sender);
				else {
					if(args[2].equals("add")) {
						if(!PermissionManager.hasPermission(sender, "cadmin perms <PLAYER> add")) return false;
						sender.sendMessage("§cNutze §f/cadmin perms "+arg+" add <§7Permission§f> §cum "+arg+" eine Permission erteilen");
					}else if(args[2].equals("remove")) {
						if(!PermissionManager.hasPermission(sender, "cadmin perms <PLAYER> remove")) return false;
						sender.sendMessage("§cNutze §f/cadmin perms "+arg+" remove <§7Permission§f> §cum "+arg+" eine Permission zu entziehen");
					}else if(args[2].equals("set")) {
						if(!PermissionManager.hasPermission(sender, "cadmin perms <PLAYER> set")) return false;
						sender.sendMessage("§cNutze §f/cadmin perms "+arg+" set g:<Perm-Group>");
					}
				}
			}
			break;
		case 4:
			if(args[2].equals("")) {
				
			} else if(args[1].startsWith("G:") || args[1].startsWith("g:") || args[1].startsWith("g.")) {
				String group = args[1].substring(2, args[1].length());
				String perm = args[3];
				if(args[2].equals("add")) {
					if(!PermissionManager.hasPermission(sender, "cadmin perms <GROUP> add")) return false;
					PermissionManager.getPermGroup(group).addPermission(perm);
					CivilCraft.sendInfo(sender, "", "Gruppe §7"+group+" §fwurde §7"+perm+" §ferteilt");
					
				}else if(args[2].equals("remove")) {
					if(!PermissionManager.hasPermission(sender, "cadmin perms <GROUP> remove")) return false;
					PermissionManager.getPermGroup(group).removePermission(perm);
					CivilCraft.sendInfo(sender, "", "Gruppe §7"+group+" §fwurde §7"+perm+" §fentzogen");
				}else if(args[2].equals("prefix")) {
					if(!PermissionManager.hasPermission(sender, "cadmin perms <GROUP> prefix")) return false;
					sender.sendMessage("§cNutze §f/cadmin perms "+group+" prefix §<Prefix> set §cum den Prefix zu ändern");
				}else if(args[2].equals("suffix")) {
					if(!PermissionManager.hasPermission(sender, "cadmin perms <GROUP> suffix")) return false;
					sender.sendMessage("§cNutze §f/cadmin perms "+group+" suffix §<Suffix> set §cum den Suffix zu ändern");
				}
			} else {
				//Prüfen, ob das Argument 1 ein Spielername ist
				String arg = args[1];
				if(PlayerAtlas.getUUIDbyName(arg) == null) sendPermsInfo(sender);
				else {
					if(args[2].equals("add")) {
						if(!PermissionManager.hasPermission(sender, "cadmin perms <PLAYER> add")) return false;
						PermissionManager.getPermPlayer(PlayerAtlas.getUUIDbyName(arg)).addPermission(PermissionOrigin.OWN, args[3]);
						CivilCraft.sendInfo(sender, "", arg+" wurde "+args[3]+" erteilt");
					}else if(args[2].equals("remove")) {
						if(!PermissionManager.hasPermission(sender, "cadmin perms <PLAYER> remove")) return false;
						PermissionManager.getPermPlayer(PlayerAtlas.getUUIDbyName(arg)).removePermission(args[3]);
						CivilCraft.sendInfo(sender, "", arg+" wurde "+args[3]+" entzogen");
					}else if(args[2].equals("set")) {
						if(!PermissionManager.hasPermission(sender, "cadmin perms <PLAYER> set")) return false;
						if(args[3].startsWith("G:") || args[3].startsWith("g:") || args[3].startsWith("g.") || args[3].startsWith("G.")) {
							String groupname = args[3].substring(2, args[3].length());
							PermissionManager.getPermPlayer(PlayerAtlas.getUUIDbyName(arg)).setGroup(groupname);
							CivilCraft.sendInfo(sender, "", "Gruppe §7"+groupname+"§f wurde gesetzt");
						}
					}
				}
			}
			break;
		case 5:
			if(args[2].equals("")) {
				
			} else if(args[1].startsWith("G:") || args[1].startsWith("g:") || args[1].startsWith("g.")) {
				String group = args[1].substring(2, args[1].length());
				if(args[4].equals("set")) {
					if(args[2].equals("prefix")) {
						if(!PermissionManager.hasPermission(sender, "cadmin perms <GROUP> prefix set")) return false;
						PermissionManager.getPermGroup(group).prefix = args[3].replace("&", "§").replaceAll("%a", " ");
						PermissionManager.getPermGroup(group).saveData(true);
						PermissionManager.refreshGroupData(PermissionManager.getPermGroup(group));
						CivilCraft.sendInfo(sender, "", "Prefix wurde zu '§f"+args[3].replace("&", "§").replaceAll("%a", " ")+"§f' §fgeändert");
					}else if(args[2].equals("suffix")) {
						if(!PermissionManager.hasPermission(sender, "cadmin perms <GROUP> suffix set")) return false;
						PermissionManager.getPermGroup(group).suffix = args[3].replace("&", "§").replaceAll("%a", " ");
						PermissionManager.getPermGroup(group).saveData(true);
						PermissionManager.refreshGroupData(PermissionManager.getPermGroup(group));
						CivilCraft.sendInfo(sender, "", "Suffix wurde zu '§f"+args[3].replace("&", "§").replaceAll("%a", " ")+"§f' geändert");
					}
				}
			}
			break;
			default:
				
				break;
		}
		
		
		return false;
	}
	
	private static void sendPermsInfo(CommandSender sender) {
		sender.sendMessage("");
		sender.sendMessage("         §6"+FileManager.server_prefix+"s Permission-System");
		sender.sendMessage(" /cadmin perms [...]");
		sender.sendMessage("  list §aListet alle verfügbaren Permissions auf");
		sender.sendMessage("  <Spielername> §aZeigt alle Perms-Daten eines Spielers");
		sender.sendMessage("  <Spielername> set g:<Perm-Group> §aPerm-Group setzen");
		sender.sendMessage("  <Spielername> [add/remove] <Permission> §aPermission erteilen/entziehen");
		sender.sendMessage("  groups §aZeigt alle erfassten Perm-Groups an");
		sender.sendMessage("  g:<Gruppenname> create §aEine neue Gruppe erstellen");
		sender.sendMessage("  g:<Gruppenname> [add/remove] <Permission> §aEiner Gruppe eine Permission erteilen/entziehen");
		sender.sendMessage("  g:<Gruppenname> [prefix/suffix] <Wert> set §aPrefix/Suffix einer Gruppe ändern");
	}
	
	private static void listPermissions(CommandSender sender) {
		sender.sendMessage("");
		sender.sendMessage("§e"+FileManager.server_prefix+" Permissions:");
		sender.sendMessage(" §7- [Command] §a[Permission]");
		HashMap<String, String> perms = PermissionList.getPermissions();
		for(String c : perms.keySet()) {
			sender.sendMessage(" - " + c+" §a"+perms.get(c));
		}
	}
	private static void listGroups(CommandSender sender) {
		sender.sendMessage("");
		sender.sendMessage("§6Von "+FileManager.server_prefix+" erfasste Gruppen:");
		for(String s : PermissionManager.registered_group_names) {
			sender.sendMessage(" - §7"+s);
		}
	}
	private static void sendGroupInfos(CommandSender sender, String group) {
		PermGroup pGroup = PermissionManager.getPermGroup(group);
		sender.sendMessage("§6Daten von der Gruppe '"+group+"'");
		sender.sendMessage("Prefix: §7"+pGroup.prefix);
		sender.sendMessage("        §7"+pGroup.prefix.replace("&", "§"));
		sender.sendMessage("Suffix: §7"+pGroup.suffix);
		sender.sendMessage("        §7"+pGroup.suffix.replace("&", "§"));
		sender.sendMessage("Erbung:");
		for(PermGroup pg : pGroup.inherit) sender.sendMessage(" - §6"+pg.groupname);
		sender.sendMessage("Permissions:");
		for(String permission : pGroup.permissions) sender.sendMessage(" - §f"+permission);
		sender.sendMessage("Anti-Permissions:");
		for(String permission : pGroup.anti_permissions) sender.sendMessage(" - §f"+permission);
	}
	
	//Sendet Perm-Infos eines Spielers
	private static ArrayList<String> registered_perms = new ArrayList<String>();
	private static ArrayList<String> registered_anti_perms = new ArrayList<String>();
	private static void sendPlayerInfo(CommandSender sender, String playername) {
		UUID tar_uuid = PlayerAtlas.getUUIDbyName(playername);
		PermPlayer pPlayer = PermissionManager.getPermPlayer(tar_uuid);
		sender.sendMessage("");
		CivilCraft.sendInfo(sender, "Perms", "Daten von §7"+playername);
		CivilCraft.sendInfo(sender, "Perms", "UUID: §7"+tar_uuid.toString());
		CivilCraft.sendInfo(sender, "Perms", "Gruppe: §6"+pPlayer.group.groupname);
		for(String s : pPlayer.group.permissions) if(registered_perms.contains(s) == false) registered_perms.add(s);
		for(String s : pPlayer.group.anti_permissions) if(registered_anti_perms.contains(s) == false) registered_perms.add(s);
		
		CivilCraft.sendInfo(sender, "Perms", "Permissions & Anti-Permissions:");
		CivilCraft.sendInfo(sender, "Perms", "§fWhite=Own §6Gold=Group §cRed=Group(Anti)");
		for(String perm : pPlayer.permissions.keySet()) {
			PermissionOrigin origin = pPlayer.permissions.get(perm);
			if(origin == PermissionOrigin.OWN){
				sender.sendMessage("§f - "+perm);
			}
			else if(origin == PermissionOrigin.GROUP) {
				if(perm.startsWith("-") == false) sender.sendMessage("§6 - "+perm);
				else sender.sendMessage("§c - §c-"+perm);
			}
		}
		for(String perm : pPlayer.group.anti_permissions) {
			if(perm.startsWith("-") == false) sender.sendMessage("§6 - "+perm);
			else sender.sendMessage("§c - §c"+perm);
		}
	}
	
}
