package io.github.fourfantastics.standby.model;

import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
public class PrivacyRequest{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;
	
	@NotNull
	@Column(nullable = false)
	RequestStateType requestState;
	
	@NotNull
	@Column(nullable = false)
	Long requestDate;
	
	/*@ManyToOne(optional=false)//receives
	@JoinColumn(name = "filmmaker_id", referencedColumnName = "id")
	Filmmaker filmmaker;
	
	@ManyToOne(optional=false)//send
	@JoinColumn(name = "company_id", referencedColumnName = "id")
	Company company;*/
}
