package io.github.fourfantastics.standby.model.form;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import io.github.fourfantastics.standby.model.Company;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CompanyData {
	@NotNull
	String companyName;

	@NotNull
	Integer taxIDNumber;

	@NotNull
	String businessPhone;

	@NotNull
	String officeAddress;
	
	@NotNull
	Boolean byPrivacyRequests;
	
	public static CompanyData fromCompany(Company company) {
		CompanyData companyData= new CompanyData();
		companyData.setBusinessPhone(company.getBusinessPhone());
		companyData.setCompanyName(company.getCompanyName());
		companyData.setOfficeAddress(company.getOfficeAddress());
		companyData.setTaxIDNumber(company.getTaxIDNumber());
		companyData.setByPrivacyRequests(company.getConfiguration().getByPrivacyRequests());
		return companyData;
	}
	
	public void copyToCompany(Company company) {
		company.setBusinessPhone(getBusinessPhone());
		company.setCompanyName(getCompanyName());
		company.setOfficeAddress(getOfficeAddress());
		company.setTaxIDNumber(getTaxIDNumber());
		company.getConfiguration().setByPrivacyRequests(getByPrivacyRequests());
	}

}
