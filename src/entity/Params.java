package entity;

public class Params {
	private String xsitype;
	private String terminalName;
    private String calledAddressName;
    private String callingAddressName;
    private String providerName;
    private String contactID;
    
    
	public String getTerminalName() {
		return terminalName;
	}
	public void setTerminalName(String terminalName) {
		this.terminalName = terminalName;
	}
	public String getCalledAddressName() {
		return calledAddressName;
	}
	public void setCalledAddressName(String calledAddressName) {
		this.calledAddressName = calledAddressName;
	}
	public String getCallingAddressName() {
		return callingAddressName;
	}
	public void setCallingAddressName(String callingAddressName) {
		this.callingAddressName = callingAddressName;
	}
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	public String getContactID() {
		return contactID;
	}
	public void setContactID(String contactID) {
		this.contactID = contactID;
	}
	public String getXsitype() {
		return xsitype;
	}
	public void setXsitype(String xsitype) {
		this.xsitype = xsitype;
	}
}
