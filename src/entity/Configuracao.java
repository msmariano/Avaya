package entity;

import java.util.List;

public class Configuracao {
	
	private String nomeServidorAvaya;
	private String portaServidorAvaya;
	private List<Usuario> listaUsuarios;
	
	public String getNomeServidorAvaya() {
		return nomeServidorAvaya;
	}
	public void setNomeServidorAvaya(String nomeServidorAvaya) {
		this.nomeServidorAvaya = nomeServidorAvaya;
	}
	public String getPortaServidorAvaya() {
		return portaServidorAvaya;
	}
	public void setPortaServidorAvaya(String portaServidorAvaya) {
		this.portaServidorAvaya = portaServidorAvaya;
	}
	public List<Usuario> getListaUsuarios() {
		return listaUsuarios;
	}
	public void setListaUsuarios(List<Usuario> listaUsuarios) {
		this.listaUsuarios = listaUsuarios;
	}
	
}
