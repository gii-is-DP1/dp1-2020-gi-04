package io.github.fourfantastic.standby.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.configuration.SecurityConfiguration;
import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.form.CompanyRegisterData;
import io.github.fourfantastics.standby.model.validator.CompanyRegisterDataValidator;
import io.github.fourfantastics.standby.service.CompanyService;
import io.github.fourfantastics.standby.service.NotificationConfigurationService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exceptions.DataMismatchException;
import io.github.fourfantastics.standby.service.exceptions.NotUniqueException;
import io.github.fourfantastics.standby.web.CompanyController;

@ContextConfiguration(classes = StandbyApplication.class)
@WebMvcTest(controllers = CompanyController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class CompanyControllerTest {

	@SuppressWarnings("unused")
	@Autowired
	private CompanyController companyController;

	@MockBean
	private CompanyService companyService;

	@MockBean
	private UserService userService;

	@MockBean
	private NotificationConfigurationService notificationConfigurationService;

	@MockBean
	private CompanyRegisterDataValidator companyRegisterDataValidator;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setup() throws DataMismatchException, NotUniqueException {

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

		given(this.companyService.registerCompany(companyRegisterData)).willReturn(company);
	}

	@WithMockUser(value = "spring")
	@Test
	void testGetRegisterView() throws Exception {
		mockMvc.perform(get("/register/company")).andExpect(status().isOk())
				.andExpect(model().attributeExists("companyRegisterData")).andExpect(view().name("registerCompany"));
	}

	@WithMockUser(value = "spring")
	@Test
	void testRegisterFilmmaker() throws Exception {
		mockMvc.perform(post("/register/company").param("type", "Company").param("name", "Company4")
				.param("email", "company4@gmail.com").param("password", "patataa").param("creationDate", "12/12/2020")
				.param("photoUrl", "url photo").with(csrf()).param("companyName", "Company4 Studios")
				.param("taxIDNumber", "123-98-1674567").param("businessPhone", "685493865")
				.param("officeAddress", "Calle Manzana 1")).andExpect(status().is3xxRedirection());
	}

}
