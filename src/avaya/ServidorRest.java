package avaya;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import entity.ClienteEventos;
import entity.Configuracao;
import entity.ConfiguracaoGeral;
import entity.Usuario;

public class ServidorRest {

	private static final String CONTEXT = "/Avaya";
	private static final int PORT = 8000;
	private static final int PORTMENS = 8001;
	private HttpRequestHandler handler;
	private final List<ClienteEventos> listaClienteEventos;
	private static ServerSocket servidorMens;
	private Configuracao conf;
	private ConfiguracaoGeral configuracaoGeral;

	ServidorRest() {
		handler = new HttpRequestHandler();
		listaClienteEventos = new ArrayList<>();
	}

	public static void main(String[] args) throws Exception {
		
		ServidorRest servidorRest = new ServidorRest();
		BufferedReader br = null;
		if(args.length >0) {
			if(args[0].equalsIgnoreCase("config")) {
				Config config = new Config();
				config.setVisible(true);
				return;
			}
		}
		String configGeralJson="";
		try {
			br = new BufferedReader(new FileReader("configGeral.json"));
		}catch (Exception e) {
			System.err.println("Falhou ao abrir arquivo de configuração Geral. Execute java -jar Avaya.jar config e realize as configuções gerais.");
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
		

		servidorMens = new ServerSocket(PORTMENS);
		Servidor servidor = new Servidor(PORT, CONTEXT, servidorRest.getHandler());
		servidorRest.getHandler().setListaClienteEventos(servidorRest.listaClienteEventos);
		String confJson = "";
		
		try {
			br = new BufferedReader(new FileReader(servidorRest.configuracaoGeral.getPathConfiguracao()));
		}catch (Exception e) {
			System.err.println("Falhou ao abrir arquivo de configuração.");
			return;
		}

		while (br.ready()) {
			confJson = confJson + br.readLine();

		}
		br.close();

		if (confJson.length() > 0) {
			try {
				Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
				servidorRest.conf = gson.fromJson(confJson, Configuracao.class);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		servidorRest.getHandler().setConf(servidorRest.conf);

		if (servidorRest.conf != null && servidorRest.conf.getListaUsuarios() != null) {
			List<String> ramais = new ArrayList<>();
			for (Usuario usuario : servidorRest.conf.getListaUsuarios()) {
				ramais.add(usuario.getOrigTerminalName());
			}
			if (ramais.size() > 0) {
				ClienteRestAvaya clienteRestAvaya = new ClienteRestAvaya();
				clienteRestAvaya.setPortaEvento(String.valueOf(PORT));
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

		servidor.start();
		System.out.println("Servidor Http iniciado na porta " + PORT);

		new Thread() {

			@Override
			public void run() {
				System.out.println("Servidor de Mensagens iniciado na porta " + PORTMENS);
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
