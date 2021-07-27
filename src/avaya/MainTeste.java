package avaya;

import java.io.IOException;

import com.google.gson.JsonSyntaxException;

public class MainTeste {

	public static void main(String[] args) throws JsonSyntaxException, IOException {
		// TODO Auto-generated method stub
		
		ClienteRestAvaya teste = new ClienteRestAvaya();
		teste.setServidorEnd("servidor");
		teste.setServidorPorta("80");
		teste.setDomain("domain");
		teste.setUsername("username");
		teste.setPassword("password");
		teste.obterToken();
		teste.setDst("6000");
		teste.discar();
		

	}

}
