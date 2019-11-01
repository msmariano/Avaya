package avaya;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import entity.EndPoint;

@SuppressWarnings("restriction")
public class HttpRequestHandler implements HttpHandler {

	private static final int HTTP_OK_STATUS = 200;

	private static final String AND_DELIMITER = "&";
	private static final String EQUAL_DELIMITER = "=";

	private List<ClienteRestAvaya> listaRamal;
	
	private EndPoint endPoint;
	
	HttpRequestHandler(){
		listaRamal = new ArrayList<>();
	}

	public void handle(HttpExchange t) throws IOException {

		BufferedReader br = null;
		String inputRequest = null;
		StringBuilder requestContent = new StringBuilder();

		if (t.getRequestBody() != null) {
			br = new BufferedReader(new InputStreamReader(t.getRequestBody()));
			if (br != null) {
				while ((inputRequest = br.readLine()) != null) {
					System.out.println(inputRequest);
					requestContent.append(inputRequest);
				}

			}
		}

		// Create a response form the request query parameters
		URI uri = t.getRequestURI();

		if (uri.getPath().equals("/Avaya/rest/ramal/eventos")) {
			if (requestContent.toString().length() > 0) {
				Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
				setEndPoint(gson.fromJson(requestContent.toString().replace("@xsi.type", "xsitype"), EndPoint.class));
			}

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
				for (ClienteRestAvaya cra : listaRamal) {
					if(cra.getTerminalName().equals(org)) {
						cliente = cra;
						isFind = true;
						break;
					}
				}
				if(!isFind) {
					cliente = new ClienteRestAvaya();
					listaRamal.add(cliente);
					cliente.setTerminalName(org);
					//cliente.obterToken();
				}
				
				cliente.setOrg(org);
				cliente.setDst(dst);

			}

		}

		String response = "";
		t.sendResponseHeaders(HTTP_OK_STATUS, response.length());
		OutputStream os = t.getResponseBody();
		os.write(response.getBytes());
		os.close();

	}

	public EndPoint getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(EndPoint endPoint) {
		this.endPoint = endPoint;
	}

}
