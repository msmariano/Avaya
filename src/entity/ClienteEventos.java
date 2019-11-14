package entity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

import avaya.ClienteRestAvaya;

public class ClienteEventos implements Runnable {
	private Socket socketCliente;
	private String id;
	private List<ClienteEventos> listaClienteEventos;
	private Configuracao conf;
	private ClienteRestAvaya clienteRestAvaya;
	private Usuario usuarioEvento;
	private boolean ativado;

	public ClienteEventos() {
		clienteRestAvaya = new ClienteRestAvaya();
		ativado = false;
	}

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

	public void retorno(String mensagem) throws IOException {
		BufferedWriter saida = new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream()));
		saida.write(mensagem + "\n");
		saida.flush();

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
							if (cli.getId() != null && cli.getId().toLowerCase().equals(parse[1].toLowerCase())) {
								BufferedWriter saida = new BufferedWriter(
										new OutputStreamWriter(socketCliente.getOutputStream()));
								saida.write("ramal ja esta sendo monitorado!\n");
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

						boolean isFind = false;
						for (Usuario usuario : conf.getListaUsuarios()) {
							if (usuario.getOrigTerminalName().equals(parse[1])) {
								usuarioEvento = usuario;
								isFind = true;
								break;
							}
						}

						if (!isFind) {
							retorno("monitoracao;ramal nao esta na lista de configuracao;error");
							try {
								socketCliente.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							listaClienteEventos.remove(this);
							return;

						}

						clienteRestAvaya.setDomain(conf.getDominio());
						clienteRestAvaya.setServidorEnd(conf.getNomeServidorAvaya());
						clienteRestAvaya.setServidorPorta(conf.getPortaServidorAvaya());
						clienteRestAvaya.setUsername(usuarioEvento.getNomeUsuario());
						clienteRestAvaya.setPassword(usuarioEvento.getSenhaUsuario());
						clienteRestAvaya.setTerminalName(usuarioEvento.getOrigTerminalName());
						clienteRestAvaya.setAdressName(usuarioEvento.getOrigAddressName());
						if (clienteRestAvaya.obterToken()) {
							ativado = true;
							this.id = parse[1];
							retorno("monitoracao;"+clienteRestAvaya.getSsotoken().getUser().getSsoTokenValue()+";ok");
						} else {
							retorno("monitoracao;nao foi possivel obter token;error");
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
				} else {

					if (mens.length() > 2) {
						if (mens.substring(0, 1).equals("[") && mens.substring(mens.length() - 1).equals("]")) {
							mens = mens.replace("[", "");
							mens = mens.replace("]", "");
							parse = mens.split(";");
							System.out.println("comando " + parse[0] + " recebido.");
							if (parse[0].equalsIgnoreCase("makecall")) {
								if (ativado) {

									clienteRestAvaya.setOrg(parse[1]);
									clienteRestAvaya.setDst(parse[2]);
									if (clienteRestAvaya.discar()) {
										retorno("MakeCallResult;Ok;"
												+ clienteRestAvaya.getContactId().getContact().getContactId());
									} else
										retorno("MakeCallResult;Error");

								} else {
									retorno("makecall;ramal sem token;error");
								}

							} else if (parse[0].equalsIgnoreCase("clearconnection")) {
								ContactIdRest contactIdRest = new ContactIdRest();
								ContactId contactId  = new ContactId();
								contactId.setContactId(parse[2]);
								contactIdRest.setContact(contactId);
								clienteRestAvaya.desligar();

							} else if (parse[0].equalsIgnoreCase("answercall")) {
								ContactIdRest contactIdRest = new ContactIdRest();
								ContactId contactId  = new ContactId();
								contactId.setContactId(parse[2]);
								contactIdRest.setContact(contactId);
								clienteRestAvaya.atender();

							}

							continue;
						}
					}

					BufferedWriter saida = new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream()));
					saida.write("comando invalido.Desconectando!\n");
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

	public Configuracao getConf() {
		return conf;
	}

	public void setConf(Configuracao conf) {
		this.conf = conf;
	}

	public ClienteRestAvaya getClienteRestAvaya() {
		return clienteRestAvaya;
	}

	public void setClienteRestAvaya(ClienteRestAvaya clienteRestAvaya) {
		this.clienteRestAvaya = clienteRestAvaya;
	}

	public Usuario getUsuarioEvento() {
		return usuarioEvento;
	}

	public void setUsuarioEvento(Usuario usuarioEvento) {
		this.usuarioEvento = usuarioEvento;
	}

	public boolean getAtivado() {
		return ativado;
	}

	public void setAtivado(boolean ativado) {
		this.ativado = ativado;
	}

}
