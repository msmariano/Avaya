package entity;

import java.util.List;

public class Contact {
	private List<String> destAddressNames;
	private String terminalType;
	public List<String> getDestAddressNames() {
		return destAddressNames;
	}
	public void setDestAddressNames(List<String> destAddressNames) {
		this.destAddressNames = destAddressNames;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
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
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	public String getTerminalType() {
		return terminalType;
	}
	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}
	private String mode;
	private String origAddressName;
	private String origTerminalName;
	private String providerName;
	
}


