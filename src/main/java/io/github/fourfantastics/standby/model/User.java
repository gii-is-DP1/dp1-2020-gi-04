package io.github.fourfantastics.standby.model;

import javax.persistence.Column;

import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class User extends BaseEntity {
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
}