package avaya;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import entity.Configuracao;
import entity.Usuario;
import util.ArquivoUtil;

public class ServidorRest {

	private static final String CONTEXT = "/Avaya";
	private static final int PORT = 8000;
	private HttpRequestHandler handler;

	
	ServidorRest(){
		handler = new HttpRequestHandler();
	}
	
	public static void main(String[] args) throws Exception {
		
		
		Configuracao conf = new Configuracao();
		List<Usuario> usuarios = new ArrayList<>();
		Usuario usuario = new Usuario();
		Usuario usuario1 = new Usuario();
		
		usuarios.add(usuario);
		usuario.setNomeUsuario("3002");
		usuario.setSenhaUsuario("Avaya@123");
		usuario.setOrigTerminalName("3002");
		usuario.setOrigAddressName("sip:3002@ipo2.sigmatelecom.com.br");
		
		usuarios.add(usuario1);
		usuario1.setNomeUsuario("3001");
		usuario1.setSenhaUsuario("Avaya@123");
		usuario1.setOrigTerminalName("3001");
		usuario1.setOrigAddressName("sip:3001@ipo2.sigmatelecom.com.br");
		
		
		conf.setListaUsuarios(usuarios);
		conf.setNomeServidorAvaya("http://172.17.5.235");
		conf.setPortaServidorAvaya("9085");
		conf.setDominio("SGT-SRV-ACCS");
		
		//Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		//String json = gson.toJson(conf);
		
		
		
		Configuracao confArq = ArquivoUtil.leConfiguracao();
		
		
		System.out.println(confArq);

		ServidorRest teste = new ServidorRest();
		// Create a new SimpleHttpServer
		Servidor servidor = new Servidor(PORT, CONTEXT, teste.getHandler());

		// Start the server
		servidor.start();
		System.out.println("Server is started and listening on port " + PORT);
	}

	public HttpRequestHandler getHandler() {
		return handler;
	}

	public void setHandler(HttpRequestHandler handler) {
		this.handler = handler;
	}

}
