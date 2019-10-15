package me.ichmagomaskekse.de.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.ichmagomaskekse.de.CivilCraft;
import me.teamdream.de.server.AccountManager;
import me.teamdream.de.server.events.GeldEinzahlEvent;

public class GeldEinzahlListener implements Listener {

	public GeldEinzahlListener() {
		CivilCraft.getInstance().getServer().getPluginManager().registerEvents(this, CivilCraft.getInstance());
	}
	
	@EventHandler
	public void onEinzahlung(GeldEinzahlEvent e) {
		CivilCraft.getInstance().csb.updateCoins(Bukkit.getPlayer(e.getGetter()), AccountManager.getMoney(e.getGetter())-e.getAnzahl(), AccountManager.getMoney(e.getGetter()));
	}
	
}
