package io.github.fourfantastics.standby.model;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	@NotNull
	@Length(min = 5)
	String name;
	@NotNull
	@Length(min = 5)
	@Email
	String email;
	@NotNull
	@Length(min = 8)
	String password;
	@NotNull
	@DateTimeFormat(iso = ISO.DATE)
	Date creationDate;
	String photoUrl;
}