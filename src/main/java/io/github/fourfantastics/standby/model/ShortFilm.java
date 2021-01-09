package io.github.fourfantastics.standby.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "ratings", "comments", "tags", "favouriteUsers", "uploader" })
@AllArgsConstructor
public class ShortFilm {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@NotNull
	@NotEmpty
	@Length(max = 128)
	@Column(nullable = false)
	String title;

	@NotNull
	@NotEmpty
	@Column(nullable = false)
	String videoUrl;

	@Column(nullable = true)
	String thumbnailUrl;
	
	@NotNull
	@Column(nullable = false)
	Long uploadDate;

	@NotNull
	@Length(max = 10000)
	@Column(nullable = false)
	String description;
	
	@NotNull
	@Column(nullable = false)
	@Range(min = 0)
	Long viewCount;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "shortFilm")
	Set<Rating> ratings = new HashSet<Rating>();

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "shortFilm")
	Set<Comment> comments = new HashSet<Comment>();

	@ManyToMany(fetch = FetchType.EAGER)
	Set<Tag> tags = new HashSet<Tag>();

	@ManyToMany(fetch = FetchType.EAGER, mappedBy = "favouriteShortFilms")
	Set<User> favouriteUsers = new HashSet<User>();

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "shortfilm")
	Set<Role> roles = new HashSet<Role>();

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	Filmmaker uploader;
	
	public String getFormattedUploadDate() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.UK);
		return dateFormatter.format(new Date(getUploadDate()));
	}
	
	public ShortFilm() {
		super();
		setViewCount(0L);
	}
}