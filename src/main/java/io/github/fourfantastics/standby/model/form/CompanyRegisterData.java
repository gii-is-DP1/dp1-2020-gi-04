package io.github.fourfantastics.standby.model.form;

import javax.validation.constraints.NotNull;

import io.github.fourfantastics.standby.model.Company;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CompanyRegisterData {

	@NotNull
	String name;

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
	String confirmPassword;

	public Company companyFromForm() {
		Company company = new Company();
		company.setBusinessPhone(getBusinessPhone());
		company.setCompanyName(getCompanyName());
		company.setOfficeAddress(getOfficeAddress());
		company.setPassword(getPassword());
		company.setTaxIDNumber(getTaxIDNumber());
		company.setEmail(getEmail());
		company.setName(getName());
		return company;
	}
}
