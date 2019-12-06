package avaya;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import entity.ClienteEventos;
import entity.Configuracao;
import entity.ConfiguracaoGeral;
import entity.Usuario;

public class ServidorRest {

	private static final String CONTEXT = "/Avaya";
	private HttpRequestHandler handler;
	private final List<ClienteEventos> listaClienteEventos;
	private static ServerSocket servidorMens;
	private Configuracao conf;
	private ConfiguracaoGeral configuracaoGeral;
	private boolean isConf;
	ClienteRestAvaya clienteRestAvaya;

	ServidorRest() {
		handler = new HttpRequestHandler();
		listaClienteEventos = new ArrayList<>();
	    clienteRestAvaya = new ClienteRestAvaya();
	}

	public static void main(String[] args) throws Exception {
		
		/*
		 * String entrada="%40 %3A";
		 * 
		 * for(int i=33;i<127;i++) { String hex = "%"+String.format("%X", i); String
		 * simbolo = String.format("%c", i); entrada = entrada.replace(hex,simbolo); }
		 * 
		 * System.out.println(entrada);
		 */

		ServidorRest servidorRest = new ServidorRest();
		BufferedReader br = null;
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("config")) {
				java.awt.EventQueue.invokeLater(new Runnable() {
					public void run() {
						new Config().setVisible(true);
					}
				});
				return;
			} else if (args[0].equalsIgnoreCase("teste")) {
				Runtime run = Runtime.getRuntime();
				// run.exec("mkdir /home/msmariano/Desktop/teste");
				return;
			} else if (args[0].equalsIgnoreCase("versao")) {
				System.out.println("v1.0.0");
				return;
			}

			else if (args[0].equalsIgnoreCase("install")) {
				System.err.println("Digite o numero da porta Http:");

				Scanner s = new Scanner(System.in);
				String portaHttpConfGeral = s.next();
				System.err.println("Digite o numero da porta de Mensagens:");
				String portaMens = s.next();
				// System.err.println("Digite o caminho do arquivo de configuracao:");
				System.err.println("Digite o ip do Servidor:");
				String ipServidor = s.next();
				String arqPathConf = "";
				ConfiguracaoGeral confGeral = new ConfiguracaoGeral();
				confGeral.setHttpPort(portaHttpConfGeral);
				confGeral.setMensPort(portaMens);
				confGeral.setPathConfiguracao(arqPathConf);
				confGeral.setIpServidor(ipServidor);
				Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
				String jSonRetorno = gson.toJson(confGeral);

				String arq = servidorRest.getClass().getResource("").toString();
				

				if (arq.contains("jar:")) {
					String path[] = null;
					path = arq.split("Avaya.jar!");
					arq = path[0];
				}

				arq = arq.replace("file:", "");
				arq = arq.replace("jar:", "");

				String arqAbs = arq;

				BufferedWriter bw = new BufferedWriter(new FileWriter(arq + "configGeral.json"));
				bw.write(jSonRetorno);
				bw.close();

				try {
					URL url = servidorRest.getClass().getResource("/html/serverest");
					if(url!=null)
						arq = servidorRest.getClass().getResource("/html/serverest").toString();
					else
					{
						System.err.println("não obteve recurso serverest");
						return;
					}
				}catch (Exception e) {
					System.err.println(e.getMessage());
					return;
				}

				String serverest = "";

				System.out.println("Buscando arquivo serverest");
				if (arq.contains("jar:")) {

					try {
						System.out.println("Recuperando recurso no JAR");
						InputStream in = servidorRest.getClass().getResourceAsStream("/html/serverest");
						BufferedReader reader = new BufferedReader(new InputStreamReader(in));
						while (reader.ready()) {
							serverest = serverest + reader.readLine() + "\n";

						}
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}

				} else {
					// System.out.println("Recuperando recurso");
					arq = arq.replace("file:", "");
					arq = arq.replace("jar:", "");
					BufferedReader brConf = new BufferedReader(new FileReader(arq));
					while (brConf.ready()) {
						serverest = serverest + brConf.readLine() + "\n";

					}
					brConf.close();
				}

				serverest = serverest.replace("TagPath", arqAbs);

				System.out.println("Salvando arquivo de servico");
				try {
					bw = new BufferedWriter(new FileWriter("/etc/init.d/serverest"));
					bw.write(serverest);
					bw.close();
				} catch (Exception e) {

				}

				System.out.println("Instalando servico");
				Runtime run = Runtime.getRuntime();
				run.exec("chmod 777 /etc/init.d/serverest");
				run.exec("update-rc.d serverest defaults");
				run.exec("update-rc.d serverest start 90 2 3 4 5");
				run.exec("/etc/init.d/serverest remove");
				System.out.println("Fim");
				return;

			}
		}
		String configGeralJson = "";
		try {

			String arq = servidorRest.getClass().getResource("").toString();

			if (arq.contains("jar:")) {
				String path[] = null;
				path = arq.split("Avaya.jar!");
				arq = path[0];
			}

			arq = arq.replace("file:", "");
			arq = arq.replace("jar:", "");

			System.out.println(arq);

			br = new BufferedReader(new FileReader(arq + "configGeral.json"));
		} catch (Exception e) {
			System.err.println(
					"Falhou ao abrir arquivo de configuracao Geral. Execute java -jar Avaya.jar install e realize as configuracoes gerais.");
			return;
		}

		while (br.ready()) {
			configGeralJson = configGeralJson + br.readLine();

		}

		if (configGeralJson.length() > 0) {
			try {
				Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
				servidorRest.configuracaoGeral = gson.fromJson(configGeralJson, ConfiguracaoGeral.class);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		br.close();

		servidorMens = new ServerSocket(Integer.parseInt(servidorRest.configuracaoGeral.getMensPort()));
		servidorRest.clienteRestAvaya.setIpServidor(servidorRest.configuracaoGeral.getIpServidor());
		Servidor servidor = new Servidor(Integer.parseInt(servidorRest.configuracaoGeral.getHttpPort()), CONTEXT,
				servidorRest.getHandler());
		servidorRest.getHandler().setListaClienteEventos(servidorRest.listaClienteEventos);
		String confJson = "";

		try {
			br = new BufferedReader(new FileReader("config.json"));
			while (br.ready()) {
				confJson = confJson + br.readLine();

			}
			br.close();

			if (confJson.length() > 0) {
				try {
					Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
					servidorRest.conf = gson.fromJson(confJson, Configuracao.class);
					servidorRest.isConf = true;
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		} catch (Exception e) {
			servidorRest.isConf = false;
			System.err.println("Falhou ao abrir arquivo de configuracao.");
			// return;
		}

		//if (servidorRest.isConf) {
			servidorRest.getHandler().setConf(servidorRest.conf);

			if (servidorRest.conf != null && servidorRest.conf.getListaUsuarios() != null) {
				//List<String> ramais = new ArrayList<>();
				//for (Usuario usuario : servidorRest.conf.getListaUsuarios()) {
					/*
					 * ramais.add(usuario.getOrigTerminalName()); } if (ramais.size() > 0) {
					 */
					
					servidorRest.clienteRestAvaya.setPortaEvento(String.valueOf(servidorRest.configuracaoGeral.getHttpPort()));
					servidorRest.clienteRestAvaya.setServidorEnd(servidorRest.conf.getNomeServidorAvaya());
					servidorRest.clienteRestAvaya.setServidorPorta(servidorRest.conf.getPortaServidorAvaya());
					servidorRest.clienteRestAvaya.setDomain(servidorRest.conf.getDominio());
					servidorRest.clienteRestAvaya.setUsername(servidorRest.conf.getUsuarioCCT());
					servidorRest.clienteRestAvaya.setPassword(servidorRest.conf.getSenhaCCT());
					/*
					 * if (clienteRestAvaya.obterToken()) clienteRestAvaya.assinarEventos(ramais);
					 * else { System.err.println("Nao foi possivel iniciar eventos"); }
					 */
				//}
			}
		//}

		servidor.start();
		System.out.println("Servidor Http iniciado na porta " + servidorRest.configuracaoGeral.getHttpPort());

		new Thread() {

			@Override
			public void run() {
				System.out.println(
						"Servidor de Mensagens iniciado na porta " + servidorRest.configuracaoGeral.getMensPort());
				while (true) {
					try {
						Socket cliente;
						cliente = servidorMens.accept();
						System.out.println("Cliente conectado: " + cliente.getInetAddress().getHostAddress() + " porta "
								+ cliente.getPort());
						new Thread() {
							@Override
							public void run() {

								ClienteEventos clienteEventos = new ClienteEventos();
								clienteEventos.setListaClienteEventos(servidorRest.listaClienteEventos);
								clienteEventos.setSocketCliente(cliente);
								servidorRest.listaClienteEventos.add(clienteEventos);
								clienteEventos.setConf(servidorRest.conf);
								clienteEventos.setClienteRestAvaya(servidorRest.clienteRestAvaya);
								clienteEventos.run();
							}

						}.start();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}.start();

	}

	public HttpRequestHandler getHandler() {
		return handler;
	}

	public void setHandler(HttpRequestHandler handler) {
		this.handler = handler;
	}

	public ConfiguracaoGeral getConfiguracaoGeral() {
		return configuracaoGeral;
	}

	public void setConfiguracaoGeral(ConfiguracaoGeral configuracaoGeral) {
		this.configuracaoGeral = configuracaoGeral;
	}

}
