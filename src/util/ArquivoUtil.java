package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import entity.Configuracao;

public class ArquivoUtil {

	public static Configuracao leConfiguracao() throws IOException {

		Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
		FileReader arq = new FileReader("/home/msmariano/Desktop/conf.json");
		BufferedReader lerArq = new BufferedReader(arq);

		String json = "";
		String linha = lerArq.readLine();
		if (linha != null)
			json = json + linha;
		while (linha != null) {
			linha = lerArq.readLine();
			if (linha != null)
				json = json + linha;
		}

		//Log.grava(json);

		Configuracao conf = gson.fromJson(json, Configuracao.class);

		arq.close();

		return conf;

	}

}
