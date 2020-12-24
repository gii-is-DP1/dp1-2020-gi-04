package io.github.fourfantastic.standby.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
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
import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.form.CompanyConfigurationData;
import io.github.fourfantastics.standby.model.form.Credentials;
import io.github.fourfantastics.standby.model.form.FilmmakerConfigurationData;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.DataMismatchException;
import io.github.fourfantastics.standby.service.exception.NotFoundException;
import io.github.fourfantastics.standby.utils.Utils;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	UserService userService;

	@Test
	void loginViewTest() {
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/login")).andExpect(status().isOk())
					.andExpect(model().attribute("credentials", new Credentials()))
					.andExpect(view().name("login.html"));
		});
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
	}

	@Test
	void loginUserIsLoggedViewTest() {
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(new User()));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/login")).andExpect(status().isFound()).andExpect(redirectedUrl("/"));
		});

		verify(userService, only()).getLoggedUser(any(HttpSession.class));
	}

	@Test
	void logInTest() throws NotFoundException, DataMismatchException {
		final Credentials mockCredentials = new Credentials();
		mockCredentials.setName("filmmmaker1");
		mockCredentials.setPassword("password");

		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		when(userService.authenticate(anyString(), anyString())).thenReturn(new User());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/login").with(csrf()).param("name", mockCredentials.getName()).param("password",
					mockCredentials.getPassword())).andExpect(status().isFound()).andExpect(redirectedUrl("/"));
		});

		verify(userService, times(1)).getLoggedUser(any(HttpSession.class));
		verify(userService, times(1)).authenticate(mockCredentials.getName(), mockCredentials.getPassword());
		verify(userService, times(1)).logIn(any(HttpSession.class), eq(new User()));
		verifyNoMoreInteractions(userService);
	}

	@Test
	void logInUserNotFoundTest() throws NotFoundException, DataMismatchException {
		final Credentials mockCredentials = new Credentials();
		mockCredentials.setName("inventedName");
		mockCredentials.setPassword("password");

		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		when(userService.authenticate(anyString(), anyString()))
				.thenThrow(new NotFoundException("", Utils.hashSet("name")));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/login").with(csrf()).param("name", mockCredentials.getName()).param("password",
					mockCredentials.getPassword())).andExpect(status().isOk()).andExpect(view().name("login.html"));
		});

		verify(userService, times(1)).getLoggedUser(any(HttpSession.class));
		verify(userService, times(1)).authenticate(mockCredentials.getName(), mockCredentials.getPassword());
		verifyNoMoreInteractions(userService);
	}

	@Test
	void logInPasswordDoNotMatchTest() throws DataMismatchException, NotFoundException {
		final Credentials mockCredentials = new Credentials();
		mockCredentials.setName("filmmaker");
		mockCredentials.setPassword("wrong password");

		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		when(userService.authenticate(anyString(), anyString()))
				.thenThrow(new DataMismatchException("", Utils.hashSet("password")));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/login").with(csrf()).param("name", mockCredentials.getName()).param("password",
					mockCredentials.getPassword())).andExpect(status().isOk()).andExpect(view().name("login.html"));
		});

		verify(userService, times(1)).getLoggedUser(any(HttpSession.class));
		verify(userService, times(1)).authenticate(mockCredentials.getName(), mockCredentials.getPassword());
		verifyNoMoreInteractions(userService);
	}

	@Test
	void logInValidatorMissingData() {
		final Credentials mockCredentials = new Credentials();
		mockCredentials.setName("");
		mockCredentials.setPassword("wrong password");

		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/login").with(csrf()).param("name", mockCredentials.getName()).param("password",
					mockCredentials.getPassword())).andExpect(status().isOk()).andExpect(view().name("login.html"));
		});

		verify(userService, only()).getLoggedUser(any(HttpSession.class));
	}

	@Test
	void logInUserIsLogged() {
		final Credentials mockCredentials = new Credentials();
		mockCredentials.setName("filmmaker");
		mockCredentials.setPassword("wrong password");

		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(new User()));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/login").with(csrf()).param("name", mockCredentials.getName()).param("password",
					mockCredentials.getPassword())).andExpect(status().isFound()).andExpect(redirectedUrl("/"));
		});

		verify(userService, only()).getLoggedUser(any(HttpSession.class));
	}

	@Test
	void manageAccountFilmmakerView() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setFullname("Filmmaker1");
		mockFilmmaker.setCountry("Spain");
		mockFilmmaker.setCity("Seville");
		mockFilmmaker.setPhone("678543167");
		mockFilmmaker.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(mockFilmmaker));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/account")).andExpect(status().isOk())
					.andExpect(model().attribute("filmmakerConfigurationData",
							FilmmakerConfigurationData.fromFilmmaker(mockFilmmaker)))
					.andExpect(view().name("manageFilmmakerAccount"));
		});

		verify(userService, only()).getLoggedUser(any(HttpSession.class));
	}
	
	@Test
	void manageAccountCompanyView() {
		final Company mockCompany = new Company();
		mockCompany.setBusinessPhone("675849765");
		mockCompany.setCompanyName("Company1");
		mockCompany.setOfficeAddress("Calle Manzanita 3");
		mockCompany.setTaxIDNumber("123-78-1234567");
		mockCompany.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(mockCompany));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/account"))
					.andExpect(status().isOk())
					.andExpect(model().attribute("companyConfigurationData",
							CompanyConfigurationData.fromCompany(mockCompany)))
					.andExpect(view().name("manageCompanyAccount"));
		});

		verify(userService, only()).getLoggedUser(any(HttpSession.class));
	}
	
	@Test
	void manageAccountUserIsNotLoggedView() {
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/account"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/login"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
	}
	
	@Test
	void manageAccountFilmmaker() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setFullname("Filmmaker1");
		mockFilmmaker.setCountry("Spain");
		mockFilmmaker.setCity("Seville");
		mockFilmmaker.setPhone("678543167");
		mockFilmmaker.setConfiguration(new NotificationConfiguration());

		final FilmmakerConfigurationData mockConfigFilmmaker = new FilmmakerConfigurationData();
		mockConfigFilmmaker.setByComments(false);
		mockConfigFilmmaker.setByRatings(true);
		mockConfigFilmmaker.setBySubscriptions(true);
		mockConfigFilmmaker.setCity("Huelva");
		mockConfigFilmmaker.setCountry("Spain");
		mockConfigFilmmaker.setFullname("Filmmaker1");
		mockConfigFilmmaker.setPhone("616449997");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(mockFilmmaker));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/account/filmmaker").with(csrf())
					.param("fullname", mockConfigFilmmaker.getFullname())
					.param("country", mockConfigFilmmaker.getCountry())
					.param("city", mockConfigFilmmaker.getCity())
					.param("phone", mockConfigFilmmaker.getPhone())
					.param("byComments", mockConfigFilmmaker.getByComments().toString())
					.param("byRatings", mockConfigFilmmaker.getByRatings().toString())
					.param("bySubscriptions", mockConfigFilmmaker.getBySubscriptions().toString()))
					.andExpect(status().isOk())
					.andExpect(view().name("manageFilmmakerAccount"));
		});
		
		verify(userService, times(1)).getLoggedUser(any(HttpSession.class));
		mockConfigFilmmaker.copyToFilmmaker(mockFilmmaker);
		verify(userService, times(1)).saveUser(mockFilmmaker);
		verifyNoMoreInteractions(userService);
	}
	
	@Test
	void manageAccountFilmmakerNotLogged() {
		final FilmmakerConfigurationData mockConfigFilmmaker = new FilmmakerConfigurationData();
		mockConfigFilmmaker.setByComments(false);
		mockConfigFilmmaker.setByRatings(true);
		mockConfigFilmmaker.setBySubscriptions(true);
		mockConfigFilmmaker.setCity("Huelva");
		mockConfigFilmmaker.setCountry("Spain");
		mockConfigFilmmaker.setFullname("Filmmaker1");
		mockConfigFilmmaker.setPhone("616449997");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/account/filmmaker").with(csrf())
					.param("fullname", mockConfigFilmmaker.getFullname())
					.param("country", mockConfigFilmmaker.getCountry())
					.param("city", mockConfigFilmmaker.getCity())
					.param("phone", mockConfigFilmmaker.getPhone())
					.param("byComments", mockConfigFilmmaker.getByComments().toString())
					.param("byRatings", mockConfigFilmmaker.getByRatings().toString())
					.param("bySubscriptions", mockConfigFilmmaker.getBySubscriptions().toString()))
					.andExpect(status().isFound())
					.andExpect(redirectedUrl("/login"));
		});	
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
	}
	
	@Test
	void manageAccountFilmmakerMissingData() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setFullname("Filmmaker1");
		mockFilmmaker.setCountry("Spain");
		mockFilmmaker.setCity("Seville");
		mockFilmmaker.setPhone("678543167");
		mockFilmmaker.setConfiguration(new NotificationConfiguration());

		final FilmmakerConfigurationData mockConfigFilmmaker = new FilmmakerConfigurationData();
		mockConfigFilmmaker.setByComments(false);
		mockConfigFilmmaker.setByRatings(true);
		mockConfigFilmmaker.setBySubscriptions(true);
		mockConfigFilmmaker.setCity("");
		mockConfigFilmmaker.setCountry("");
		mockConfigFilmmaker.setFullname("Filmmaker1");
		mockConfigFilmmaker.setPhone("616449997");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(mockFilmmaker));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/account/filmmaker").with(csrf())
					.param("fullname", mockConfigFilmmaker.getFullname())
					.param("country", mockConfigFilmmaker.getCountry())
					.param("city", mockConfigFilmmaker.getCity())
					.param("phone", mockConfigFilmmaker.getPhone())
					.param("byComments", mockConfigFilmmaker.getByComments().toString())
					.param("byRatings", mockConfigFilmmaker.getByRatings().toString())
					.param("bySubscriptions", mockConfigFilmmaker.getBySubscriptions().toString()))
					.andExpect(status().isOk())
					.andExpect(view().name("manageFilmmakerAccount"));
		});	
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
	}
	
	@Test
	void ManageAccountFilmmakerAsCompany() {
		final Company mockCompany = new Company();
		mockCompany.setBusinessPhone("675849765");
		mockCompany.setCompanyName("Company1");
		mockCompany.setOfficeAddress("Calle Manzanita 3");
		mockCompany.setTaxIDNumber("123-78-1234567");
		mockCompany.setConfiguration(new NotificationConfiguration());
		
		final FilmmakerConfigurationData mockConfigFilmmaker = new FilmmakerConfigurationData();
		mockConfigFilmmaker.setByComments(false);
		mockConfigFilmmaker.setByRatings(true);
		mockConfigFilmmaker.setBySubscriptions(true);
		mockConfigFilmmaker.setCity("");
		mockConfigFilmmaker.setCountry("");
		mockConfigFilmmaker.setFullname("Filmmaker1");
		mockConfigFilmmaker.setPhone("616449997");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(mockCompany));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/account/filmmaker").with(csrf())
					.param("fullname", mockConfigFilmmaker.getFullname())
					.param("country", mockConfigFilmmaker.getCountry())
					.param("city", mockConfigFilmmaker.getCity())
					.param("phone", mockConfigFilmmaker.getPhone())
					.param("byComments", mockConfigFilmmaker.getByComments().toString())
					.param("byRatings", mockConfigFilmmaker.getByRatings().toString())
					.param("bySubscriptions", mockConfigFilmmaker.getBySubscriptions().toString()))
					.andExpect(status().isFound())
					.andExpect(redirectedUrl("/manageAccount"));
		});	
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
	}
	
	@Test
	void ManageAccountCompany() {
		final Company mockCompany = new Company();
		mockCompany.setBusinessPhone("675849765");
		mockCompany.setCompanyName("Company1");
		mockCompany.setOfficeAddress("Calle Manzanita 3");
		mockCompany.setTaxIDNumber("123-78-1234567");
		mockCompany.setConfiguration(new NotificationConfiguration());	
		
		final CompanyConfigurationData mockConfigCompany = new CompanyConfigurationData();
		mockConfigCompany.setBusinessPhone("675849765");
		mockConfigCompany.setByPrivacyRequests(false);
		mockConfigCompany.setCompanyName("Company2");
		mockConfigCompany.setOfficeAddress("Apple street 3");
		mockConfigCompany.setTaxIDNumber("123-78-1234567");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(mockCompany));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/account/company").with(csrf())
					.param("companyName", mockConfigCompany.getCompanyName())
					.param("taxIDNumber", mockConfigCompany.getTaxIDNumber())
					.param("businessPhone", mockConfigCompany.getBusinessPhone())
					.param("officeAddress", mockConfigCompany.getOfficeAddress())
					.param("byPrivacyRequests", mockConfigCompany.getByPrivacyRequests().toString()))
					.andExpect(status().isOk())
					.andExpect(view().name("manageCompanyAccount"));
		});
		
		verify(userService, times(1)).getLoggedUser(any(HttpSession.class));
		mockConfigCompany.copyToCompany(mockCompany);
		verify(userService, times(1)).saveUser(mockCompany);
		verifyNoMoreInteractions(userService);
	}
	
	@Test
	void manageAccountCompanyNotLogged() {
		final CompanyConfigurationData mockConfigCompany = new CompanyConfigurationData();
		mockConfigCompany.setBusinessPhone("675849765");
		mockConfigCompany.setByPrivacyRequests(false);
		mockConfigCompany.setCompanyName("Company2");
		mockConfigCompany.setOfficeAddress("Apple street 3");
		mockConfigCompany.setTaxIDNumber("123-78-1234567");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/account/company").with(csrf())
					.param("companyName", mockConfigCompany.getCompanyName())
					.param("taxIDNumber", mockConfigCompany.getTaxIDNumber())
					.param("businessPhone", mockConfigCompany.getBusinessPhone())
					.param("officeAddress", mockConfigCompany.getOfficeAddress())
					.param("byPrivacyRequests", mockConfigCompany.getByPrivacyRequests().toString()))
					.andExpect(status().isFound())
					.andExpect(redirectedUrl("/login"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
	}
	
	@Test
	void manageAccountCompanyMissingData() {
		final Company mockCompany = new Company();
		mockCompany.setBusinessPhone("675849765");
		mockCompany.setCompanyName("Company1");
		mockCompany.setOfficeAddress("Calle Manzanita 3");
		mockCompany.setTaxIDNumber("123-78-1234567");
		mockCompany.setConfiguration(new NotificationConfiguration());	
		
		final CompanyConfigurationData mockConfigCompany = new CompanyConfigurationData();
		mockConfigCompany.setBusinessPhone("675849765");
		mockConfigCompany.setByPrivacyRequests(false);
		mockConfigCompany.setCompanyName("");
		mockConfigCompany.setOfficeAddress("Apple street 3");
		mockConfigCompany.setTaxIDNumber("");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(mockCompany));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/account/company").with(csrf())
					.param("companyName", mockConfigCompany.getCompanyName())
					.param("taxIDNumber", mockConfigCompany.getTaxIDNumber())
					.param("businessPhone", mockConfigCompany.getBusinessPhone())
					.param("officeAddress", mockConfigCompany.getOfficeAddress())
					.param("byPrivacyRequests", mockConfigCompany.getByPrivacyRequests().toString()))
					.andExpect(status().isOk())
					.andExpect(view().name("manageCompanyAccount"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
	}
	
	@Test
	void manageAccountCompanyAsFilmmaker() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setFullname("Filmmaker1");
		mockFilmmaker.setCountry("Spain");
		mockFilmmaker.setCity("Seville");
		mockFilmmaker.setPhone("678543167");
		mockFilmmaker.setConfiguration(new NotificationConfiguration());
		
		final CompanyConfigurationData mockConfigCompany = new CompanyConfigurationData();
		mockConfigCompany.setBusinessPhone("675849765");
		mockConfigCompany.setByPrivacyRequests(false);
		mockConfigCompany.setCompanyName("Company2");
		mockConfigCompany.setOfficeAddress("Apple street 3");
		mockConfigCompany.setTaxIDNumber("123-78-1234567");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(mockFilmmaker));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/account/company").with(csrf())
					.param("companyName", mockConfigCompany.getCompanyName())
					.param("taxIDNumber", mockConfigCompany.getTaxIDNumber())
					.param("businessPhone", mockConfigCompany.getBusinessPhone())
					.param("officeAddress", mockConfigCompany.getOfficeAddress())
					.param("byPrivacyRequests", mockConfigCompany.getByPrivacyRequests().toString()))
					.andExpect(status().isFound())
					.andExpect(redirectedUrl("/manageAccount"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
	}
}
