package io.github.fourfantastics.standby.model;

import javax.persistence.Column;

import javax.persistence.Entity;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor

public class Rating extends BaseEntity{
	
	
	@NotNull
	@Column(nullable = false)
	@Range(min=0,max=10)
	Integer grade;

}
