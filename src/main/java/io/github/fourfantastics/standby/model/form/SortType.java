package io.github.fourfantastics.standby.model.form;

public enum SortType {
	UPLOAD_DATE("Today"),
	RATINGS("This week"),
	VIEWS("This month");
	
	private String name;
	   
	SortType(String name) {
		this.name = name;
	}
	   
	public String getName() {
		return name;
	}
}
