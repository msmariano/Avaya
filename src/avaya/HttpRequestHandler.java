package avaya;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import entity.ClienteEventos;
import entity.Configuracao;
import entity.EndPoint;
import entity.Usuario;
import util.FiltroWeb;

@SuppressWarnings("restriction")
public class HttpRequestHandler implements HttpHandler {

	private static final int HTTP_OK_STATUS = 200;

	private static final String AND_DELIMITER = "&";
	private static final String EQUAL_DELIMITER = "=";

	private List<ClienteRestAvaya> listaRamal;

	private List<ClienteEventos> listaClienteEventos;

	private Configuracao conf;

	private FiltroWeb filtro;

	public List<ClienteRestAvaya> getListaRamal() {
		return listaRamal;
	}

	public void setListaRamal(List<ClienteRestAvaya> listaRamal) {
		this.listaRamal = listaRamal;
	}

	private EndPoint endPoint;

	HttpRequestHandler() {
		listaRamal = new ArrayList<>();
		conf = new Configuracao();
		filtro = new FiltroWeb();

	}

	public void handle(HttpExchange t) throws IOException {

		BufferedReader br = null;
		String inputRequest = null;
		StringBuilder requestContent = new StringBuilder();

		if (t.getRequestBody() != null) {
			br = new BufferedReader(new InputStreamReader(t.getRequestBody()));
			if (br != null) {
				while ((inputRequest = br.readLine()) != null) {
					//System.out.println(inputRequest);
					requestContent.append(inputRequest);
				}

			}
		}

		URI uri = t.getRequestURI();

		if (uri.getPath().equals("/Avaya/rest/ramal/config")) {

			Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
			List<Usuario> listaUsuarios = null;

			String htmlConf = "";
			String confServidor = "";
			String endServidorAvaya = "";
			String portaServidorAvaya = "";
			String dominio = "";
			String jsonConfig = "";
			String nomeUsuario = "";
			String senhaUsuario = "";
			String origTerminalName = "";
			String origAddressName = "";
			String confRamal = "";
			String usuarioTableHtml = "";
			String terminalExcluir = "";
			String excluirRamal = "";
			String tagAlert = "";
			String editarRamal = "";
			String terminalEditar= "";
			String tagNomeUsuario="";
			String tagSenhaUsuario="";
			String tagOrigTerminalName="";
			String tagOrigAddressName="";

			try {
				BufferedReader rd = new BufferedReader(new FileReader("config.json"));
				while (rd.ready()) {
					jsonConfig = jsonConfig + rd.readLine();
				}
				rd.close();
				gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
				if (jsonConfig.length() > 0) {
					conf = gson.fromJson(jsonConfig, Configuracao.class);
				} else
					conf = new Configuracao();
			} catch (Exception e) {
				// TODO: handle exception
			}
			if (conf.getListaUsuarios() == null) {
				listaUsuarios = new ArrayList<>();
				conf.setListaUsuarios(listaUsuarios);
			}

			if (requestContent.toString().trim().length() > 0) {

				String entrada = requestContent.toString();
				entrada = entrada.replace("%40","@");
				entrada = entrada.replace("%3A",":");
				
				
				String variavelValor[] = entrada.split("&");

				for (String campo : variavelValor) {
					String conf[] = campo.split("=");
					if (conf != null && conf.length > 1) {
						if (conf[0].equals("endServidorAvaya")) {
							endServidorAvaya = conf[1];
						} else if (conf[0].equals("confServidor")) {
							confServidor = conf[1];
						} else if (conf[0].equals("portaServidorAvaya")) {
							portaServidorAvaya = conf[1];
						} else if (conf[0].equals("dominio")) {
							dominio = conf[1];
						} else if (conf[0].equals("nomeUsuario")) {
							nomeUsuario = conf[1];
						} else if (conf[0].equals("senhaUsuario")) {
							senhaUsuario = conf[1];
						} else if (conf[0].equals("origTerminalName")) {
							origTerminalName = conf[1];
						} else if (conf[0].equals("origAddressName")) {
							origAddressName = conf[1];
						} else if (conf[0].equals("confRamal")) {
							confRamal = conf[1];
						} else if (conf[0].equals("terminalExcluir")) {
							terminalExcluir = conf[1];
						} else if (conf[0].equals("excluirRamal")) {
							excluirRamal = conf[1];
						}
						else if (conf[0].equals("editarRamal")) {
							editarRamal = conf[1];
						} else if (conf[0].equals("terminalEditar")) {
							terminalEditar = conf[1];
						}
					}

				}

			}

			if (confServidor.equals("true")) {
				conf.setNomeServidorAvaya(endServidorAvaya);
				conf.setPortaServidorAvaya(portaServidorAvaya);
				conf.setDominio(dominio);
			} else if (confRamal.equals("true")) {
				boolean isFind = false;
				for (Usuario usuario : conf.getListaUsuarios()) {
					if (usuario.getOrigTerminalName().equals(origTerminalName)) {
						isFind = true;
						usuario.setSenhaUsuario(senhaUsuario);
						usuario.setOrigAddressName(origAddressName);
						usuario.setOrigTerminalName(origTerminalName);
						usuario.setNomeUsuario(nomeUsuario);
						break;
					}
				}
				if (!isFind) {
					if (senhaUsuario.trim().length() == 0) {
						tagAlert = "alert('Senha deve ser preenchida')";
					} else if (origAddressName.trim().length() == 0) {
						tagAlert = "alert('Nome do Endereço de origem deve ser preenchido')";
					} else if (origTerminalName.trim().length() == 0) {
						tagAlert = "alert('Nome do Terminal de Origem')";
					} else if (nomeUsuario.trim().length() == 0) {
						tagAlert = "alert('Nome do Usuário')";
					} else {
						Usuario usuario = new Usuario();
						usuario.setSenhaUsuario(senhaUsuario);
						usuario.setOrigAddressName(origAddressName);
						usuario.setOrigTerminalName(origTerminalName);
						usuario.setNomeUsuario(nomeUsuario);
						conf.getListaUsuarios().add(usuario);
					}
				}
			} else if (excluirRamal.equals("true")) {
				for (Usuario usuario : conf.getListaUsuarios()) {
					if (usuario.getOrigTerminalName().equals(terminalExcluir)) {
						conf.getListaUsuarios().remove(usuario);
						break;
					}

				}

			}
			else if (editarRamal.equals("true")) {
				
				for (Usuario usuario : conf.getListaUsuarios()) {
					if (usuario.getOrigTerminalName().equals(terminalEditar)) {
						tagNomeUsuario = usuario.getNomeUsuario();
						tagSenhaUsuario = usuario.getSenhaUsuario();
						tagOrigAddressName = usuario.getOrigAddressName();
						tagOrigTerminalName = usuario.getOrigTerminalName();
						break;
					}

				}
			}

			jsonConfig = gson.toJson(conf);
			BufferedWriter bw = new BufferedWriter(new FileWriter("config.json"));
			bw.write(jsonConfig);
			bw.close();

			try {

				String arq = getClass().getResource("/html/Config.html").toString();

				if (arq.contains("jar:")) {
					//System.out.println("Recuperando recurso no JAR");
					InputStream in = getClass().getResourceAsStream("/html/Config.html");
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					while (reader.ready()) {
						htmlConf = htmlConf + reader.readLine();

					}
					reader.close();
					//ok(t, htmlConf);
					//return;
				}
				else {
					//System.out.println("Recuperando recurso");
					arq = arq.replace("file:", "");
					arq = arq.replace("jar:", "");
					BufferedReader brConf = new BufferedReader(new FileReader(arq));
					while (brConf.ready()) {
						htmlConf = htmlConf + brConf.readLine();

					}
					brConf.close();
				}
				
				//System.err.println("1");
				
				
				if(conf.getNomeServidorAvaya()!=null)
					htmlConf = htmlConf.replace("endServidorAvayaTag", conf.getNomeServidorAvaya());
				else
					htmlConf = htmlConf.replace("endServidorAvayaTag","");
				if(conf.getPortaServidorAvaya()!=null)
					htmlConf = htmlConf.replace("portaServidorAvayaTag", conf.getPortaServidorAvaya());
				else
					htmlConf = htmlConf.replace("portaServidorAvayaTag","");
				if(conf.getDominio()!=null)
					htmlConf = htmlConf.replace("dominioTag", conf.getDominio());
				else
					htmlConf = htmlConf.replace("dominioTag","");
				
				htmlConf = htmlConf.replace("tagAlert", tagAlert);
				
				htmlConf = htmlConf.replace("tagNomeUsuario", tagNomeUsuario);
				htmlConf = htmlConf.replace("tagSenhaUsuario", tagSenhaUsuario);
				htmlConf = htmlConf.replace("tagOrigTerminalName", tagOrigTerminalName);
				htmlConf = htmlConf.replace("tagOrigAddressName", tagOrigAddressName);
				
				
				//System.err.println("2");

				for (Usuario usuario : conf.getListaUsuarios()) {
					usuarioTableHtml = usuarioTableHtml + "<tr><td>" + usuario.getNomeUsuario() + "</td><td>"
							+ usuario.getOrigAddressName() + "</td><td>" + usuario.getOrigTerminalName()
							+ "</td><td><input type=\"button\" value=\"excluir\" onclick=\"excluir('"
							+ usuario.getOrigTerminalName()
							+ "');\"/></td> <td> <input type=\"button\" value=\"editar\" onclick=\"editar('"
							+ usuario.getOrigTerminalName()+"');\"/></td></tr>";

				}
				htmlConf = htmlConf.replace("linhaTag", usuarioTableHtml);

				try {
					byte[] bs = htmlConf.getBytes("UTF-8");
					t.sendResponseHeaders(200, bs.length);
					OutputStream os = t.getResponseBody();
					os.write(bs);
				} catch (IOException ex) {
					//System.err.println("2");
				}

			} catch (Exception e) {
				//System.err.println(e.getMessage());
			}

		} else if (uri.getPath().equals("/Avaya/rest/ramal/eventos")) {
			if (requestContent.toString().length() > 0) {
				Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
				setEndPoint(gson.fromJson(requestContent.toString().replace("@xsi.type", "xsitype"), EndPoint.class));

				for (ClienteEventos cli : listaClienteEventos) {
					if (cli.getId().equals(endPoint.getEvent().getParams().getTerminalName())) {
						String evento = endPoint.getEvent().getType() + ";"
								+ endPoint.getEvent().getParams().getCallingAddressName() + ";"
								+ endPoint.getEvent().getParams().getCalledAddressName() + ";"
								+ endPoint.getEvent().getParams().getContactID() + "\n";
						cli.send(evento);

						break;
					}
				}
			}
			ok(t, "");

		} else if (uri.getPath().equals("/Avaya/rest/ramal/discar")) {

			String org = "";
			String dst = "";
			String query = uri.getQuery();
			if (query != null) {
				String[] queryParams = query.split(AND_DELIMITER);
				if (queryParams.length > 0) {
					for (String qParam : queryParams) {
						String[] param = qParam.split(EQUAL_DELIMITER);
						if (param.length > 0) {
							for (int i = 0; i < param.length; i++) {
								if ("org".equalsIgnoreCase(param[0])) {
									org = param[1];
								}
								if ("dst".equalsIgnoreCase(param[0])) {
									dst = param[1];
								}
							}
						}
					}
				}

				ClienteRestAvaya cliente = null;
				boolean isFind = false;
				boolean noConf = true;
				for (ClienteRestAvaya cra : listaRamal) {
					if (cra.getTerminalName().equals(org)) {
						cliente = cra;
						isFind = true;
						break;
					}
				}
				if (!isFind) {

					if (conf.getListaUsuarios() != null) {
						for (Usuario usuario : conf.getListaUsuarios()) {
							if (usuario.getNomeUsuario().equals(org)) {
								cliente = new ClienteRestAvaya();
								listaRamal.add(cliente);
								cliente.setTerminalName(org);
								cliente.setServidorEnd(conf.getNomeServidorAvaya());
								cliente.setServidorPorta(conf.getPortaServidorAvaya());
								cliente.setDomain(conf.getDominio());
								cliente.setUsername(usuario.getNomeUsuario());
								cliente.setPassword(usuario.getSenhaUsuario());
								cliente.setAdressName(usuario.getOrigAddressName());
								noConf = false;
								break;
							}
						}
					} else {
						String response = "arquivo de configuracao nao carregado.";
						t.sendResponseHeaders(HTTP_OK_STATUS, response.length());
						OutputStream os = t.getResponseBody();
						os.write(response.getBytes());
						os.close();

					}

				}

				if (!noConf) {
					String response = "";
					if (cliente.obterToken()) {
						cliente.setOrg(org);
						cliente.setDst(dst);
						if (cliente.discar()) {
							response = cliente.getContactId().getContact().getContactId();
						} else {
							response = "sem contato.";
						}
					} else
						response = "nao foi possivel obter token";
					t.sendResponseHeaders(HTTP_OK_STATUS, response.length());
					OutputStream os = t.getResponseBody();
					os.write(response.getBytes());
					os.close();
				} else {
					String response = "Configuracao nao encontrada.";
					t.sendResponseHeaders(HTTP_OK_STATUS, response.length());
					OutputStream os = t.getResponseBody();
					os.write(response.getBytes());
					os.close();

				}

			}

		} else if (uri.getPath().equals("/Avaya/rest/ramal/desligar")) {
			filtro.setUri(uri);
			filtro.parse();
			if (filtro.getRml() != null) {
				ClienteRestAvaya cliente = obtemClienteRest(t, filtro.getRml());
				if (cliente.desligar()) {
					ok(t, "");
				} else
					ok(t, "sem contato.");
			} else
				ok(t, "informe numero do ramal.");

		} else if (uri.getPath().equals("/Avaya/rest/ramal/atender")) {
			filtro.setUri(uri);
			filtro.parse();
			if (filtro.getRml() != null) {
				ClienteRestAvaya cliente = obtemClienteRest(t, filtro.getRml());
				if (cliente.atender()) {
					ok(t, "");
				} else
					ok(t, "sem contato.");
			} else
				ok(t, "informe numero do ramal.");
		}

	}

	public void ok(HttpExchange t, String response) throws IOException {

		t.sendResponseHeaders(HTTP_OK_STATUS, response.length());
		OutputStream os = t.getResponseBody();
		os.write(response.getBytes());
		os.close();

	}

	public ClienteRestAvaya obtemClienteRest(HttpExchange t, String rml) throws IOException {
		ClienteRestAvaya cliente = null;
		boolean isFind = false;
		boolean noConf = true;
		for (ClienteRestAvaya cra : listaRamal) {
			if (cra.getTerminalName().equals(rml)) {
				cliente = cra;
				isFind = true;
				break;
			}
		}
		if (!isFind) {

			if (conf.getListaUsuarios() != null) {
				for (Usuario usuario : conf.getListaUsuarios()) {
					if (usuario.getNomeUsuario().equals(rml)) {
						cliente = new ClienteRestAvaya();
						listaRamal.add(cliente);
						cliente.setTerminalName(rml);
						cliente.setServidorEnd(conf.getNomeServidorAvaya());
						cliente.setServidorPorta(conf.getPortaServidorAvaya());
						cliente.setDomain(conf.getDominio());
						cliente.setUsername(usuario.getNomeUsuario());
						cliente.setPassword(usuario.getSenhaUsuario());
						cliente.setAdressName(usuario.getOrigAddressName());
						noConf = false;
						break;
					}
				}
			} else {
				String response = "arquivo de configuracao nao carregado.";
				t.sendResponseHeaders(HTTP_OK_STATUS, response.length());
				OutputStream os = t.getResponseBody();
				os.write(response.getBytes());
				os.close();
			}

		}

		if (!noConf) {

			if (cliente.obterToken()) {
				return cliente;
			}
		}
		return null;

	}

	public EndPoint getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(EndPoint endPoint) {
		this.endPoint = endPoint;
	}

	public Configuracao getConf() {
		return conf;
	}

	public void setConf(Configuracao conf) {
		this.conf = conf;
	}

	public List<ClienteEventos> getListaClienteEventos() {
		return listaClienteEventos;
	}

	public void setListaClienteEventos(List<ClienteEventos> listaClienteEventos) {
		this.listaClienteEventos = listaClienteEventos;
	}

}
