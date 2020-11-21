package io.github.fourfantastics.standby.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
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
@ToString(exclude = "ratings")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class ShortFilm {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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
	
	@OneToMany(mappedBy = "shortFilm")
	List<Rating> ratings;
	
}