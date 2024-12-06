package me.mchiappinam.pdghbroadcastall;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
    protected String mysql_url = "jdbc:mysql://localhost:3306/reportar";
    protected String mysql_user = "root";
    protected String mysql_pass = "5ebFj1EYOhpyu0tJ47";
    protected String servidor = "ERRO";
	public List<String> pendente = new ArrayList<String>();
	public HashMap<String, Integer> taskIDs = new HashMap<String, Integer>();
	
	public void onEnable() {
		getServer().getPluginCommand("reportar").setExecutor(new Comando(this));
	    getServer().getPluginManager().registerEvents(new Listeners(this), this);

		File file = new File(getDataFolder(),"config.yml");
		if(!file.exists()) {
			try {
				saveResource("config_template.yml",false);
				File file2 = new File(getDataFolder(),"config_template.yml");
				file2.renameTo(new File(getDataFolder(),"config.yml"));
			}catch(Exception e) {}
		}
		try {
			Connection con = DriverManager.getConnection(mysql_url,mysql_user,mysql_pass);
			if (con == null) {
				getLogger().warning("ERRO: Conexao ao banco de dados MySQL falhou!");
				getServer().getPluginManager().disablePlugin(this);
			}else{
				Statement st = con.createStatement();
				st.execute("CREATE TABLE IF NOT EXISTS `reportes` ( `id` MEDIUMINT NOT NULL AUTO_INCREMENT, `servidor` text, `datareportado` text, `reportado` text, `reportadopor` text, `motivo` text, `dataresolvido` text, `resolvidopor` text, `resposta` text, `resolvido` INT(255), PRIMARY KEY (`id`))");
				st.execute("CREATE TABLE IF NOT EXISTS `sendmsgifonline` ( `servidor` text, `jogador` text, `reporteid` MEDIUMINT, `staff` boolean)");
				//st.execute("ALTER TABLE  `rankpvp` CHANGE  `nome` `nome` VARCHAR(30)"); /* Retira código na próxima versão */
				st.close();
				getServer().getConsoleSender().sendMessage("§3[PDGHReportar] §3Conectado ao banco de dados MySQL!");
			}
			con.close();
		}catch (SQLException e) {
			getLogger().warning("ERRO: Conexao ao banco de dados MySQL falhou!");
			getLogger().warning("ERRO: "+e.toString());
			getServer().getPluginManager().disablePlugin(this);
		}
		
		servidor=getConfig().getString("servidor");
		getServer().getConsoleSender().sendMessage("§3[PDGHReportar] §2Servidor detectado: "+servidor);
		
		getServer().getConsoleSender().sendMessage("§3[PDGHReportar] §2ativado - Plugin by: mchiappinam");
		getServer().getConsoleSender().sendMessage("§3[PDGHReportar] §2Acesse: http://pdgh.com.br/");
	}

	public void onDisable() {
		getServer().getConsoleSender().sendMessage("§3[PDGHReportar] §2desativado - Plugin by: mchiappinam");
		getServer().getConsoleSender().sendMessage("§3[PDGHReportar] §2Acesse: http://pdgh.com.br/");
	}

	public void help(Player p) {
		p.sendMessage("§3§l[PDGHReportar]");
		p.sendMessage("§c/reportar <jogador> <motivo> -§4- Reporta o jogador.");
		p.sendMessage("§c/reportar lista -§4- Mostra seus 8 últimos reportes.");
		p.sendMessage("§c/reportar buscar <id> -§4- Mostra informações do seu reporte.");
		if(p.hasPermission("pdgh.moderador")) {
			p.sendMessage("§4>=Moderador...");
			p.sendMessage("§4Seus comandos são diferentes...:");
			p.sendMessage("§4/reportar lista -- Mostra todos os reportes não resolvidos.");
			p.sendMessage("§4/reportar buscar <id> -- Mostra informações do reporte desejado.");
			p.sendMessage("§4/reportar resolver <id> <resposta da staff. exemplo: 'punido por uso de hacker'> -- Resolve um reporte pendente.");
		}
	}
	
	public boolean isValidNumber(String args) {
		try{
			if(!StringUtils.isNumeric(args)) {
				return false;
			}
			int valor=Integer.parseInt(args);
			if(!(valor>0)&&(valor<=999999999)) {
				return false;
			}
			return true;
		}catch (NumberFormatException nfe){
			return false;
		}
	}
	
	public void startTask(final Player p) {
		taskIDs.put(p.getName(), getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				join(p);
			}
		}, 30*20L));
	}
	
	public void join(Player p) {
		Threads t = new Threads(this,"join",p,p.getName().toLowerCase().trim());
		t.start();
	}
	
	public void cancelTask(Player p) {
		if(taskIDs.get(p.getName())!=null)
			Bukkit.getScheduler().cancelTask(taskIDs.get(p.getName()));
	}
  
}