package avaya;

public class ServidorRest {

	private static final String CONTEXT = "/Avaya";
	private static final int PORT = 8000;
	private HttpRequestHandler handler;

	
	ServidorRest(){
		handler = new HttpRequestHandler();
	}
	
	public static void main(String[] args) throws Exception {

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
