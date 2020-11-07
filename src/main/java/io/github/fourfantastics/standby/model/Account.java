package io.github.fourfantastics.standby.model;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
	@Size(min = 4, max = 64)
	String name;
	@NotNull
	@Size(min = 8, max = 128)
	@Email
	String email;
	@NotNull
	@Size(min = 8, max = 128)
	String password;
	@DateTimeFormat(iso = ISO.DATE)
	Date creationDate;
	String photoUrl;
}