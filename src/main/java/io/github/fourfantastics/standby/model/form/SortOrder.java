package io.github.fourfantastics.standby.model.form;

public enum SortOrder {
	DESCENDING("Descending"),
	ASCENDING("Ascending");
	
	private String name;
	
	SortOrder(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
