package io.github.fourfantastics.standby.model.form;

import java.util.Set;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CompanyProfileData {

	String photoUrl;

	String companyName;

	Set<Filmmaker> filmmakersSubscribedTo;

	public static CompanyProfileData fromCompany(Company company) {
		CompanyProfileData companyProfileData = new CompanyProfileData();
		companyProfileData.setPhotoUrl(company.getPhotoUrl());
		companyProfileData.setCompanyName(company.getCompanyName());
		return companyProfileData;
	}
}
