package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Log {
	
	public static void grava(String mensLog) {
		
		 
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter("log.txt",true));
			bw.write(mensLog+"\n");
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
