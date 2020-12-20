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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
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
import io.github.fourfantastics.standby.web.FilmmakerController;

@ContextConfiguration(classes = StandbyApplication.class)
@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = CompanyController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE), excludeAutoConfiguration = SecurityConfiguration.class)
public class CompanyControllerTest {

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


	@WithMockUser(value = "spring")
	@Test
	void testGetRegisterView() throws Exception {
		mockMvc.perform(get("/register/company")).andExpect(status().isOk())
				.andExpect(model().attributeExists("companyRegisterData")).andExpect(view().name("registerCompany"));
	}

	@WithMockUser(value = "spring")
	@Test
	void testRegisterFilmmaker() throws Exception {
		mockMvc.perform(post("/register/company"));
	}

}
