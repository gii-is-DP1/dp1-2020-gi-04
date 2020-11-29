package io.github.fourfantastics.standby.model;

public enum UserType {
	Filmmaker("Filmmaker"),
	Company("Company");
	
	private String type;
	
	private UserType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public String toString() {
		return getType();
	}
}
