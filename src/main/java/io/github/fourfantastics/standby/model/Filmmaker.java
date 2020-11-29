package io.github.fourfantastics.standby.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"userSubscriptions","receivedRequests","participateAs"})
@AllArgsConstructor
public class Filmmaker extends User {
	@NotNull
	@Column(unique = true, nullable = false)
	String fullname;

	@Column(nullable = true)
	String country;

	@Column(nullable = true)
	String city;

	@Column(nullable = true)
	String phone;
	
	@ManyToMany(fetch = FetchType.EAGER , mappedBy = "filmmakersSubscribedTo")
	Set<User> userSubscriptions = new HashSet<User>();
	
	@OneToMany(fetch = FetchType.EAGER)
	Set<PrivacyRequest> receivedRequests = new HashSet<PrivacyRequest>();
	
	@OneToMany(fetch = FetchType.EAGER)
	Set<Role> participateAs = new HashSet<Role>();

	public Filmmaker() {
		super();
		setType(UserType.Filmmaker);
	}
}
