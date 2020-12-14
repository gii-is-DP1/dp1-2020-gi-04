package io.github.fourfantastics.standby.model.form;

import javax.validation.constraints.NotNull;

import io.github.fourfantastics.standby.model.Company;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CompanyConfigurationData {
	@NotNull
	String companyName;

	@NotNull
	String taxIDNumber;

	@NotNull
	String businessPhone;

	@NotNull
	String officeAddress;

	@NotNull
	Boolean byPrivacyRequests;

	public static CompanyConfigurationData fromCompany(Company company) {
		CompanyConfigurationData companyConfigurationData = new CompanyConfigurationData();
		companyConfigurationData.setBusinessPhone(company.getBusinessPhone());
		companyConfigurationData.setCompanyName(company.getCompanyName());
		companyConfigurationData.setOfficeAddress(company.getOfficeAddress());
		companyConfigurationData.setTaxIDNumber(company.getTaxIDNumber());
		companyConfigurationData.setByPrivacyRequests(company.getConfiguration().getByPrivacyRequests());
		return companyConfigurationData;
	}

	public void copyToCompany(Company company) {
		company.setBusinessPhone(getBusinessPhone());
		company.setCompanyName(getCompanyName());
		company.setOfficeAddress(getOfficeAddress());
		company.setTaxIDNumber(getTaxIDNumber());
		company.getConfiguration().setByPrivacyRequests(getByPrivacyRequests());
	}
}
