package entity;

public class Subscription {

	private String eventEndpointUri;
	private String providerName;
	private SubscriptionDetails subscriptionDetails;
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	public String getEventEndpointUri() {
		return eventEndpointUri;
	}
	public void setEventEndpointUri(String eventEndpointUri) {
		this.eventEndpointUri = eventEndpointUri;
	}
	public SubscriptionDetails getSubscriptionDetails() {
		return subscriptionDetails;
	}
	public void setSubscriptionDetails(SubscriptionDetails subscriptionDetails) {
		this.subscriptionDetails = subscriptionDetails;
	}
}


