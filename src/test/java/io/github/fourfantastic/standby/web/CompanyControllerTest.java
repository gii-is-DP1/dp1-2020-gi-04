package io.github.fourfantastic.standby.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.form.CompanyRegisterData;
import io.github.fourfantastics.standby.service.CompanyService;
import io.github.fourfantastics.standby.service.UserService;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
@AutoConfigureMockMvc
public class CompanyControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	CompanyService companyService;

	@MockBean
	UserService userService;
	
	@Test
	void testGetRegisterView() {
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/register/company"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("companyRegisterData", new CompanyRegisterData()))
				.andExpect(view().name("registerCompany"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(companyService);
	}
	
	@Test
	void testGetRegisterUserIsPresentView() {
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(new User()));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/register/company"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(companyService);
	}
}
