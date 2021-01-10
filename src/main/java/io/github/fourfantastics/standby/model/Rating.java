package io.github.fourfantastics.standby.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "user", "shortFilm" })
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@NotNull
	@Column(nullable = false)
	@Range(min = 1, max = 10)
	Integer grade;

	@NotNull
	@Column(nullable = false)
	Long date;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	User user;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	ShortFilm shortFilm;
}
