package entity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

public class ClienteEventos implements Runnable {
	private Socket socketCliente;
	private String id;
	private List<ClienteEventos> listaClienteEventos;

	public Socket getSocketCliente() {
		return socketCliente;
	}

	public void setSocketCliente(Socket socketCliente) {
		this.socketCliente = socketCliente;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void send(String mensagem) throws IOException {
		BufferedWriter saida = new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream()));
		saida.write(mensagem);
		saida.flush();
	}

	@Override
	public void run() {
		try {
			BufferedReader entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));

			while (true) {

				String mens = entrada.readLine();
				String parse[] = mens.split("=");
				if (parse != null && parse.length == 2) {
					if (parse[0].toLowerCase().equals("ramal")) {
						System.out.println("Setando socket para id " + parse[1] + " porta " + socketCliente.getPort());

						for (ClienteEventos cli : listaClienteEventos) {
							if (cli.getId()!=null&&cli.getId().toLowerCase().equals(parse[1].toLowerCase())) {
								BufferedWriter saida = new BufferedWriter(
										new OutputStreamWriter(socketCliente.getOutputStream()));
								saida.write("ramal já esta sendo monitorado!\n");
								saida.flush();
								saida.close();
								try {
									socketCliente.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								listaClienteEventos.remove(this);
								return;
							}
						}

						this.id = parse[1];
					}
				} else {
					BufferedWriter saida = new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream()));
					saida.write("comando inválido.Desconectando!\n");
					saida.flush();
					saida.close();
					try {
						socketCliente.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					listaClienteEventos.remove(this);
					return;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			socketCliente.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		listaClienteEventos.remove(this);

	}

	public List<ClienteEventos> getListaClienteEventos() {
		return listaClienteEventos;
	}

	public void setListaClienteEventos(List<ClienteEventos> listaClienteEventos) {
		this.listaClienteEventos = listaClienteEventos;
	}

}
