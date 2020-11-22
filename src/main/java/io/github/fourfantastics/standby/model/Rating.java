package io.github.fourfantastics.standby.model;

import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	
	@NotNull
	@Column(nullable = false)
	@Range(min=0,max=10)
	Integer grade;
	
	@NotNull
	@Column(nullable = false)
	Long date;
	
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	User user;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "shortFilm_id", referencedColumnName = "id")
	ShortFilm shortFilm;

}
