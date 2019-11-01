package avaya;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import entity.AgentToken;
import entity.Contact;
import entity.EntityNames;
import entity.LoginCCT;
import entity.RestContact;
import entity.RestSubscription;
import entity.Subscription;
import entity.SubscriptionDetails;
import entity.Token;
import entity.TokenRest;
import entity.User;
import rest.HttpClient;

public class Cliente extends Frame implements ActionListener, WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7301819076565650323L;
	TextField tFtoken;
	Button bToken;
	TextField tFSubscribe;
	Button bSubscribe;
	
	
	TextField tFDialDst;
	Button bDial;
	
	
	
	HttpURLConnection con = null;

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Avaya Rest...");
		Cliente cliente = new Cliente();
		cliente.show();
		
	}
	
	public Cliente() {
		
		setLayout(new BorderLayout());
		setTitle("Avaya Teste Rest.");
		tFtoken  = new TextField(50);
		tFSubscribe =  new TextField(10);
		
		tFDialDst  =  new TextField(10);
		
		
		
		add(tFtoken);
		add(bToken = new Button("Login CCT"),BorderLayout.SOUTH);
		bToken.addActionListener(this);
		
		add(tFSubscribe,BorderLayout.SOUTH);
		add(bSubscribe = new Button("Assinar"),BorderLayout.SOUTH);
		bSubscribe.addActionListener(this);
		
		
		add(tFDialDst);
		add(bDial = new Button("Discar"),BorderLayout.SOUTH);
		bDial.addActionListener(this);
		
		
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.CENTER);
		
		setLayout(layout);
		
		pack();
		addWindowListener(this);
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		System.exit(0);
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bToken )
		{
			Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
			LoginCCT loginCCT = new LoginCCT();
			loginCCT.setUser(new User());
			loginCCT.getUser().setDomain("SGT-SRV-ACCS");
			loginCCT.getUser().setPassword("Avaya@123");
			loginCCT.getUser().setUsername("3002");
			String inputJson = gson.toJson(loginCCT);
			System.out.println(inputJson);
			con = HttpClient.httpConnect("http://172.17.5.235:9085", "/session");
			String jsonRetorno = HttpClient.postMethod(con, inputJson);
			
			TokenRest ssotoken = gson.fromJson(jsonRetorno,TokenRest.class);
			
			tFtoken.setText(ssotoken.getUser().getSsoTokenValue());
		}
		else if(e.getSource() == bSubscribe) {
			Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
			RestSubscription restSubscription = new RestSubscription();
			
			Subscription subscription = new Subscription();
			
			restSubscription.setSubscription(subscription);
			SubscriptionDetails subscriptionDetails = new SubscriptionDetails();
			subscriptionDetails.setType("terminal");
			List<String> entityNames = new ArrayList<>();
			if(tFSubscribe.getText()!=null && tFSubscribe.getText().length()>0)
				entityNames.add(tFSubscribe.getText());
			
			subscriptionDetails.setEntityNames(entityNames);
			subscription.setEventEndpointUri("http://172.17.4.5:8080/Avaya/rest/ramal/eventos");
			subscription.setProviderName("Passive");
			subscription.setSubscriptionDetails(subscriptionDetails);
			String inputJson = gson.toJson(restSubscription);
			
			System.out.println(inputJson);
			con = HttpClient.httpConnect("http://172.17.5.235:9085", "/subscriptions?ssotoken="+tFtoken.getText());
			String jsonRetorno = HttpClient.postMethod(con, inputJson);
			System.out.println(jsonRetorno);
			
		}
		else if (e.getSource() == bDial) {
			Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
			RestContact restContact = new RestContact();
			List<String> destAddressNames = new ArrayList<>();
			destAddressNames.add(tFDialDst.getText());
			Contact contact = new Contact();
			restContact.setContact(contact);
			contact.setDestAddressNames(destAddressNames);
			//contact.setTerminalType("AGENT");
			contact.setMode("create");
			contact.setOrigAddressName("sip:3002@ipo2.sigmatelecom.com.br");
			contact.setOrigTerminalName("3002");
			contact.setProviderName("Passive");
			String inputJson = gson.toJson(restContact);
			
			System.out.println(inputJson);
		
			
			con = HttpClient.httpConnect("http://172.17.5.235:9085", "/contacts?ssotoken="+tFtoken.getText());
			String jsonRetorno = HttpClient.postMethod(con, inputJson);
			System.out.println(jsonRetorno);
			
		}
		
	}

}
