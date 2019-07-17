package me.ichmagomaskekse.de.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.ichmagomaskekse.de.CivilCraft;
import me.ichmagomaskekse.de.permissions.PermissionManager;

public class BlockBreakListener implements Listener {
	
	public BlockBreakListener() {
		CivilCraft.registerEvents(this);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		//DEBUG zwecke
		if(PermissionManager.hasPermission(e.getPlayer(), "modifyworld:everywhere") == false) e.setCancelled(true);
	}
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		//DEBUG zwecke
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(PermissionManager.hasPermission(e.getPlayer(), "interact_block:everything") == false) e.setCancelled(true);
		}else if(e.getAction() == Action.RIGHT_CLICK_AIR) {
			if(PermissionManager.hasPermission(e.getPlayer(), "interact_item:everything") == false) e.setCancelled(true);
		}
	}
	
}
