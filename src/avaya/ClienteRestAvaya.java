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

	private HttpURLConnection con;

	public String obterToken() {

		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		LoginCCT loginCCT = new LoginCCT();
		loginCCT.setUser(new User());
		loginCCT.getUser().setDomain(domain);
		loginCCT.getUser().setPassword(password);
		loginCCT.getUser().setUsername(username);
		String inputJson = gson.toJson(loginCCT);
		System.out.println(inputJson);
		con = HttpClient.httpConnect(servidorEnd + ":" + servidorPorta, "/session");
		String jsonRetorno = HttpClient.postMethod(con, inputJson);
		System.out.println(jsonRetorno);
		ssotoken = gson.fromJson(jsonRetorno, TokenRest.class);
		return jsonRetorno;

	}

	public String assinarEventos(List<String> entityNames) throws UnknownHostException {
		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		RestSubscription restSubscription = new RestSubscription();

		Subscription subscription = new Subscription();

		restSubscription.setSubscription(subscription);
		SubscriptionDetails subscriptionDetails = new SubscriptionDetails();
		subscriptionDetails.setType("terminal");

		subscriptionDetails.setEntityNames(entityNames);
		subscription.setEventEndpointUri("http://"+InetAddress.getLocalHost().getHostAddress()+":"+portaEvento+"/Avaya/rest/ramal/eventos");
		subscription.setProviderName("Passive");
		subscription.setSubscriptionDetails(subscriptionDetails);
		String inputJson = gson.toJson(restSubscription);

		System.out.println(inputJson);
		con = HttpClient.httpConnect(servidorEnd + ":" + servidorPorta,
				"/subscriptions?ssotoken=" + ssotoken.getUser().getSsoTokenValue());
		String jsonRetorno = HttpClient.postMethod(con, inputJson);
		System.out.println(jsonRetorno);
		return jsonRetorno;
	}

	public String discar() throws JsonSyntaxException, IOException {

		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		RestContact restContact = new RestContact();
		List<String> destAddressNames = new ArrayList<>();
		destAddressNames.add(dst);
		Contact contact = new Contact();
		restContact.setContact(contact);
		contact.setDestAddressNames(destAddressNames);
		contact.setMode("create");
		contact.setOrigAddressName(org);
		contact.setOrigTerminalName(terminalName);
		contact.setProviderName("Passive");
		String inputJson = gson.toJson(restContact);
		System.out.println(inputJson);
		con = HttpClient.httpConnect(servidorEnd + ":" + servidorPorta,
				"/contacts?ssotoken=" + ssotoken.getUser().getSsoTokenValue());
		String jsonRetorno = HttpClient.postMethod(con, inputJson);
		System.out.println(jsonRetorno);
		if (con.getResponseCode() != 200) {
			contactId = gson.fromJson(jsonRetorno, ContactIdRest.class);
		}
		
		return jsonRetorno;
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

}
