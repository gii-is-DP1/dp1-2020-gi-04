package io.github.fourfantastics.standby.model.form;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import io.github.fourfantastics.standby.model.Company;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CompanyRegisterData {
	
	@NotNull
	String companyName;
	
	@NotNull
	String taxIDNumber;
	
	@NotNull
	String businessPhone;

	@NotNull
	String officeAddress;
	
	@NotNull
	String email;

	@NotNull
	String password;
	
	@NotNull
	String passwordConfirmation;
	

}
