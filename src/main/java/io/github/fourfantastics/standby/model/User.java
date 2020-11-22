package io.github.fourfantastics.standby.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public abstract class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;


	@NotNull
	@Column(unique = true, nullable = false)
	@Length(min = 5,max=64)
	String name;

	@NotNull
	@Column(unique = true, nullable = false)
	@Length(min = 5)
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
	
	@ManyToMany
	List<Filmmaker> filmmakers;
	
	@OneToMany(mappedBy = "user")
	List<Notification> notifications;
	
	@OneToOne(optional=false)
	private NotificationConfiguration notificationConfiguration;
	
	@OneToMany(mappedBy = "user")
	List<Rating> ratings;
	
	@OneToMany(mappedBy = "user")
	List<Comment> comments;
	
	@ManyToMany
	List<ShortFilm> shortfilms;

}