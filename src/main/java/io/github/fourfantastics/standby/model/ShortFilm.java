package io.github.fourfantastics.standby.model;

import javax.persistence.Entity;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ShortFilm extends BaseEntity {

	@NotEmpty
	@Length(min = 5)
	String name;

	@NotEmpty
	@Min(1)
	String fileUrl;

	@NotNull
	Long uploadDate;

	@NotEmpty
	@Length(min = 10)
	String description;
}