package avaya;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.ServerSocket;
import java.net.Socket;
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

	ServidorRest() {
		handler = new HttpRequestHandler();
		listaClienteEventos = new ArrayList<>();
	}

	public static void main(String[] args) throws Exception {
		
		ServidorRest servidorRest = new ServidorRest();
		BufferedReader br = null;
		if(args.length >0) {
			if(args[0].equalsIgnoreCase("config")) {
				java.awt.EventQueue.invokeLater(new Runnable() {
		            public void run() {
		                new Config().setVisible(true);
		            }
		        });
				return;
			}
			else if(args[0].equalsIgnoreCase("install")) {
				System.err.println("Digite o numero da porta Http:");
				
				Scanner s = new Scanner(System.in);
			    String portaHttpConfGeral = s.next();
			    System.err.println("Digite o numero da porta de Mensagens:");
			    String portaMens= s.next();
			    //System.err.println("Digite o caminho do arquivo de configuracao:");
			    String arqPathConf = "";
			    ConfiguracaoGeral confGeral = new ConfiguracaoGeral();
			    confGeral.setHttpPort(portaHttpConfGeral);
			    confGeral.setMensPort(portaMens);
			    confGeral.setPathConfiguracao(arqPathConf);
				Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
				String jSonRetorno = gson.toJson(confGeral);
				BufferedWriter bw = new BufferedWriter(new FileWriter("configGeral.json"));
				bw.write(jSonRetorno);
				bw.close();
			    return;
				
			}
		}
		String configGeralJson="";
		try {
			br = new BufferedReader(new FileReader("configGeral.json"));
		}catch (Exception e) {
			System.err.println("Falhou ao abrir arquivo de configuracao Geral. Execute java -jar Avaya.jar install e realize as configuracoes gerais.");
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
		Servidor servidor = new Servidor(Integer.parseInt(servidorRest.configuracaoGeral.getHttpPort()), CONTEXT, servidorRest.getHandler());
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
		}catch (Exception e) {
			servidorRest.isConf = false;
			System.err.println("Falhou ao abrir arquivo de configuracao.");
			//return;
		}

		

		if(servidorRest.isConf) {
			servidorRest.getHandler().setConf(servidorRest.conf);
	
			if (servidorRest.conf != null && servidorRest.conf.getListaUsuarios() != null) {
				List<String> ramais = new ArrayList<>();
				for (Usuario usuario : servidorRest.conf.getListaUsuarios()) {
					ramais.add(usuario.getOrigTerminalName());
				}
				if (ramais.size() > 0) {
					ClienteRestAvaya clienteRestAvaya = new ClienteRestAvaya();
					clienteRestAvaya.setPortaEvento(String.valueOf(servidorRest.configuracaoGeral.getHttpPort()));
					clienteRestAvaya.setServidorEnd(servidorRest.conf.getNomeServidorAvaya());
					clienteRestAvaya.setServidorPorta(servidorRest.conf.getPortaServidorAvaya());
					clienteRestAvaya.setDomain(servidorRest.conf.getDominio());
					clienteRestAvaya.setUsername(servidorRest.conf.getUsuarioCCT());
					clienteRestAvaya.setPassword(servidorRest.conf.getSenhaCCT());
					if(clienteRestAvaya.obterToken())
						clienteRestAvaya.assinarEventos(ramais);
					else {
						System.err.println("Nao foi possivel iniciar eventos");
					}
				}
			}
		}

		servidor.start();
		System.out.println("Servidor Http iniciado na porta " + servidorRest.configuracaoGeral.getHttpPort());

		new Thread() {

			@Override
			public void run() {
				System.out.println("Servidor de Mensagens iniciado na porta " + servidorRest.configuracaoGeral.getMensPort());
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
