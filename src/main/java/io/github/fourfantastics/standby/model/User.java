package io.github.fourfantastics.standby.model;

import java.util.HashSet;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "notifications", "ratings", "comments","configuration" })
@NoArgsConstructor
@AllArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	Long id;

	@NotNull
	UserType type;

	@NotNull
	@Column(unique = true, nullable = false)
	@Length(min = 5, max = 64)
	String name;

	@NotNull
	@Column(unique = true, nullable = false)
	@Email
	String email;

	@NotNull
	@Column(nullable = false)
	String password;

	@NotNull
	@Column(nullable = false)
	Long creationDate;

	@Column(nullable = true)
	String photoUrl;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
	Set<Notification> notifications = new HashSet<Notification>();

	@OneToOne(fetch = FetchType.EAGER)
	NotificationConfiguration configuration;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
	Set<Rating> ratings = new HashSet<Rating>();

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
	Set<Comment> comments = new HashSet<Comment>();


}