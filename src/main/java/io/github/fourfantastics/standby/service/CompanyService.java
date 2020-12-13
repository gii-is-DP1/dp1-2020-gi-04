package io.github.fourfantastics.standby.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.model.form.CompanyRegisterData;
import io.github.fourfantastics.standby.repository.CompanyRepository;
import io.github.fourfantastics.standby.service.exceptions.DataMismatchException;
import io.github.fourfantastics.standby.service.exceptions.NotUniqueException;
import io.github.fourfantastics.standby.utils.Utils;

@Service
public class CompanyService {
	@Autowired
	CompanyRepository companyRepository;

	@Autowired
	UserService userService;

	@Autowired
	NotificationConfigurationService configurationService;

	public Optional<Company> getNotificationById(Long id) {
		return companyRepository.findById(id);
	}

	public void saveNotification(Company company) {
		companyRepository.save(company);
	}

	public Company registerCompany(CompanyRegisterData companyRegisterData)
			throws DataMismatchException, NotUniqueException {

		if (!companyRegisterData.getPassword().equals(companyRegisterData.getConfirmPassword())) {
			throw new DataMismatchException("The password doesn't match", Utils.hashSet("password"));
		}

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
