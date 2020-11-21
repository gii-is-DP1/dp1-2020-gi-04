package io.github.fourfantastics.standby.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Filmmaker implements User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	
	@NotNull
	@Column(unique = true, nullable = false)
	@Length(min = 5)
	String name;
	
	@NotNull
	@Column(unique = true, nullable = false)
	@Length(min = 5)
	@Email
	String email;
	
	@NotNull
	@Column(nullable = false)
	@Length(min = 8)
	String password;
	
	@NotNull
	@Column(nullable = false)
	Long creationDate;
	
	@Column(nullable = true)
	String photoUrl;
	
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
