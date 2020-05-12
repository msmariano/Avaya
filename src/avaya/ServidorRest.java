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
import util.Log;

public class ServidorRest {

	private static final String CONTEXT = "/Avaya";
	private HttpRequestHandler handler;
	private final List<ClienteEventos> listaClienteEventos;
	private static ServerSocket servidorMens;
	private Configuracao conf;
	private ConfiguracaoGeral configuracaoGeral;
	// private boolean isConf;
	ClienteRestAvaya clienteRestAvaya;
	private static Scanner s;

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
		 * Log.grava(entrada);
		 */

		Log.grava("carregando ServidorRest");
		ServidorRest servidorRest = new ServidorRest();
		servidorRest.getHandler().setServidorRest(servidorRest);
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
				// Runtime run = Runtime.getRuntime();
				// run.exec("mkdir /home/msmariano/Desktop/teste");
				return;
			} else if (args[0].equalsIgnoreCase("versao")) {
				Log.grava("v1.0.12");
				return;
			}

			else if (args[0].equalsIgnoreCase("install")) {
				Log.grava("Digite o numero da porta Http:");

				s = new Scanner(System.in);
				String portaHttpConfGeral = s.next();
				Log.grava("Digite o numero da porta de Mensagens:");
				String portaMens = s.next();
				Log.grava("Digite o ip do Servidor:");
				String ipServidor = s.next();
				String arqPathConf = "";
				ConfiguracaoGeral confGeral = new ConfiguracaoGeral();
				confGeral.setHttpPort(portaHttpConfGeral);
				confGeral.setMensPort(portaMens);
				confGeral.setPathConfiguracao(arqPathConf);
				confGeral.setIpServidor(ipServidor);

				String arq = servidorRest.getClass().getResource("").toString();

				if (arq.contains("jar:")) {
					String path[] = null;
					path = arq.split("Avaya.jar!");
					arq = path[0];
				}

				arq = arq.replace("file:", "");
				arq = arq.replace("jar:", "");

				String arqAbs = arq;
				confGeral.setCaminhoDoExecutavel(arqAbs);
				Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
				String jSonRetorno = gson.toJson(confGeral);

				BufferedWriter bw = new BufferedWriter(new FileWriter(arq + "configGeral.json"));
				bw.write(jSonRetorno);
				bw.close();

				try {
					URL url = servidorRest.getClass().getResource("/html/serverest");
					if (url != null)
						arq = servidorRest.getClass().getResource("/html/serverest").toString();
					else {
						Log.grava("nao obteve recurso serverest");
						return;
					}
				} catch (Exception e) {
					Log.grava(e.getMessage());
					return;
				}

				String serverest = "";

				Log.grava("Buscando arquivo serverest");
				if (arq.contains("jar:")) {

					try {
						Log.grava("Recuperando recurso no JAR");
						InputStream in = servidorRest.getClass().getResourceAsStream("/html/serverest");
						BufferedReader reader = new BufferedReader(new InputStreamReader(in));
						while (reader.ready()) {
							serverest = serverest + reader.readLine() + "\n";

						}
					} catch (Exception e) {
						Log.grava(e.getMessage());
					}

				} else {
					// Log.grava("Recuperando recurso");
					arq = arq.replace("file:", "");
					arq = arq.replace("jar:", "");
					BufferedReader brConf = new BufferedReader(new FileReader(arq));
					while (brConf.ready()) {
						serverest = serverest + brConf.readLine() + "\n";

					}
					brConf.close();
				}

				serverest = serverest.replace("TagPath", arqAbs);

				Log.grava("Salvando arquivo de servico");
				try {
					bw = new BufferedWriter(new FileWriter("/etc/init.d/serverest"));
					bw.write(serverest);
					bw.close();
				} catch (Exception e) {

				}

				Log.grava("Instalando servico");
				Runtime run = Runtime.getRuntime();
				run.exec("chmod 777 /etc/init.d/serverest");
				run.exec("update-rc.d serverest defaults");
				run.exec("update-rc.d serverest start 90 2 3 4 5");
				run.exec("/etc/init.d/serverest remove");
				Log.grava("Fim");
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

			Log.grava(arq);

			br = new BufferedReader(new FileReader(arq + "configGeral.json"));
		} catch (Exception e) {
			Log.grava(
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
				Log.grava(e.getMessage());
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
			if (servidorRest.getConfiguracaoGeral().getCaminhoDoExecutavel() != null) {
				br = new BufferedReader(new FileReader(servidorRest.getConfiguracaoGeral().getCaminhoDoExecutavel()+"/config.json"));
				while (br.ready()) {
					confJson = confJson + br.readLine();

				}
				br.close();

				if (confJson.length() > 0) {
					try {
						Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
						servidorRest.conf = gson.fromJson(confJson, Configuracao.class);
						// servidorRest.isConf = true;
					} catch (Exception e) {
						Log.grava(e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			// servidorRest.isConf = false;
			Log.grava("Falhou ao abrir arquivo de configuracao.");
			// return;
		}

		// if (servidorRest.isConf) {
		servidorRest.getHandler().setConf(servidorRest.conf);

		if (servidorRest.conf != null && servidorRest.conf.getListaUsuarios() != null) {
			// List<String> ramais = new ArrayList<>();
			// for (Usuario usuario : servidorRest.conf.getListaUsuarios()) {
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
			 * else { Log.grava("Nao foi possivel iniciar eventos"); }
			 */
			// }
		}
		// }

		servidor.start();
		Log.grava("Servidor Http iniciado na porta " + servidorRest.configuracaoGeral.getHttpPort());

		new Thread() {

			@Override
			public void run() {
				Log.grava("Servidor de Mensagens iniciado na porta " + servidorRest.configuracaoGeral.getMensPort());
				while (true) {
					try {
						Socket cliente;
						cliente = servidorMens.accept();
						Log.grava("Cliente conectado: " + cliente.getInetAddress().getHostAddress() + " porta "
								+ cliente.getPort());
						new Thread() {
							@Override
							public void run() {

								ClienteEventos clienteEventos = new ClienteEventos();
								clienteEventos.setListaClienteEventos(servidorRest.listaClienteEventos);
								clienteEventos.setSocketCliente(cliente);
								servidorRest.listaClienteEventos.add(clienteEventos);
								clienteEventos.setConf(servidorRest.conf);
								servidorRest.configuraClienteRestAvaya(clienteEventos.getClienteRestAvaya());
								
								//clienteEventos.setClienteRestAvaya(servidorRest.clienteRestAvaya);
								clienteEventos.setServidorRest(servidorRest);
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
	
	public ClienteRestAvaya configuraClienteRestAvaya (ClienteRestAvaya clienteRestAvayaLocal){
		clienteRestAvayaLocal.setIpServidor(configuracaoGeral.getIpServidor());
		clienteRestAvayaLocal.setPortaEvento(String.valueOf(configuracaoGeral.getHttpPort()));
		clienteRestAvayaLocal.setServidorEnd(conf.getNomeServidorAvaya());
		clienteRestAvayaLocal.setServidorPorta(conf.getPortaServidorAvaya());
		clienteRestAvayaLocal.setDomain(conf.getDominio());
		clienteRestAvayaLocal.setUsername(conf.getUsuarioCCT());
		clienteRestAvayaLocal.setPassword(conf.getSenhaCCT());
		
		return clienteRestAvayaLocal;
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
