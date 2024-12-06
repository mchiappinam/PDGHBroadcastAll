package me.mchiappinam.pdghbroadcastall;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Comando implements CommandExecutor {
  private Main plugin;

  public Comando(Main main) {
    plugin = main;
  }

  public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
	if(cmd.getName().equalsIgnoreCase("reportar")) {
		if(args.length<1) {
			plugin.help((Player)sender);
			return true;
		}else if(args[0].equalsIgnoreCase("buscar")) {
			if((args.length<2)||(args.length>2)) {
				sender.sendMessage("§4§l[PDGHReportar]§c Use /reportar buscar <id>");
				return true;
			}
			if(!plugin.isValidNumber(args[1])) {
				sender.sendMessage("§4§l[PDGHReportar]§c Apenas números de 1 à 999999999.");
				return true;
			}
	        if(plugin.pendente.contains(sender.getName().toLowerCase())) {
	        	sender.sendMessage("§4§l[PDGHReportar]§r §c§lVocê já tem uma solicitação pendente ao banco de dados. Aguarde!");
	        	return true;
	        }
	        plugin.pendente.add(sender.getName().toLowerCase());
	        if(sender.hasPermission("pdgh.moderador")) {
				Threads t = new Threads(plugin,"buscar",((Player)sender),Integer.parseInt(args[1]));
				t.start();
	        }else{
				Threads t = new Threads(plugin,"buscarmeusreportes",((Player)sender),Integer.parseInt(args[1]));
				t.start();
	        }
			return true;
		}else if(args[0].equalsIgnoreCase("lista")) {
			if(args.length>1) {
				sender.sendMessage("§4§l[PDGHReportar]§c Use /reportar lista");
				return true;
			}
	        if(plugin.pendente.contains(sender.getName().toLowerCase())) {
	        	sender.sendMessage("§4§l[PDGHReportar]§r §c§lVocê já tem uma solicitação pendente ao banco de dados. Aguarde!");
	        	return true;
	        }
	        plugin.pendente.add(sender.getName().toLowerCase());
	        if(sender.hasPermission("pdgh.moderador")) {
				Threads t = new Threads(plugin,"lista",((Player)sender));
				t.start();
	        }else{
				Threads t = new Threads(plugin,"listameusreportes",((Player)sender), sender.getName().toLowerCase().trim());
				t.start();
	        }
			return true;
		}
		if(sender.hasPermission("pdgh.moderador"))
			if(args[0].equalsIgnoreCase("resolver")) {
				if(args.length<3) {
					sender.sendMessage("§4§l[PDGHReportar]§c Use /reportar resolver <id> <resposta da staff. exemplo: 'punido por uso de hacker'>");
					return true;
				}
				if(!plugin.isValidNumber(args[1])) {
					sender.sendMessage("§4§l[PDGHReportar]§c Apenas números de 1 à 999999999.");
					return true;
				}
		        if(plugin.pendente.contains(sender.getName().toLowerCase())) {
		        	sender.sendMessage("§4§l[PDGHReportar]§r §c§lVocê já tem uma solicitação pendente ao banco de dados. Aguarde!");
		        	return true;
		        }
		        StringBuilder sb = new StringBuilder();
		        sb.append(args[2]);
		        for (int i = 3; i < args.length; i++) {
		          sb.append(" ");
		          sb.append(args[i]);
		        }
		        plugin.pendente.add(sender.getName().toLowerCase());
				Threads t = new Threads(plugin,"resolver",((Player)sender), sender.getName().toLowerCase().trim(), sb.toString().toLowerCase().trim(), Integer.parseInt(args[1]));
				t.start();
				return true;
			}
		if(args.length<2) {
			plugin.help((Player)sender);
			return true;
		}
		if(plugin.getServer().getPlayerExact(args[0])==null) {
			sender.sendMessage("§4§l[PDGHReportar]§c Jogador "+args[0]+" não está online!");
			return true;
		}
		if(plugin.getServer().getPlayerExact(args[0])==sender) {
			sender.sendMessage("§4§l[PDGHReportar]§c Você não pode se reportar!");
			return true;
		}
        if(plugin.pendente.contains(sender.getName().toLowerCase())) {
        	sender.sendMessage("§4§l[PDGHReportar]§r §c§lVocê já tem uma solicitação pendente ao banco de dados. Aguarde!");
        	return true;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(args[1]);
        for (int i = 2; i < args.length; i++) {
          sb.append(" ");
          sb.append(args[i]);
        }
        plugin.pendente.add(sender.getName().toLowerCase());
		Threads t = new Threads(plugin,"reportar",((Player)sender),args[0].toLowerCase().trim(), sender.getName().toLowerCase().trim(), sb.toString().toLowerCase().trim());
		t.start();
		return true;
	}
    return false;
  }
  
  
  
  
  
  
}