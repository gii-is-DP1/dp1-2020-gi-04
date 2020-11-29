package io.github.fourfantastics.standby.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = "movies")
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@NotEmpty
	@NotNull
	@Column(nullable = false)
	String tagname;

	@ManyToMany(fetch = FetchType.EAGER, mappedBy = "tags")
	Set<ShortFilm> movies = new HashSet<ShortFilm>();
}
