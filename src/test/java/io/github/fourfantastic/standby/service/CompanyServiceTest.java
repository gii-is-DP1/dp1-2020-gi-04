package io.github.fourfantastic.standby.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.form.CompanyRegisterData;
import io.github.fourfantastics.standby.service.CompanyService;
import io.github.fourfantastics.standby.service.exceptions.DataMismatchException;
import io.github.fourfantastics.standby.service.exceptions.NotUniqueException;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = StandbyApplication.class)
public class CompanyServiceTest {
	@Autowired
	protected CompanyService companyService;

	@Test
	void getNotificationById() {
		Optional<Company> company1 = this.companyService.getCompanyById(2L);
		assertThat(company1.isPresent()).isEqualTo(true);

		Optional<Company> company2 = this.companyService.getCompanyById(84L);
		assertThat(company2.isPresent()).isEqualTo(false);
	}

	@Test
	void registerCompanyTest() throws DataMismatchException, NotUniqueException {
		CompanyRegisterData companyRegisterData = new CompanyRegisterData();
		companyRegisterData.setBusinessPhone("675849765");
		companyRegisterData.setCompanyName("Company1 Surname");
		companyRegisterData.setName("Company1");
		companyRegisterData.setEmail("company1@gmail.com");
		companyRegisterData.setOfficeAddress("Calle Manzanita 3");
		companyRegisterData.setTaxIDNumber("123-78-1234567");
		companyRegisterData.setPassword("patata");
		companyRegisterData.setConfirmPassword("patata");

		Company company = this.companyService.registerCompany(companyRegisterData);

		Optional<Company> company1 = this.companyService.getCompanyById(company.getId());

		assertThat(company1.get().getName()).isEqualTo("Company1");
		assertThat(company1.get().getOfficeAddress()).doesNotMatch("Calle Manzanita 4");
		assertThat(company1.get().getComments().isEmpty()).isEqualTo(true);

	}
}
