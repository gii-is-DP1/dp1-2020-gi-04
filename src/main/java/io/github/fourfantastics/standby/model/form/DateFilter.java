package io.github.fourfantastics.standby.model.form;

public enum DateFilter {
	ALL("All time"), TODAY("Today"), WEEK("This week"), MONTH("This month"), YEAR("This year");

	private String name;

	DateFilter(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
