package io.github.fourfantastics.standby.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

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
public class Company implements User {
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