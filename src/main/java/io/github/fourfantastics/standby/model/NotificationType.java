package io.github.fourfantastics.standby.model;

public enum NotificationType {
	COMMENT("Comment"),
	RATING("Rating"),
	SUBSCRIPTION("Subscription"),
	PRIVACY_REQUEST("Privacy Request");
	
	private String name;
	   
	NotificationType(String name) {
		this.name = name;
	}
	   
	public String getName() {
		return name;
	}
}
