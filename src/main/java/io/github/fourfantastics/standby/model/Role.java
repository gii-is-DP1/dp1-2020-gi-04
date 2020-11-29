package io.github.fourfantastics.standby.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "filmmaker", "shortfilm" })
@NoArgsConstructor
@AllArgsConstructor
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@NotNull
	@Column(nullable = false)
	RoleType role;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	Filmmaker filmmaker;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	ShortFilm shortfilm;
}
