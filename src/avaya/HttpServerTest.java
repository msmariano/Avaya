package avaya;

public class HttpServerTest {

	private static final String CONTEXT = "/Avaya";
	private static final int PORT = 8000;
	private HttpRequestHandler handler;

	
	HttpServerTest(){
		handler = new HttpRequestHandler();
	}
	
	public static void main(String[] args) throws Exception {

		HttpServerTest teste = new HttpServerTest();
		// Create a new SimpleHttpServer
		Servidor simpleHttpServer = new Servidor(PORT, CONTEXT, teste.getHandler());

		// Start the server
		simpleHttpServer.start();
		System.out.println("Server is started and listening on port " + PORT);
	}

	public HttpRequestHandler getHandler() {
		return handler;
	}

	public void setHandler(HttpRequestHandler handler) {
		this.handler = handler;
	}

}
