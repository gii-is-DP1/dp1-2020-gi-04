package io.github.fourfantastics.standby.model.form;

public enum SortType {
	UPLOAD_DATE("Upload date"),
	RATINGS("Rating"),
	VIEWS("Views");
	
	private String name;
	   
	SortType(String name) {
		this.name = name;
	}
	   
	public String getName() {
		return name;
	}
}
