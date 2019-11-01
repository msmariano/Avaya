package entity;

import java.util.List;

public class SubscriptionDetails {
	
	private List<String> entityNames;
	private String type;
	
	public List<String> getEntityNames() {
		return entityNames;
	}
	public void setEntityNames(List<String> entityNames) {
		this.entityNames = entityNames;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}



