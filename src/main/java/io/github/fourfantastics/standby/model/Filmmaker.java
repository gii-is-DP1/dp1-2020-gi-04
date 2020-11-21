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
	@Length(min = 5)
	String fullname;

	@Column(nullable = true)
	String country;

	@Column(nullable = true)
	String city;

	@Pattern(regexp = "^\\+[1-9]{1}[0-9]{3,14}$")
	@Column(nullable = true)
	String phone;
}
