package io.github.fourfantastics.standby.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "user", "favouriteShortfilm" })
@NoArgsConstructor
@AllArgsConstructor
public class Favourite {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	User user;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	ShortFilm favouriteShortfilm;

}
