package io.github.fourfantastics.standby.model;

public enum RequestStateType {
	PENDING("Pending"),
	ACCEPTED("Accepted"),
	DECLINED("Declined");
	
	private String name;
	   
	RequestStateType(String name) {
		   this.name = name;
	   }
	   
	   public String getName() {
		   return name;
	   }
	   
	   public String toString() {
		   return getName();
	   }
}
