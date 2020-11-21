package io.github.fourfantastics.standby.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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
}
