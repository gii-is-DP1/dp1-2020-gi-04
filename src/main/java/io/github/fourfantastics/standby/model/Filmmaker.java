package io.github.fourfantastics.standby.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = { "receivedRequests", "participateAs", "uploadedShortFilms" })
@AllArgsConstructor
public class Filmmaker extends User {
	@NotNull
	@Column(unique = false, nullable = false)
	String fullname;

	@Column(nullable = true)
	String country;

	@Column(nullable = true)
	String city;

	@Column(nullable = true)
	String phone;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "filmmaker")
	Set<PrivacyRequest> receivedRequests = new HashSet<PrivacyRequest>();

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "filmmaker")
	Set<Role> participateAs = new HashSet<Role>();

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "uploader")
	Set<ShortFilm> uploadedShortFilms = new HashSet<ShortFilm>();

	public Filmmaker() {
		super();
		setType(UserType.Filmmaker);
	}
}
