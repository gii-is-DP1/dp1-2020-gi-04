package io.github.fourfantastics.standby.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"userSubscriptions"})
@NoArgsConstructor
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
	
	/*@OneToMany(mappedBy = "filmmaker")
	List<PrivacyRequest> requests;*/
}
