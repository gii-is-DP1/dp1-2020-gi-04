package io.github.fourfantastic.standby.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.form.CompanyRegisterData;
import io.github.fourfantastics.standby.service.CompanyService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exceptions.NotUniqueException;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = StandbyApplication.class)
public class CompanyServiceTest {
	@Autowired
	CompanyService companyService;

	@Autowired
	UserService userService;

	@Test
	void registerCompanyTest() {
		final String name = "Company1";
		final String password = "patata";

		CompanyRegisterData companyRegisterData = new CompanyRegisterData();
		companyRegisterData.setName(name);
		companyRegisterData.setEmail("company1@gmail.com");
		companyRegisterData.setPassword(password);
		companyRegisterData.setConfirmPassword(password);
		companyRegisterData.setBusinessPhone("675849765");
		companyRegisterData.setCompanyName("Company1 Surname");
		companyRegisterData.setOfficeAddress("Calle Manzanita 3");
		companyRegisterData.setTaxIDNumber("123-78-1234567");

		assertDoesNotThrow(() -> {
			companyService.registerCompany(companyRegisterData);
		});

		Optional<Company> optionalCompany = this.companyService.getCompanyByName(name);
		assertTrue(optionalCompany.isPresent());
		Company company = optionalCompany.get();

		assertThat(company.getName()).isEqualTo(companyRegisterData.getName());
		assertThat(company.getEmail()).isEqualTo(companyRegisterData.getEmail());
		assertTrue(userService.getEncoder().matches(password, company.getPassword()));

		assertThat(company.getBusinessPhone()).isEqualTo(companyRegisterData.getBusinessPhone());
		assertThat(company.getCompanyName()).isEqualTo(companyRegisterData.getCompanyName());
		assertThat(company.getOfficeAddress()).isEqualTo(companyRegisterData.getOfficeAddress());
		assertThat(company.getTaxIDNumber()).isEqualTo(companyRegisterData.getTaxIDNumber());

		assertTrue(company.getComments().isEmpty());
		assertTrue(company.getFavouriteShortFilms().isEmpty());
		assertTrue(company.getFilmmakersSubscribedTo().isEmpty());
		assertTrue(company.getNotifications().isEmpty());
	}
	
	@Test
	void registerCompanyDuplicatedName() {
		final String name = "Company1";
		final String password = "patata";

		CompanyRegisterData companyRegisterData = new CompanyRegisterData();
		companyRegisterData.setName(name);
		companyRegisterData.setEmail("company1@gmail.com");
		companyRegisterData.setPassword(password);
		companyRegisterData.setConfirmPassword(password);
		companyRegisterData.setBusinessPhone("675849765");
		companyRegisterData.setCompanyName("Company1 Surname");
		companyRegisterData.setOfficeAddress("Calle Manzanita 3");
		companyRegisterData.setTaxIDNumber("123-78-1234567");
		
		assertDoesNotThrow(() -> {
			companyService.registerCompany(companyRegisterData);
		});
		
		assertThrows(NotUniqueException.class, () -> {
			companyService.registerCompany(companyRegisterData);
		});
	}
}
