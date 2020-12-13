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

@Entity
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class PrivacyRequest {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@NotNull
	@Column(nullable = false)
	RequestStateType requestState;

	@NotNull
	@Column(nullable = false)
	Long requestDate;

	@ManyToOne(fetch = FetchType.EAGER, optional = false) // receives
	Filmmaker filmmaker;

	@ManyToOne(fetch = FetchType.EAGER, optional = false) // send
	Company company;
}
