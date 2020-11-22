package io.github.fourfantastics.standby.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	
	@NotNull
	@NotEmpty
	@Length(max= 1000)
	@Column(nullable = false)
	String comment;
	
	@NotNull
	Long date;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id" , referencedColumnName= "id")
	User user;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "shortfilm_id" , referencedColumnName= "id")
	ShortFilm shortFilm;
	
}
