package avaya;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import entity.ClienteEventos;

public class ServidorRest {

	private static final String CONTEXT = "/Avaya";
	private static final int PORT = 8000;
	private static final int PORTMENS = 8001;
	private HttpRequestHandler handler;
	private final List<ClienteEventos> listaClienteEventos;
	private static ServerSocket servidorMens;

	ServidorRest() {
		handler = new HttpRequestHandler();
		listaClienteEventos = new ArrayList<>();
	}

	public static void main(String[] args) throws Exception {

		servidorMens = new ServerSocket(PORTMENS);
		ServidorRest servidorRest = new ServidorRest();
		Servidor servidor = new Servidor(PORT, CONTEXT, servidorRest.getHandler());
		servidorRest.getHandler().setListaClienteEventos(servidorRest.listaClienteEventos);
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
						System.out.println("Cliente conectado: " + cliente.getInetAddress().getHostAddress()+" porta "+cliente.getPort());
						new Thread() {
							@Override
							public void run() {

								ClienteEventos clienteEventos = new ClienteEventos();
								clienteEventos.setListaClienteEventos(servidorRest.listaClienteEventos);
								clienteEventos.setSocketCliente(cliente);
								servidorRest.listaClienteEventos.add(clienteEventos);
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
