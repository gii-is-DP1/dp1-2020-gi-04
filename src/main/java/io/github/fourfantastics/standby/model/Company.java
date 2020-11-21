package io.github.fourfantastics.standby.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Company extends User {

	@NotNull
	@Column(unique = true, nullable = false)
	@Length(min = 5)
	String companyName;

	@NotNull
	@Column(unique = true, nullable = false)
	Integer taxIDNumber;

	@NotNull
	@Column(nullable = false)
	String bussinessPhone;

	@NotNull
	@Column(nullable = false)
	String officeAddress;
}