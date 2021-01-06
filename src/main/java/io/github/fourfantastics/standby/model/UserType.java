package io.github.fourfantastics.standby.model;

public enum UserType {
	Filmmaker("Filmmaker"),
	Company("Company");
	
	private String name;
	
	private UserType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
