package entity;

import java.util.List;

public class Configuracao {
	
	private String nomeServidorAvaya;
	private String portaServidorAvaya;
	private String dominio;
	private String portaEventos;
	private String usuarioCCT;
	private String senhaCCT;
	
	public String getUsuarioCCT() {
		return usuarioCCT;
	}
	public void setUsuarioCCT(String usuarioCCT) {
		this.usuarioCCT = usuarioCCT;
	}
	public String getSenhaCCT() {
		return senhaCCT;
	}
	public void setSenhaCCT(String senhaCCT) {
		this.senhaCCT = senhaCCT;
	}
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
	public String getDominio() {
		return dominio;
	}
	public void setDominio(String dominio) {
		this.dominio = dominio;
	}
	public String getPortaEventos() {
		return portaEventos;
	}
	public void setPortaEventos(String portaEventos) {
		this.portaEventos = portaEventos;
	}
	
}
