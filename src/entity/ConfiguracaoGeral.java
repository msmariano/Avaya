package entity;

public class ConfiguracaoGeral {

	private String pathConfiguracao;
	private String httpPort;
	private String mensPort;
	private String ipServidor;
	private String caminhoDoExecutavel;
	
	
	public String getHttpPort() {
		return httpPort;
	}
	public void setHttpPort(String httpPort) {
		this.httpPort = httpPort;
	}
	public String getMensPort() {
		return mensPort;
	}
	public void setMensPort(String mensPort) {
		this.mensPort = mensPort;
	}
	public String getPathConfiguracao() {
		return pathConfiguracao;
	}
	public void setPathConfiguracao(String pathConfiguracao) {
		this.pathConfiguracao = pathConfiguracao;
	}
	public String getIpServidor() {
		return ipServidor;
	}
	public void setIpServidor(String ipServidor) {
		this.ipServidor = ipServidor;
	}
	public String getCaminhoDoExecutavel() {
		return caminhoDoExecutavel;
	}
	public void setCaminhoDoExecutavel(String caminhoDoExecutavel) {
		this.caminhoDoExecutavel = caminhoDoExecutavel;
	}
	
}
