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
@ToString(exclude = "sentRequests")

@AllArgsConstructor
public class Company extends User {
	@NotNull
	@Column(unique = true, nullable = false)
	String companyName;

	@NotNull
	@Column(unique = true, nullable = false)
	Integer taxIDNumber;

	@NotNull
	@Column(nullable = false)
	String businessPhone;

	@NotNull
	@Column(nullable = false)
	String officeAddress;
	
	@OneToMany(fetch = FetchType.EAGER)
	Set<PrivacyRequest> sentRequests = new HashSet<PrivacyRequest>();

	public Company() {
		super();
		setType(UserType.Company);
	}
}