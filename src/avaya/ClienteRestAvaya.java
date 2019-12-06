package avaya;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import entity.Contact;
import entity.ContactIdRest;
import entity.EndPoint;
import entity.LoginCCT;
import entity.RestContact;
import entity.RestSubscription;
import entity.Subscription;
import entity.SubscriptionDetails;
import entity.TokenRest;
import entity.User;
import rest.HttpClient;

public class ClienteRestAvaya {

	private String org;
	private String dst;
	private String terminalName;
	private TokenRest ssotoken;
	private String domain;
	private String password;
	private String username;
	private String servidorEnd;
	private String servidorPorta;
	private String portaEvento;
	private ContactIdRest contactId;
	private List<String> eventos;
	private String adressName;
	private EndPoint endPoint;

	private HttpURLConnection con;

	public ClienteRestAvaya() {
		eventos = new ArrayList<>();
	}

	public Boolean obterToken() {

		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		LoginCCT loginCCT = new LoginCCT();
		loginCCT.setUser(new User());
		loginCCT.getUser().setDomain(domain);
		loginCCT.getUser().setPassword(password);
		loginCCT.getUser().setUsername(username);
		String inputJson = gson.toJson(loginCCT);
		System.out.println(inputJson);
		con = HttpClient.httpConnect("http://"+servidorEnd + ":" + servidorPorta, "/session");
		if (con != null) {
			try {
				String jsonRetorno = HttpClient.postMethod(con, inputJson);
				System.out.println(jsonRetorno);
				ssotoken = gson.fromJson(jsonRetorno, TokenRest.class);
				return true;
			}
			catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
		System.err.println("Erro ao obter token."+servidorEnd + ":" + servidorPorta);
		return false;

	}

	public Boolean assinarEventos(List<String> entityNames) throws UnknownHostException {

		if (ssotoken != null && ssotoken.getUser() != null && ssotoken.getUser().getSsoTokenValue() != null) {
			Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
			RestSubscription restSubscription = new RestSubscription();

			Subscription subscription = new Subscription();

			restSubscription.setSubscription(subscription);
			SubscriptionDetails subscriptionDetails = new SubscriptionDetails();
			subscriptionDetails.setType("terminal");

			subscriptionDetails.setEntityNames(entityNames);
			subscription.setEventEndpointUri("http://" + InetAddress.getLocalHost().getHostAddress() + ":" + portaEvento
					+ "/Avaya/rest/ramal/eventos");
			subscription.setProviderName("Passive");
			subscription.setSubscriptionDetails(subscriptionDetails);
			String inputJson = gson.toJson(restSubscription);

			System.out.println(inputJson);

			con = HttpClient.httpConnect("http://"+servidorEnd + ":" + servidorPorta,
					"/subscriptions?ssotoken=" + ssotoken.getUser().getSsoTokenValue());
			if (con != null) {
				String jsonRetorno = HttpClient.postMethod(con, inputJson);
				System.out.println(jsonRetorno);
				System.out.println(
						"Servidor de Eventos Rest iniciado no servidor " + servidorEnd + " porta " + servidorPorta);
				return true;

			}

		}
		return false;
	}
	
	
	public Boolean atender() throws JsonSyntaxException, IOException {
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		RestContact restContact = new RestContact();
		Contact contact = new Contact();
		restContact.setContact(contact);
		contact.setOrigTerminalName(terminalName);
		contact.setMode("answer");
		contact.setProviderName("Passive");
		String inputJson = gson.toJson(restContact);
		System.out.println(inputJson);
		con = HttpClient.httpConnect("http://"+servidorEnd + ":" + servidorPorta,
				"/"+contactId.getContact().getContactId()+"?ssotoken=" + ssotoken.getUser().getSsoTokenValue());
		if (con != null) {
			String jsonRetorno = HttpClient.postMethod(con, inputJson);
			System.out.println(jsonRetorno);
			if (con.getResponseCode() != 200) {
				contactId = gson.fromJson(jsonRetorno, ContactIdRest.class);
				return true;
			}
		}
		
		return false;
	}
	
	
	public Boolean desligar() throws JsonSyntaxException, IOException {
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		RestContact restContact = new RestContact();
		Contact contact = new Contact();
		restContact.setContact(contact);
		contact.setOrigTerminalName(terminalName);
		contact.setMode("drop");
		contact.setProviderName("Passive");
		String inputJson = gson.toJson(restContact);
		System.out.println(inputJson);
		con = HttpClient.httpConnect("http://"+servidorEnd + ":" + servidorPorta,
				"/"+contactId.getContact().getContactId()+"?ssotoken=" + ssotoken.getUser().getSsoTokenValue());
		if (con != null) {
			String jsonRetorno = HttpClient.postMethod(con, inputJson);
			System.out.println(jsonRetorno);
			if (con.getResponseCode() != 200) {
				contactId = gson.fromJson(jsonRetorno, ContactIdRest.class);
				return true;
			}
		}
		
		return false;
	}

	public Boolean discar() throws JsonSyntaxException, IOException {

		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		RestContact restContact = new RestContact();
		List<String> destAddressNames = new ArrayList<>();
		destAddressNames.add(dst);
		Contact contact = new Contact();
		restContact.setContact(contact);
		contact.setDestAddressNames(destAddressNames);
		contact.setMode("create");
		contact.setOrigAddressName(adressName);
		contact.setOrigTerminalName(terminalName);
		contact.setProviderName("Passive");
		String inputJson = gson.toJson(restContact);
		System.out.println(inputJson);
		con = HttpClient.httpConnect("http://"+servidorEnd + ":" + servidorPorta,
				"/contacts?ssotoken=" + ssotoken.getUser().getSsoTokenValue());
		if (con != null) {
			String jsonRetorno = HttpClient.postMethod(con, inputJson);
			System.out.println(jsonRetorno);
			if (con.getResponseCode() == 200) {
				contactId = gson.fromJson(jsonRetorno, ContactIdRest.class);
				
				return true;
			}
		}

		return false;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getDst() {
		return dst;
	}

	public void setDst(String dst) {
		this.dst = dst;
	}

	public String getTerminalName() {
		return terminalName;
	}

	public void setTerminalName(String terminalName) {
		this.terminalName = terminalName;
	}

	public TokenRest getSsotoken() {
		return ssotoken;
	}

	public void setSsotoken(TokenRest ssotoken) {
		this.ssotoken = ssotoken;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getServidorEnd() {
		return servidorEnd;
	}

	public void setServidorEnd(String servidorEnd) {
		this.servidorEnd = servidorEnd;
	}

	public String getServidorPorta() {
		return servidorPorta;
	}

	public void setServidorPorta(String servidorPorta) {
		this.servidorPorta = servidorPorta;
	}

	public String getPortaEvento() {
		return portaEvento;
	}

	public void setPortaEvento(String portaEvento) {
		this.portaEvento = portaEvento;
	}

	public ContactIdRest getContactId() {
		return contactId;
	}

	public void setContactId(ContactIdRest contactId) {
		this.contactId = contactId;
	}

	public List<String> getEventos() {
		return eventos;
	}

	public void setEventos(List<String> eventos) {
		this.eventos = eventos;
	}

	public String getAdressName() {
		return adressName;
	}

	public void setAdressName(String adressName) {
		this.adressName = adressName;
	}

	public EndPoint getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(EndPoint endPoint) {
		this.endPoint = endPoint;
	}

}
