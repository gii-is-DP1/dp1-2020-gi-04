package io.github.fourfantastic.standby.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.model.form.CompanyRegisterData;
import io.github.fourfantastics.standby.repository.CompanyRepository;
import io.github.fourfantastics.standby.service.CompanyService;
import io.github.fourfantastics.standby.service.NotificationConfigurationService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.NotUniqueException;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class CompanyServiceTest {
	CompanyService companyService;

	@Mock
	CompanyRepository companyRepository;
	
	@Mock
	UserService userService;
	
	@Mock
	NotificationConfigurationService notificationConfigurationService;

	@BeforeEach
	public void setup() throws NotUniqueException {
		companyService = new CompanyService(companyRepository, userService, notificationConfigurationService);
		
		when(userService.register(any(Company.class))).then(AdditionalAnswers.returnsFirstArg());
		when(notificationConfigurationService.saveNotificationConfiguration(any(NotificationConfiguration.class)))
			.then(AdditionalAnswers.returnsFirstArg());
		when(userService.saveUser(any(Company.class))).then(AdditionalAnswers.returnsFirstArg());
	}
	
	@Test
	void registerCompanyTest() {
		CompanyRegisterData companyRegisterData = new CompanyRegisterData();
		companyRegisterData.setName("company1");
		companyRegisterData.setEmail("company1@gmail.com");
		companyRegisterData.setPassword("password");
		companyRegisterData.setConfirmPassword("password");
		companyRegisterData.setBusinessPhone("675849765");
		companyRegisterData.setCompanyName("Company1");
		companyRegisterData.setOfficeAddress("Calle Manzanita 3");
		companyRegisterData.setTaxIDNumber("123-78-1234567");
		
		assertDoesNotThrow(() -> {
			Company company = companyService.registerCompany(companyRegisterData);
			
			assertThat(company.getName()).isEqualTo(companyRegisterData.getName());
			assertThat(company.getEmail()).isEqualTo(companyRegisterData.getEmail());
			assertThat(company.getPassword()).isEqualTo(companyRegisterData.getPassword());

			assertThat(company.getBusinessPhone()).isEqualTo(companyRegisterData.getBusinessPhone());
			assertThat(company.getCompanyName()).isEqualTo(companyRegisterData.getCompanyName());
			assertThat(company.getOfficeAddress()).isEqualTo(companyRegisterData.getOfficeAddress());
			assertThat(company.getTaxIDNumber()).isEqualTo(companyRegisterData.getTaxIDNumber());

			assertTrue(company.getComments().isEmpty());
			assertTrue(company.getFavouriteShortFilms().isEmpty());
			assertTrue(company.getFilmmakersSubscribedTo().isEmpty());
			assertTrue(company.getNotifications().isEmpty());
			
			verify(userService, times(1)).register(companyRegisterData.toCompany());
			verify(notificationConfigurationService, only()).saveNotificationConfiguration(any(NotificationConfiguration.class));
			verify(userService, times(1)).saveUser(companyRegisterData.toCompany());
			verifyNoMoreInteractions(userService);
		});
	}
}
