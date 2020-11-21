package io.github.fourfantastics.standby.model;

public interface User {
	public Long getId();
	public void setId(Long id);
	public String getName();
	public void setName(String name);
	public String getEmail();
	public void setEmail(String email);
	public String getPassword();
	public void setPassword(String password);
	public Long getCreationDate();
	public void setCreationDate(Long creationDate);
	public String getPhotoUrl();
	public void setPhotoUrl(String photoUrl);
}