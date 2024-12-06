package me.mchiappinam.pdghbroadcastall;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.entity.Player;

public class Threads extends Thread {
	private Main plugin;
	private String tipo;

	private String p1_string;
	private String p2_string;
	private String motivo;
	private int numero;
	private Player sender;
	public Threads(Main pl,String tipo2,Player P,String player, String motivo2,int num) {
		plugin=pl;
		tipo=tipo2;
		sender=P;
		p1_string=player;
		motivo=motivo2;
		numero=num;
	}
	public Threads(Main pl,String tipo2,Player P,String player,String player2,String motivo2) {
		plugin=pl;
		tipo=tipo2;
		sender=P;
		p1_string=player;
		p2_string=player2;
		motivo=motivo2;
	}
	public Threads(Main pl,String tipo2,Player player2) {
		plugin=pl;
		tipo=tipo2;
		sender=player2;
	}
	public Threads(Main pl,String tipo2,Player player2,String player) {
		plugin=pl;
		tipo=tipo2;
		sender=player2;
		p1_string=player;
	}
	public Threads(Main pl,String tipo2,Player p,int num) {
		plugin=pl;
		tipo=tipo2;
		numero=num;
		sender=p;
	}
    
    public static String calendario() {
		Calendar agora = Calendar.getInstance();
		SimpleDateFormat gdf = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss.SSS");
        return gdf.format(agora.getTime());
    }
	
	public void run() {
		switch(tipo) {
			case "reportar": {
				String data = calendario().trim();
				try {
					Connection con = DriverManager.getConnection(plugin.mysql_url,plugin.mysql_user,plugin.mysql_pass);
					//Prepared statement
					PreparedStatement pst = con.prepareStatement("SELECT `id`,`servidor`,`reportado`,`datareportado`,`motivo` FROM `reportes` WHERE( `reportadopor`='"+p2_string.trim()+"' AND `servidor`='"+plugin.servidor.trim()+"' AND `resolvido`=0);");
					ResultSet rs = pst.executeQuery();
					boolean existe=false;
					while(rs.next()) {
							if(sender==null) {
								plugin.pendente.remove(sender.getName().toLowerCase());
								rs.close();
								pst.close();
								con.close();
								break;
							}
							sender.sendMessage("§4§l[PDGHReportar]");
							sender.sendMessage("§cVocê reportou o jogador "+rs.getString("reportado")+" e ele ainda está em moderação. Aguarde o mesmo ser resolvido para você conseguir reportar outro jogador.");
							sender.sendMessage("§8Informações adicionais:");
							sender.sendMessage("§8ID #"+rs.getInt("id"));
							sender.sendMessage("§8Data-horário: "+rs.getString("datareportado"));
							sender.sendMessage("§8Servidor: "+rs.getString("servidor"));
							sender.sendMessage("§8Motivo: "+rs.getString("motivo"));
							sender.sendMessage("§4§l[PDGHReportar]");
							existe=true;
					}
					if(existe) {
						plugin.pendente.remove(sender.getName().toLowerCase());
						rs.close();
						pst.close();
						con.close();
						break;
					}
				} catch (SQLException ex) {
					System.out.print(ex);
					sender.sendMessage("§cErro! Contate um staffer!");
					break;
				}
				try {
					Connection con = DriverManager.getConnection(plugin.mysql_url,plugin.mysql_user,plugin.mysql_pass);
					
					//Prepared statement
					PreparedStatement pst = con.prepareStatement("INSERT INTO reportes(servidor, datareportado, reportado, reportadopor, motivo, resolvido) VALUES(?, ?, ?, ?, ?, ?)");
					//Values
					pst.setString(1, plugin.servidor.trim());
					pst.setString(2, data);
					pst.setString(3, p1_string.trim());
					pst.setString(4, p2_string.trim());
					pst.setString(5, motivo.trim());
					pst.setInt(6, 0);
					//Do the MySQL query
					pst.executeUpdate();
					pst.close();
					con.close();
				} catch (SQLException ex) {
					System.out.print(ex);
					sender.sendMessage("§cErro! Contate um staffer!");
					break;
				}
				try {
					Connection con = DriverManager.getConnection(plugin.mysql_url,plugin.mysql_user,plugin.mysql_pass);
					//Prepared statement
					PreparedStatement pst = con.prepareStatement("SELECT `id` FROM `reportes` WHERE( `reportadopor`='"+p2_string.trim()+"' AND `resolvido`=0);");
					ResultSet rs = pst.executeQuery();
					if(rs.next()) {
						if(sender==null) {
							plugin.pendente.remove(sender.getName().toLowerCase());
							rs.close();
							pst.close();
							con.close();
							break;
						}
						sender.sendMessage("§a§l[PDGHReportar]");
						sender.sendMessage("§aVocê("+sender.getName()+") reportou "+p1_string.trim()+" com sucesso");
						sender.sendMessage("§8Informações adicionais:");
						sender.sendMessage("§8ID #"+rs.getInt("id"));
						sender.sendMessage("§8Data-horário: "+data);
						sender.sendMessage("§8Servidor: "+plugin.servidor.trim());
						sender.sendMessage("§8Motivo: "+motivo.trim());
						sender.sendMessage("§a§l[PDGHReportar]");
						plugin.pendente.remove(sender.getName().toLowerCase());
						rs.close();
						pst.close();
						con.close();
						break;
					}
				} catch (SQLException ex) {
					System.out.print(ex);
					sender.sendMessage("§cErro! Contate um staffer!");
					break;
				}
			}
			case "lista": {
		        try {
		        	Connection con = DriverManager.getConnection(plugin.mysql_url,plugin.mysql_user,plugin.mysql_pass);
					//Prepared statement
					PreparedStatement pst = con.prepareStatement("SELECT `id`,`servidor`,`reportado`,`motivo` FROM `reportes` WHERE( `resolvido`=0) ORDER BY `id`,`servidor` ASC;");
					ResultSet rs = pst.executeQuery();
					boolean empty = true;
					sender.sendMessage("§a§l[PDGHReportar]");
		            while(rs.next()) {
						if(sender==null) {
							plugin.pendente.remove(sender.getName().toLowerCase());
							rs.close();
							pst.close();
							con.close();
							break;
						}
		            	sender.sendMessage("§9#§2"+rs.getInt("id")+"§9. Servidor: "+rs.getString("servidor")+". Acusado: "+rs.getString("reportado")+". Motivo: "+rs.getString("motivo"));
		            	empty=false;
		            }
		            if(empty) {
						if(sender==null) {
							plugin.pendente.remove(sender.getName().toLowerCase());
							rs.close();
							pst.close();
							con.close();
							break;
						}
		            	sender.sendMessage("§aSem reportes pendentes :)");
		            }
					if(sender==null) {
						plugin.pendente.remove(sender.getName().toLowerCase());
						rs.close();
						pst.close();
						con.close();
						break;
					}
					sender.sendMessage("§a§l[PDGHReportar]");

					plugin.pendente.remove(sender.getName().toLowerCase());
					rs.close();
					pst.close();
					con.close();
					break;
				} catch (SQLException ex) {
					System.out.print(ex);
					sender.sendMessage("§cErro! Contate um staffer!");
					break;
		        }
			}
			case "listameusreportes": {
		        try {
		        	Connection con = DriverManager.getConnection(plugin.mysql_url,plugin.mysql_user,plugin.mysql_pass);
					//Prepared statement
					PreparedStatement pst = con.prepareStatement("SELECT `id`,`servidor`,`reportado`,`motivo`,`resolvido` FROM `reportes` WHERE( `reportadopor`='"+p1_string+"' AND `servidor`='"+plugin.servidor.trim()+"') ORDER BY `id` DESC LIMIT 8;");
					ResultSet rs = pst.executeQuery();
					boolean empty = true;
					sender.sendMessage("§a§l[PDGHReportar]");
		            while(rs.next()) {
						if(sender==null) {
							plugin.pendente.remove(sender.getName().toLowerCase());
							rs.close();
							pst.close();
							con.close();
							break;
						}
						if(rs.getInt("resolvido")!=0)
							sender.sendMessage("§9#§2"+rs.getInt("id")+"§9. Servidor: "+rs.getString("servidor")+". Acusado: "+rs.getString("reportado")+". Motivo: "+rs.getString("motivo"));
						else if(rs.getInt("resolvido")==0)
							sender.sendMessage("§2#§2"+rs.getInt("id")+"§2. Servidor: "+rs.getString("servidor")+". Acusado: "+rs.getString("reportado")+". Motivo: "+rs.getString("motivo"));
		            	empty=false;
		            }
		            if(empty) {
						if(sender==null) {
							plugin.pendente.remove(sender.getName().toLowerCase());
							rs.close();
							pst.close();
							con.close();
							break;
						}
		            	sender.sendMessage("§aVocê não tem reportes...");
		            }
					if(sender==null) {
						plugin.pendente.remove(sender.getName().toLowerCase());
						rs.close();
						pst.close();
						con.close();
						break;
					}
					sender.sendMessage("§a§l[PDGHReportar]");

					plugin.pendente.remove(sender.getName().toLowerCase());
					rs.close();
					pst.close();
					con.close();
					break;
				} catch (SQLException ex) {
					System.out.print(ex);
					sender.sendMessage("§cErro! Contate um staffer!");
					break;
		        }
			}
			case "buscar": {
		        try {
		        	Connection con = DriverManager.getConnection(plugin.mysql_url,plugin.mysql_user,plugin.mysql_pass);
					//Prepared statement
					PreparedStatement pst = con.prepareStatement("SELECT `id`,`servidor`,`datareportado`,`reportado`,`reportadopor`,`motivo`,`dataresolvido`,`resolvidopor`,`resolvido`,`resposta` FROM `reportes` WHERE( `id`='"+numero+"');");
					ResultSet rs = pst.executeQuery();
					boolean ne = true;
		            if(rs.next()) {
		            	String dataresolvido=rs.getString("dataresolvido");
		            	String resolvidopor=rs.getString("resolvidopor");
		            	String resposta=rs.getString("resposta");
		            	String resolvido="§cSEM INFORMAÇÃO";
		            	
		            	if(dataresolvido==null)
		            		dataresolvido="§cN/R";

		            	if(resolvidopor==null)
		            		resolvidopor="§cN/R";

		            	if(resposta==null)
		            		resposta="§cN/R";
		            	
		            	if(rs.getInt("resolvido")==0)
		            		resolvido="§cN/R";
		            	else if(rs.getInt("resolvido")==1)
		            		resolvido="§cNÃO";
		            	else if(rs.getInt("resolvido")==2)
		            		resolvido="§aSIM";

						if(sender==null) {
							plugin.pendente.remove(sender.getName().toLowerCase());
							rs.close();
							pst.close();
							con.close();
							break;
						}
		            	sender.sendMessage("§a§l[PDGHReportar]");
		            	sender.sendMessage("§9ID #§2"+rs.getInt("id"));
		            	sender.sendMessage("§9Servidor: "+rs.getString("servidor")+".");
		            	sender.sendMessage("§9Data: "+rs.getString("datareportado")+".");
		            	sender.sendMessage("§9Acusado: §c"+rs.getString("reportado")+"§9.");
		            	sender.sendMessage("§9Acusador: §a"+rs.getString("reportadopor")+"§9.");
		            	sender.sendMessage("§9Motivo: "+rs.getString("motivo"));
		            	sender.sendMessage("§a===========SOLUÇÃO===========");
		            	sender.sendMessage("§9Data: §a"+dataresolvido+"§9.");
		            	sender.sendMessage("§9STAFF que resolveu: §a"+resolvidopor+"§9.");
		            	sender.sendMessage("§9Acusador já foi avisado da solução? "+resolvido+"§9.");
		            	sender.sendMessage("§9Resposta da STAFF: §a"+resposta);
		            	sender.sendMessage("§a§l[PDGHReportar]");
		            	ne=false;
		            }
		            if(ne) {
						if(sender==null) {
							plugin.pendente.remove(sender.getName().toLowerCase());
							rs.close();
							pst.close();
							con.close();
							break;
						}
		            	sender.sendMessage("§4§l[PDGHReportar]§c ID "+numero+" não encontrado");
		            }

					plugin.pendente.remove(sender.getName().toLowerCase());
					rs.close();
					pst.close();
					con.close();
					break;
				}catch (SQLException ex) {
					System.out.print(ex);
					sender.sendMessage("§cErro! Contate um staffer!");
					break;
		        }
			}
			case "buscarmeusreportes": {
		        try {
		        	Connection con = DriverManager.getConnection(plugin.mysql_url,plugin.mysql_user,plugin.mysql_pass);
					//Prepared statement
					PreparedStatement pst = con.prepareStatement("SELECT `id`,`servidor`,`datareportado`,`reportado`,`reportadopor`,`motivo`,`dataresolvido`,`resolvidopor`,`resolvido`,`resposta` FROM `reportes` WHERE( `id`='"+numero+"' AND `reportadopor`='"+sender.getName().toLowerCase().trim()+"');");
					ResultSet rs = pst.executeQuery();
					boolean ne = true;
		            if(rs.next()) {
		            	String dataresolvido=rs.getString("dataresolvido");
		            	String resolvidopor=rs.getString("resolvidopor");
		            	String resposta=rs.getString("resposta");
		            	String resolvido="§cSEM INFORMAÇÃO";
		            	
		            	if(dataresolvido==null)
		            		dataresolvido="§cN/R";

		            	if(resolvidopor==null)
		            		resolvidopor="§cN/R";

		            	if(resposta==null)
		            		resposta="§cN/R";
		            	
		            	if(rs.getInt("resolvido")==0)
		            		resolvido="§cN/R";
		            	else if(rs.getInt("resolvido")==1)
		            		resolvido="§cNÃO";
		            	else if(rs.getInt("resolvido")==2)
		            		resolvido="§aSIM";

						if(sender==null) {
							plugin.pendente.remove(sender.getName().toLowerCase());
							rs.close();
							pst.close();
							con.close();
							break;
						}
		            	sender.sendMessage("§a§l[PDGHReportar]");
		            	sender.sendMessage("§9ID #§2"+rs.getInt("id"));
		            	sender.sendMessage("§9Servidor: "+rs.getString("servidor")+".");
		            	sender.sendMessage("§9Data: "+rs.getString("datareportado")+".");
		            	sender.sendMessage("§9Acusado: §c"+rs.getString("reportado")+"§9.");
		            	sender.sendMessage("§9Acusador: §a"+rs.getString("reportadopor")+"§9.");
		            	sender.sendMessage("§9Motivo: "+rs.getString("motivo"));
		            	sender.sendMessage("§a===========SOLUÇÃO===========");
		            	sender.sendMessage("§9Data: §a"+dataresolvido+"§9.");
		            	sender.sendMessage("§9STAFF que resolveu: §a"+resolvidopor+"§9.");
		            	sender.sendMessage("§9Acusador já foi avisado da solução? "+resolvido+"§9.");
		            	sender.sendMessage("§9Resposta da STAFF: §a"+resposta);
		            	sender.sendMessage("§a§l[PDGHReportar]");
		            	ne=false;
		            }
		            if(ne) {
						if(sender==null) {
							plugin.pendente.remove(sender.getName().toLowerCase());
							rs.close();
							pst.close();
							con.close();
							break;
						}
		            	sender.sendMessage("§4§l[PDGHReportar]§c ID "+numero+" não encontrado ou não foi você que reportou.");
		            }

					plugin.pendente.remove(sender.getName().toLowerCase());
					rs.close();
					pst.close();
					con.close();
					break;
				}catch (SQLException ex) {
					System.out.print(ex);
					sender.sendMessage("§cErro! Contate um staffer!");
					break;
		        }
			}
			case "resolver": {
				String data = calendario().trim();
		        try {
		        	Connection con = DriverManager.getConnection(plugin.mysql_url,plugin.mysql_user,plugin.mysql_pass);
					//Prepared statement
					PreparedStatement pst = con.prepareStatement("SELECT `id` FROM `reportes` WHERE( `id`='"+numero+"' AND `resolvido`=0);");
					ResultSet rs = pst.executeQuery();
					boolean ne = true;
		            if(rs.next()) {
		            	PreparedStatement pst2 = con.prepareStatement("UPDATE `reportes` SET `dataresolvido`='"+data+"',`resolvidopor`='"+p1_string+"',`resposta`='"+motivo+"',`resolvido`=1 WHERE( `id`='"+numero+"');");
						pst2.executeUpdate();
						pst2.close();
						if(sender==null) {
							plugin.pendente.remove(sender.getName().toLowerCase());
							rs.close();
							pst.close();
							con.close();
							break;
						}
						sender.sendMessage("§a§l[PDGHReportar]");
		            	sender.sendMessage("§aID "+numero+" resolvido com sucesso.");
		            	sender.sendMessage("§a===========SOLUÇÃO===========");
		            	sender.sendMessage("§9Data: §a"+data+"§9.");
		            	sender.sendMessage("§9STAFF que resolveu: §a"+p1_string+"§9.");
		            	sender.sendMessage("§9Resposta da STAFF: §a"+motivo+"§9.");
		            	sender.sendMessage("§a§l[PDGHReportar]");
		            	ne=false;
		            }
		            if(ne) {
						if(sender==null) {
							plugin.pendente.remove(sender.getName().toLowerCase());
							rs.close();
							pst.close();
							con.close();
							break;
						}
		            	sender.sendMessage("§4§l[PDGHReportar]§c ID "+numero+" não encontrado ou esse reporte já está resolvido.");
		            }

					plugin.pendente.remove(sender.getName().toLowerCase());
					rs.close();
					pst.close();
					con.close();
					break;
				}catch (SQLException ex) {
					System.out.print(ex);
					sender.sendMessage("§cErro! Contate um staffer!");
					break;
		        }
			}
			case "join": {
				try {
		        	Connection con = DriverManager.getConnection(plugin.mysql_url,plugin.mysql_user,plugin.mysql_pass);
					//Prepared statement
					PreparedStatement pst = con.prepareStatement("SELECT `id` FROM `reportes` WHERE( `reportadopor`='"+p1_string+"' AND `servidor`='"+plugin.servidor+"' AND `resolvido`=1);");
					ResultSet rs = pst.executeQuery();
		            while(rs.next()) {
		            	int id=rs.getInt("id");
						if(sender==null) {
							rs.close();
							pst.close();
							con.close();
							break;
						}
		            	PreparedStatement pst2 = con.prepareStatement("UPDATE `reportes` SET `resolvido`=2 WHERE( `id`='"+id+"');");
						pst2.executeUpdate();
						pst2.close();
						sender.sendMessage("§a§l[PDGHReportar]");
		            	sender.sendMessage("§aID "+id+" resolvido com sucesso.");
		            	sender.sendMessage("§aVeja mais detalhes com o comando:");
		            	sender.sendMessage("§a/reportar buscar "+id);
		            	sender.sendMessage("§a§l[PDGHReportar]");
		            }
					rs.close();
					pst.close();
					con.close();
					break;
				}catch (SQLException ex) {
					System.out.print(ex);
					sender.sendMessage("§cErro! Contate um staffer!");
					break;
		        }
			}
		}
	}
}
