package io.github.fourfantastics.standby.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class ShortFilm {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@NotEmpty
	@Length(min = 5)
	String name;

	@NotEmpty
	String fileUrl;

	@NotNull
	Long uploadDate;

	@NotEmpty
	@Length(min = 10)
	String description;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "shortFilm")
	Set<Rating> ratings = new HashSet<Rating>();

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "shortFilm")
	Set<Comment> comments = new HashSet<Comment>();
	
	@ManyToMany(fetch = FetchType.EAGER)
	Set<Tag> tags = new HashSet<Tag>();

}