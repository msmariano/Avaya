package entity;

public class Usuario {
	private String nomeUsuario;
	private String senhaUsuario;
	private String origTerminalName;
	private String origAddressName;
	private String tokenSessaoUsuario;
	
	public String getNomeUsuario() {
		return nomeUsuario;
	}
	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}
	public String getSenhaUsuario() {
		return senhaUsuario;
	}
	public void setSenhaUsuario(String senhaUsuario) {
		this.senhaUsuario = senhaUsuario;
	}
	
	public String getTokenSessaoUsuario() {
		return tokenSessaoUsuario;
	}
	public void setTokenSessaoUsuario(String tokenSessaoUsuario) {
		this.tokenSessaoUsuario = tokenSessaoUsuario;
	}
	public String getOrigAddressName() {
		return origAddressName;
	}
	public void setOrigAddressName(String origAddressName) {
		this.origAddressName = origAddressName;
	}
	public String getOrigTerminalName() {
		return origTerminalName;
	}
	public void setOrigTerminalName(String origTerminalName) {
		this.origTerminalName = origTerminalName;
	}
	
	
}
