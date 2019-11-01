package entity;

public class Usuario {
	private String nomeUsuario;
	private String senhaUsuario;
	private String terminalUsuario;
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
	public String getTerminalUsuario() {
		return terminalUsuario;
	}
	public void setTerminalUsuario(String terminalUsuario) {
		this.terminalUsuario = terminalUsuario;
	}
	public String getTokenSessaoUsuario() {
		return tokenSessaoUsuario;
	}
	public void setTokenSessaoUsuario(String tokenSessaoUsuario) {
		this.tokenSessaoUsuario = tokenSessaoUsuario;
	}
	private String tokenSessaoUsuario;
}
