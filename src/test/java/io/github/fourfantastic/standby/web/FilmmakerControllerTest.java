package io.github.fourfantastic.standby.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.form.FilmmakerConfigurationData;
import io.github.fourfantastics.standby.model.form.FilmmakerRegisterData;
import io.github.fourfantastics.standby.service.FilmmakerService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.InvalidExtensionException;
import io.github.fourfantastics.standby.service.exception.NotUniqueException;
import io.github.fourfantastics.standby.service.exception.TooBigException;
import io.github.fourfantastics.standby.utils.Utils;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
@AutoConfigureMockMvc
public class FilmmakerControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	FilmmakerService filmmakerService;

	@MockBean
	UserService userService;
	
	@Test
	void registerViewTest() {
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/register/filmmaker"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("filmmakerRegisterData", new FilmmakerRegisterData()))
				.andExpect(view().name("registerFilmmaker"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(filmmakerService);
	}
	
	@Test
	void registerViewUserIsPresentTest() {
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(new User()));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/register/filmmaker"))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(filmmakerService);
	}
	
	@Test
	void registerFilmmakerTest() throws Exception {
		final FilmmakerRegisterData mockFilmmakerRegisterData = new FilmmakerRegisterData();
		mockFilmmakerRegisterData.setName("filmmaker1");
		mockFilmmakerRegisterData.setEmail("filmmaker1@gmail.com");
		mockFilmmakerRegisterData.setPassword("password");
		mockFilmmakerRegisterData.setConfirmPassword("password");
		mockFilmmakerRegisterData.setFullname("Filmmaker1");
		mockFilmmakerRegisterData.setCountry("Spain");
		mockFilmmakerRegisterData.setCity("Seville");
		mockFilmmakerRegisterData.setPhone("678543167");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		when(filmmakerService.registerFilmmaker(mockFilmmakerRegisterData)).then(x -> ((FilmmakerRegisterData) x.getArgument(0)).toFilmmaker());
		
		mockMvc.perform(post("/register/filmmaker").with(csrf())
				.param("name", mockFilmmakerRegisterData.getName())
				.param("email", mockFilmmakerRegisterData.getEmail())
				.param("password", mockFilmmakerRegisterData.getPassword())
				.param("confirmPassword", mockFilmmakerRegisterData.getConfirmPassword())
				.param("fullname", mockFilmmakerRegisterData.getFullname())
				.param("country", mockFilmmakerRegisterData.getCountry())
				.param("city", mockFilmmakerRegisterData.getCity())
				.param("phone", mockFilmmakerRegisterData.getPhone()))
		.andExpect(status().isFound())
		.andExpect(redirectedUrl("/"));
		
		verify(userService, times(1)).getLoggedUser(any(HttpSession.class));
		verify(filmmakerService, only()).registerFilmmaker(mockFilmmakerRegisterData);
		verifyNoMoreInteractions(filmmakerService);
		verify(userService, times(1)).logIn(any(HttpSession.class), eq(mockFilmmakerRegisterData.toFilmmaker()));
		verifyNoMoreInteractions(userService);
	}
	
	@Test
	void registerFilmmakerUserIsPresentTest() throws NotUniqueException {
		final FilmmakerRegisterData mockFilmmakerRegisterData = new FilmmakerRegisterData();
		mockFilmmakerRegisterData.setName("filmmaker1");
		mockFilmmakerRegisterData.setEmail("filmmaker1@gmail.com");
		mockFilmmakerRegisterData.setPassword("password");
		mockFilmmakerRegisterData.setConfirmPassword("password");
		mockFilmmakerRegisterData.setFullname("Filmmaker1");
		mockFilmmakerRegisterData.setCountry("Spain");
		mockFilmmakerRegisterData.setCity("Seville");
		mockFilmmakerRegisterData.setPhone("678543167");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(new User()));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/register/filmmaker").with(csrf())
					.param("name", mockFilmmakerRegisterData.getName())
					.param("email", mockFilmmakerRegisterData.getEmail())
					.param("password", mockFilmmakerRegisterData.getPassword())
					.param("confirmPassword", mockFilmmakerRegisterData.getConfirmPassword())
					.param("fullname", mockFilmmakerRegisterData.getFullname())
					.param("country", mockFilmmakerRegisterData.getCountry())
					.param("city", mockFilmmakerRegisterData.getCity())
					.param("phone", mockFilmmakerRegisterData.getPhone()))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("/"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(filmmakerService);
	}
	
	@Test
	void registerFilmmakerMissingDataTest() throws NotUniqueException {
		final FilmmakerRegisterData mockFilmmakerRegisterData = new FilmmakerRegisterData();
		mockFilmmakerRegisterData.setName("");
		mockFilmmakerRegisterData.setEmail("filmmaker1@gmail.com");
		mockFilmmakerRegisterData.setPassword("password");
		mockFilmmakerRegisterData.setConfirmPassword("");
		mockFilmmakerRegisterData.setFullname("Filmmaker1");
		mockFilmmakerRegisterData.setCountry("Spain");
		mockFilmmakerRegisterData.setCity("Seville");
		mockFilmmakerRegisterData.setPhone("678543167");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/register/filmmaker").with(csrf())
					.param("name", mockFilmmakerRegisterData.getName())
					.param("email", mockFilmmakerRegisterData.getEmail())
					.param("password", mockFilmmakerRegisterData.getPassword())
					.param("confirmPassword", mockFilmmakerRegisterData.getConfirmPassword())
					.param("fullname", mockFilmmakerRegisterData.getFullname())
					.param("country", mockFilmmakerRegisterData.getCountry())
					.param("city", mockFilmmakerRegisterData.getCity())
					.param("phone", mockFilmmakerRegisterData.getPhone()))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("filmmakerRegisterData"))
			.andExpect(view().name("registerFilmmaker"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(filmmakerService);
	}
	
	@Test
	void registerFilmmakerAlreadyUsedNameTest() throws NotUniqueException {
		final FilmmakerRegisterData mockFilmmakerRegisterData = new FilmmakerRegisterData();
		mockFilmmakerRegisterData.setName("filmmaker1");
		mockFilmmakerRegisterData.setEmail("filmmaker1@gmail.com");
		mockFilmmakerRegisterData.setPassword("password");
		mockFilmmakerRegisterData.setConfirmPassword("password");
		mockFilmmakerRegisterData.setFullname("Filmmaker1");
		mockFilmmakerRegisterData.setCountry("Spain");
		mockFilmmakerRegisterData.setCity("Seville");
		mockFilmmakerRegisterData.setPhone("678543167");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		when(filmmakerService.registerFilmmaker(mockFilmmakerRegisterData)).thenThrow(new NotUniqueException("", Utils.hashSet("name")));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/register/filmmaker").with(csrf())
					.param("name", mockFilmmakerRegisterData.getName())
					.param("email", mockFilmmakerRegisterData.getEmail())
					.param("password", mockFilmmakerRegisterData.getPassword())
					.param("confirmPassword", mockFilmmakerRegisterData.getConfirmPassword())
					.param("fullname", mockFilmmakerRegisterData.getFullname())
					.param("country", mockFilmmakerRegisterData.getCountry())
					.param("city", mockFilmmakerRegisterData.getCity())
					.param("phone", mockFilmmakerRegisterData.getPhone()))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("filmmakerRegisterData", "name"))
			.andExpect(view().name("registerFilmmaker"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verify(filmmakerService, only()).registerFilmmaker(mockFilmmakerRegisterData);
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
			mockMvc.perform(get("/account/filmmaker")).andExpect(status().isOk())
					.andExpect(model().attribute("filmmakerConfigurationData",
							FilmmakerConfigurationData.fromFilmmaker(mockFilmmaker)))
					.andExpect(view().name("manageFilmmakerAccount"));
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
	void manageAccountFilmmakerChangePicture() throws TooBigException, InvalidExtensionException, RuntimeException {
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
		mockConfigFilmmaker.setNewPhoto(new MockMultipartFile("newPhoto", "mockFile.png", "image/png", "This is an example".getBytes()));
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(mockFilmmaker));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(multipart("/account/filmmaker")
					.file((MockMultipartFile) mockConfigFilmmaker.getNewPhoto())
					.with(csrf())
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
		verify(userService, times(1)).setProfilePicture(mockFilmmaker, mockConfigFilmmaker.getNewPhoto());
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
		mockConfigFilmmaker.setCity("Seville");
		mockConfigFilmmaker.setCountry("Spain");
		mockConfigFilmmaker.setFullname("");
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
	void manageAccountFilmmakerAsCompany() {
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
					.andExpect(redirectedUrl("/account"));
		});	
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
	}
}
