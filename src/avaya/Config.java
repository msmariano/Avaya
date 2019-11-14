package avaya;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.SystemColor;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import entity.ConfiguracaoGeral;

public class Config extends JFrame implements ActionListener, WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7301819076565650323L;

	JTextField tFPathConfig;
	JTextField tFHttpPort;
	
	JButton bObterPathConf;
	
	
	

	public Config() {

		//setLayout(new BorderLayout());
		setTitle("Configuração.");
		setBackground(SystemColor.control);
		setSize(500, 300);
		JPanel p1 = new JPanel(), p2 = new JPanel(),
				p3 = new JPanel();
		p3.add(tFPathConfig = new JTextField(30));
		p3.add(tFHttpPort = new JTextField(30));
		 setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		p2.add(bObterPathConf= new JButton("ArquivoConf"));
		p2.add(bObterPathConf= new JButton("ArquivoConf"));
	
		
		
		
		bObterPathConf.addActionListener(this);
		
		Container con = getContentPane();
		
		p1.add(p3);
		p1.add(p2);
		
		con.add(p1);
		
		p1 = new JPanel();
		p1.setBackground(SystemColor.control);
		
		
		addWindowListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bObterPathConf) {

			JFileChooser file = new JFileChooser();
			file.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int i = file.showSaveDialog(null);
			if (i != 1) {
				File arquivo = file.getSelectedFile();
				System.out.println();
				ConfiguracaoGeral cg = new ConfiguracaoGeral();
				cg.setPathConfiguracao(arquivo.getPath());
				tFPathConfig.setText(arquivo.getPath());
				Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create();
				String jsonCg = gson.toJson(cg);
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter("configGeral.json"));
					bw.write(jsonCg);
					bw.close();
				} catch (IOException e1) {
				}
			}

		}

	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}

}
