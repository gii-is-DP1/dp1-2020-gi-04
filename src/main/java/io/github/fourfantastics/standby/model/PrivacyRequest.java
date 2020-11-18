package io.github.fourfantastics.standby.model;

import javax.persistence.Column;

import javax.persistence.Entity;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class PrivacyRequest extends BaseEntity{
	
	
	@NotNull
	@Column(nullable = false)
	String requestState;
	
	@NotNull
	@Column(nullable = false)
	Long requestDate;

}
