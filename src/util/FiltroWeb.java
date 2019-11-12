package util;

import java.net.URI;

public class FiltroWeb {
	private URI uri;
	private static final String AND_DELIMITER = "&";
	private static final String EQUAL_DELIMITER = "=";
	private String org;
	private String dst;
	private String rml;
	private String token;
	private String callId;
	
	
	public void parse() {
		String query = uri.getQuery();
		if (query != null) {
			String[] queryParams = query.split(AND_DELIMITER);
			if (queryParams.length > 0) {
				for (String qParam : queryParams) {
					String[] param = qParam.split(EQUAL_DELIMITER);
					if(param.length==2) {
						if(param[0].equalsIgnoreCase("org")) {
							org = param[1];
						}
						if(param[0].equalsIgnoreCase("dst")) {
							dst = param[1];
						}
						if(param[0].equalsIgnoreCase("rml")) {
							rml = param[1];
						}
						if(param[0].equalsIgnoreCase("token")) {
							token = param[1];
						}
						if(param[0].equalsIgnoreCase("callId")) {
							callId = param[1];
						}
					}
				}
			}
		}
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getDst() {
		return dst;
	}

	public void setDst(String dst) {
		this.dst = dst;
	}

	public String getRml() {
		return rml;
	}

	public void setRml(String rml) {
		this.rml = rml;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getCallId() {
		return callId;
	}

	public void setCallId(String callId) {
		this.callId = callId;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}
	
}
