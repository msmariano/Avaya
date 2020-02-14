package entity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import avaya.ClienteRestAvaya;
import avaya.ServidorRest;
import util.Log;

public class ClienteEventos implements Runnable {
	private Socket socketCliente;
	private String id;
	private List<ClienteEventos> listaClienteEventos;
	private Configuracao conf;
	private ClienteRestAvaya clienteRestAvaya;
	private Usuario usuarioEvento;
	private boolean ativado;
	private ServidorRest servidorRest;

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

	public void salvaConfig() {
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		String jsonConfig = gson.toJson(conf);
		if (servidorRest.getConfiguracaoGeral().getCaminhoDoExecutavel() != null) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(servidorRest.getConfiguracaoGeral().getCaminhoDoExecutavel()+"/config.json"));
			bw.write(jsonConfig);
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
	}
	
	public void carregaConfig() {
		try {
			BufferedReader br = null;
			conf = null;
			conf = new Configuracao();
			String confJson = "";
			if (servidorRest.getConfiguracaoGeral().getCaminhoDoExecutavel() != null) {
				br = new BufferedReader(new FileReader(servidorRest.getConfiguracaoGeral().getCaminhoDoExecutavel()+"/config.json"));
				while (br.ready()) {
					confJson = confJson + br.readLine();

				}
				br.close();
			}

			if (confJson.length() > 0) {
				try {
					Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
					conf = gson.fromJson(confJson, Configuracao.class);

				} catch (Exception e) {
					Log.grava(e.getMessage());
				}
			}
		} catch (Exception e) {

			Log.grava("Falhou ao abrir arquivo de configuracao.");
			// return;
		}
	}

	@Override
	public void run() {
		try {
			BufferedReader entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));

			while (true) {

				String mens = entrada.readLine();
				Log.grava("Mensagem ClienteEvento:" + mens);
				String parse[] = mens.split("=");
				if (parse != null && parse.length == 2) {
					if(parse[0].toLowerCase().equals("ramalcfg")) {
						String parseCfg[] = parse[1].split(";");
						carregaConfig();
						boolean isFind = false;
						for (Usuario usuario : conf.getListaUsuarios()) {
							if (usuario.getOrigTerminalName().equals(parseCfg[0])) {
								isFind = true;
								usuario.setNomeUsuario(parseCfg[1]);
								usuario.setOrigAddressName(parseCfg[2]);
								usuario.setSenhaUsuario(parseCfg[3]);
								break;
							}
						}
						
						if(!isFind) {
							Usuario usuario = new Usuario();
							usuario.setOrigTerminalName(parseCfg[0]);
							usuario.setNomeUsuario(parseCfg[1]);
							usuario.setOrigAddressName(parseCfg[2]);
							usuario.setSenhaUsuario(parseCfg[3]);
							conf.getListaUsuarios().add(usuario);
						}
						salvaConfig();
						retorno("ramal configuração;Ok");
						
					}
					else if (parse[0].toLowerCase().equals("ramal")) {

						carregaConfig();
						Log.grava("Setando socket para id " + parse[1] + " porta " + socketCliente.getPort());

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
									//e.printStackTrace();
									Log.grava(e.getMessage());
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
								//e.printStackTrace();
								Log.grava(e.getMessage());
								
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
							retorno("monitoracao;" + clienteRestAvaya.getSsotoken().getUser().getSsoTokenValue()
									+ ";ok");
							List<String> listaEntityNames = new ArrayList<>();
							listaEntityNames.add(usuarioEvento.getOrigTerminalName());

							clienteRestAvaya.assinarEventos(listaEntityNames);
						} else {
							retorno("monitoracao;nao foi possivel obter token;error");
							try {
								socketCliente.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
								Log.grava(e.getMessage());
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
							Log.grava("comando " + parse[0] + " recebido.");
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

							} else if (parse[0].equalsIgnoreCase("dropcall")) {
								ContactIdRest contactIdRest = new ContactIdRest();
								ContactId contactId = new ContactId();
								contactId.setContactId(parse[2]);
								contactIdRest.setContact(contactId);
								clienteRestAvaya.setContactId(contactIdRest);
								clienteRestAvaya.desligar();

							} else if (parse[0].equalsIgnoreCase("answercall")) {
								ContactIdRest contactIdRest = new ContactIdRest();
								ContactId contactId = new ContactId();
								contactId.setContactId(parse[2]);
								contactIdRest.setContact(contactId);
								clienteRestAvaya.setContactId(contactIdRest);
								clienteRestAvaya.atender();

							}
							// [monitorstart;device;TMonitorType[0=mtDevice|1=mtTrunk]]
							else if (parse[0].equalsIgnoreCase("monitorstart")) {
								Log.grava("Setando socket para id " + parse[1] + " porta " + socketCliente.getPort());

								for (ClienteEventos cli : listaClienteEventos) {
									if (cli.getId() != null
											&& cli.getId().toLowerCase().equals(parse[1].toLowerCase())) {
										BufferedWriter saida = new BufferedWriter(
												new OutputStreamWriter(socketCliente.getOutputStream()));
										saida.write("ramal ja esta sendo monitorado!\n");
										saida.flush();
										saida.close();
										try {
											socketCliente.close();
										} catch (IOException e) {
											// TODO Auto-generated catch block
											//e.printStackTrace();
											Log.grava(e.getMessage());
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
										//e.printStackTrace();
										Log.grava(e.getMessage());
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
									List<String> listaEntityNames = new ArrayList<>();
									listaEntityNames.add(usuarioEvento.getOrigTerminalName());
									retorno("monitoracao;" + clienteRestAvaya.getSsotoken().getUser().getSsoTokenValue()
											+ ";ok");
								} else {
									retorno("monitoracao;nao foi possivel obter token;error");
									try {
										socketCliente.close();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										//e.printStackTrace();
										Log.grava(e.getMessage());
									}
									listaClienteEventos.remove(this);
									return;

								}

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

	public ServidorRest getServidorRest() {
		return servidorRest;
	}

	public void setServidorRest(ServidorRest servidorRest) {
		this.servidorRest = servidorRest;
	}

}
