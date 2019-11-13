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
import entity.Usuario;

public class ServidorRest {

	private static final String CONTEXT = "/Avaya";
	private static final int PORT = 8000;
	private static final int PORTMENS = 8001;
	private HttpRequestHandler handler;
	private final List<ClienteEventos> listaClienteEventos;
	private static ServerSocket servidorMens;
	private Configuracao conf;

	ServidorRest() {
		handler = new HttpRequestHandler();
		listaClienteEventos = new ArrayList<>();
	}

	public static void main(String[] args) throws Exception {

		servidorMens = new ServerSocket(PORTMENS);
		ServidorRest servidorRest = new ServidorRest();
		Servidor servidor = new Servidor(PORT, CONTEXT, servidorRest.getHandler());
		servidorRest.getHandler().setListaClienteEventos(servidorRest.listaClienteEventos);
		String confJson = "";
		BufferedReader br = new BufferedReader(new FileReader("/home/msmariano/Desktop/conf.json"));

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
				clienteRestAvaya.setPortaEvento(servidorRest.conf.getPortaEventos());
				clienteRestAvaya.setServidorEnd(servidorRest.conf.getNomeServidorAvaya());
				clienteRestAvaya.setServidorPorta(servidorRest.conf.getNomeServidorAvaya());
				clienteRestAvaya.setUsername(servidorRest.conf.getUsuarioCCT());
				clienteRestAvaya.setPassword(servidorRest.conf.getSenhaCCT());
				if(clienteRestAvaya.obterToken())
					clienteRestAvaya.assinarEventos(ramais);
				else {
					System.err.println("Não foi possível iniciar eventos");
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

}
