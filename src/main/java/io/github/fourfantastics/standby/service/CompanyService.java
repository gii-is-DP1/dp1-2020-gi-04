package io.github.fourfantastics.standby.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.model.form.CompanyRegisterData;
import io.github.fourfantastics.standby.repository.CompanyRepository;
import io.github.fourfantastics.standby.service.exceptions.NotUniqueException;

@Service
public class CompanyService {
	CompanyRepository companyRepository;
	UserService userService;
	NotificationConfigurationService configurationService;
	
	@Autowired
	public CompanyService(CompanyRepository companyRepository, UserService userService,
			NotificationConfigurationService configurationService) {
		this.companyRepository = companyRepository;
		this.userService = userService;
		this.configurationService = configurationService;
	}

	public Optional<Company> getCompanyById(Long id) {
		return companyRepository.findById(id);
	}

	public Optional<Company> getCompanyByName(String name) {
		return companyRepository.findByName(name);
	}
	
	public void saveNotification(Company company) {
		companyRepository.save(company);
	}

	public Company registerCompany(CompanyRegisterData companyRegisterData)
			throws NotUniqueException {
		Company company = companyRegisterData.companyFromForm();
		company = (Company) userService.register(company);

		NotificationConfiguration configuration = new NotificationConfiguration();
		configuration.setUser(company);
		configuration.setByComments(false);
		configuration.setByRatings(false);
		configuration.setBySubscriptions(false);
		configuration = configurationService.saveNotificationConfiguration(configuration);

		company.setConfiguration(configuration);
		userService.saveUser(company);
		return company;
	}
}
