package me.mchiappinam.pdghbroadcastall;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener {
	
	private Main plugin;
	public Listeners(Main main) {
		plugin=main;
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onJoin(final PlayerJoinEvent e) {
		if((e.getPlayer().getName().equalsIgnoreCase("buscar"))||(e.getPlayer().getName().equalsIgnoreCase("lista")))
			e.getPlayer().kickPlayer("§4§l[PDGHReportar]§c Esse nick não está liberado para uso. Escolha outro!");
		plugin.startTask(e.getPlayer());
	}
	  
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		plugin.cancelTask(e.getPlayer());
	}
		
	@EventHandler
	public void onPlayerKick(PlayerKickEvent e) {
		plugin.cancelTask(e.getPlayer());
	}
}