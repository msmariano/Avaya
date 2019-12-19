package avaya;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import entity.ConfiguracaoGeral;
import util.Log;

public class Config extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7301819076565650323L;
	
	private final Integer ALTURA_COMPONENTE = 30;

	private JLabel lbPathConfig;
	private JLabel lbHttpPort;
	private JLabel lbMensPort;
	private JTextField pathConfig;
	private JTextField httpPort;
	private JTextField mensPort;
	private JButton salvar;
	private JButton procurar;

	private void initComponents() {
		setResizable(false);
		pathConfig = new JTextField();
		httpPort = new JTextField("8000");
		mensPort = new JTextField("8001");
		salvar = new JButton("salvar");
		procurar = new JButton("procurar");
		lbPathConfig = new JLabel("Caminho do arquivo de configuracao:");
		lbHttpPort = new JLabel("Porta HTTP:");
		lbMensPort = new JLabel("Porta Mensagens:");
		
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Configuracao");
		setBackground(new java.awt.Color(102, 102, 255));
		setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
		setMaximumSize(new java.awt.Dimension(600, 600));
		setPreferredSize(new java.awt.Dimension(600, 600));
		getContentPane().setLayout(null);

		
		lbPathConfig.setBounds(1, 0, 400, ALTURA_COMPONENTE);
		pathConfig.setBounds(1, 30, 400, ALTURA_COMPONENTE);
		procurar.setBounds(405, 30, 120, ALTURA_COMPONENTE);
		lbHttpPort.setBounds(1, 60, 120, ALTURA_COMPONENTE);
		httpPort.setBounds(1, 90, 60, ALTURA_COMPONENTE);
		lbMensPort.setBounds(1, 120, 200, ALTURA_COMPONENTE);
		mensPort.setBounds(1, 150, 60, ALTURA_COMPONENTE);
		salvar.setBounds(230, 420, 90, ALTURA_COMPONENTE);
		
		getContentPane().add(lbPathConfig);
		getContentPane().add(pathConfig);
		getContentPane().add(procurar);
		getContentPane().add(lbHttpPort);
		getContentPane().add(httpPort);
		getContentPane().add(lbMensPort);
		getContentPane().add(mensPort);
		getContentPane().add(salvar);
		
		
		
		procurar.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JFileChooser file = new JFileChooser();
				file.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int i = file.showSaveDialog(null);
				if (i != 1) {
					File arquivo = file.getSelectedFile();
					ConfiguracaoGeral cg = new ConfiguracaoGeral();
					cg.setPathConfiguracao(arquivo.getPath());
					pathConfig.setText(arquivo.getPath());
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

		});

	}

	public Config() {

		initComponents();
		setSize(550, 450);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

}
