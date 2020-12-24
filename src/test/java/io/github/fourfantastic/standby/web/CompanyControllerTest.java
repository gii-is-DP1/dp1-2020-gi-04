package io.github.fourfantastic.standby.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import io.github.fourfantastics.standby.service.exception.NotUniqueException;
import io.github.fourfantastics.standby.utils.Utils;

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
	void registerViewTest() {
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
	void registerViewUserIsPresentTest() {
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(new User()));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/register/company"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(companyService);
	}
	
	@Test
	void registerCompanyTest() throws Exception {
		final CompanyRegisterData mockCompanyRegisterData = new CompanyRegisterData();
		mockCompanyRegisterData.setName("company1");
		mockCompanyRegisterData.setEmail("company1@gmail.com");
		mockCompanyRegisterData.setPassword("password");
		mockCompanyRegisterData.setConfirmPassword("password");
		mockCompanyRegisterData.setBusinessPhone("675849765");
		mockCompanyRegisterData.setCompanyName("Company1");
		mockCompanyRegisterData.setOfficeAddress("Calle Manzanita 3");
		mockCompanyRegisterData.setTaxIDNumber("123-78-1234567");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		when(companyService.registerCompany(mockCompanyRegisterData)).then(x -> ((CompanyRegisterData) x.getArgument(0)).toCompany());
		
		mockMvc.perform(post("/register/company").with(csrf())
				.param("name", mockCompanyRegisterData.getName())
				.param("email", mockCompanyRegisterData.getEmail())
				.param("password", mockCompanyRegisterData.getPassword())
				.param("confirmPassword", mockCompanyRegisterData.getConfirmPassword())
				.param("businessPhone", mockCompanyRegisterData.getBusinessPhone())
				.param("companyName", mockCompanyRegisterData.getCompanyName())
				.param("officeAddress", mockCompanyRegisterData.getOfficeAddress())
				.param("taxIDNumber", mockCompanyRegisterData.getTaxIDNumber()))
		.andExpect(status().isFound())
		.andExpect(redirectedUrl("/"));
		
		verify(userService, times(1)).getLoggedUser(any(HttpSession.class));
		verify(companyService, only()).registerCompany(mockCompanyRegisterData);
		verifyNoMoreInteractions(companyService);
		verify(userService, times(1)).logIn(any(HttpSession.class), eq(mockCompanyRegisterData.toCompany()));
		verifyNoMoreInteractions(userService);
	}
	
	@Test
	void registerCompanyUserIsPresentTest() throws NotUniqueException {
		final CompanyRegisterData mockCompanyRegisterData = new CompanyRegisterData();
		mockCompanyRegisterData.setName("company1");
		mockCompanyRegisterData.setEmail("company1@gmail.com");
		mockCompanyRegisterData.setPassword("password");
		mockCompanyRegisterData.setConfirmPassword("password");
		mockCompanyRegisterData.setBusinessPhone("675849765");
		mockCompanyRegisterData.setCompanyName("Company1");
		mockCompanyRegisterData.setOfficeAddress("Calle Manzanita 3");
		mockCompanyRegisterData.setTaxIDNumber("123-78-1234567");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(new User()));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/register/company").with(csrf())
					.param("name", mockCompanyRegisterData.getName())
					.param("email", mockCompanyRegisterData.getEmail())
					.param("password", mockCompanyRegisterData.getPassword())
					.param("confirmPassword", mockCompanyRegisterData.getConfirmPassword())
					.param("businessPhone", mockCompanyRegisterData.getBusinessPhone())
					.param("companyName", mockCompanyRegisterData.getCompanyName())
					.param("officeAddress", mockCompanyRegisterData.getOfficeAddress())
					.param("taxIDNumber", mockCompanyRegisterData.getTaxIDNumber()))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("/"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(companyService);
	}
	
	@Test
	void registerCompanyMissingDataTest() throws NotUniqueException {
		final CompanyRegisterData mockCompanyRegisterData = new CompanyRegisterData();
		mockCompanyRegisterData.setName("");
		mockCompanyRegisterData.setEmail("company1@gmail.com");
		mockCompanyRegisterData.setPassword("password");
		mockCompanyRegisterData.setConfirmPassword("password");
		mockCompanyRegisterData.setBusinessPhone("");
		mockCompanyRegisterData.setCompanyName("Company1");
		mockCompanyRegisterData.setTaxIDNumber("123-78-1234567");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/register/company").with(csrf())
					.param("name", mockCompanyRegisterData.getName())
					.param("email", mockCompanyRegisterData.getEmail())
					.param("password", mockCompanyRegisterData.getPassword())
					.param("confirmPassword", mockCompanyRegisterData.getConfirmPassword())
					.param("businessPhone", mockCompanyRegisterData.getBusinessPhone())
					.param("companyName", mockCompanyRegisterData.getCompanyName())
					.param("officeAddress", mockCompanyRegisterData.getOfficeAddress())
					.param("taxIDNumber", mockCompanyRegisterData.getTaxIDNumber()))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("companyRegisterData"))
			.andExpect(view().name("registerCompany"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(companyService);
	}
	
	@Test
	void registerCompanyAlreadyUsedNameTest() throws NotUniqueException {
		final CompanyRegisterData mockCompanyRegisterData = new CompanyRegisterData();
		mockCompanyRegisterData.setName("company1");
		mockCompanyRegisterData.setEmail("company1@gmail.com");
		mockCompanyRegisterData.setPassword("password");
		mockCompanyRegisterData.setConfirmPassword("password");
		mockCompanyRegisterData.setBusinessPhone("675849765");
		mockCompanyRegisterData.setCompanyName("Company1");
		mockCompanyRegisterData.setOfficeAddress("Calle Manzanita 3");
		mockCompanyRegisterData.setTaxIDNumber("123-78-1234567");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		when(companyService.registerCompany(mockCompanyRegisterData)).thenThrow(new NotUniqueException("", Utils.hashSet("name")));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/register/company").with(csrf())
					.param("name", mockCompanyRegisterData.getName())
					.param("email", mockCompanyRegisterData.getEmail())
					.param("password", mockCompanyRegisterData.getPassword())
					.param("confirmPassword", mockCompanyRegisterData.getConfirmPassword())
					.param("businessPhone", mockCompanyRegisterData.getBusinessPhone())
					.param("companyName", mockCompanyRegisterData.getCompanyName())
					.param("officeAddress", mockCompanyRegisterData.getOfficeAddress())
					.param("taxIDNumber", mockCompanyRegisterData.getTaxIDNumber()))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("companyRegisterData", "name"))
			.andExpect(view().name("registerCompany"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verify(companyService, only()).registerCompany(mockCompanyRegisterData);
	}
}
