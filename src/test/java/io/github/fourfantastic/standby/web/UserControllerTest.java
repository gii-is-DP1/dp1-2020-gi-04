package io.github.fourfantastic.standby.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.form.Credentials;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.UserService;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	UserService userService;
	
	@MockBean
	ShortFilmService shortFilmService;

	@Test
	void loginViewTest() {
		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/login")).andExpect(status().isOk())
					.andExpect(model().attribute("credentials", new Credentials())).andExpect(view().name("login"));
		});
		verify(userService, only()).getLoggedUser();
	}

	@Test
	void loginUserIsLoggedViewTest() {
		when(userService.getLoggedUser()).thenReturn(Optional.of(new User()));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/login")).andExpect(status().isFound()).andExpect(redirectedUrl("/"));
		});

		verify(userService, only()).getLoggedUser();
	}

	/*
	 * @Test void logInTest() throws NotFoundException, DataMismatchException {
	 * final Credentials mockCredentials = new Credentials();
	 * mockCredentials.setName("filmmmaker1");
	 * mockCredentials.setPassword("password");
	 * 
	 * when(userService.getLoggedUser()).thenReturn(Optional.empty());
	 * when(userService.authenticate(anyString(), anyString())).thenReturn(new
	 * User());
	 * 
	 * assertDoesNotThrow(() -> {
	 * mockMvc.perform(post("/login").with(csrf()).param("name",
	 * mockCredentials.getName()).param("password",
	 * mockCredentials.getPassword())).andExpect(status().isFound()).andExpect(
	 * redirectedUrl("/")); });
	 * 
	 * verify(userService, times(1)).getLoggedUser(); verify(userService,
	 * times(1)).authenticate(mockCredentials.getName(),
	 * mockCredentials.getPassword()); verify(userService, times(1)).logIn(, eq(new
	 * User())); verifyNoMoreInteractions(userService); }
	 * 
	 * @Test void logInUserNotFoundTest() throws NotFoundException,
	 * DataMismatchException { final Credentials mockCredentials = new
	 * Credentials(); mockCredentials.setName("inventedName");
	 * mockCredentials.setPassword("password");
	 * 
	 * when(userService.getLoggedUser()).thenReturn(Optional.empty());
	 * when(userService.authenticate(anyString(), anyString())) .thenThrow(new
	 * NotFoundException("", Utils.hashSet("name")));
	 * 
	 * assertDoesNotThrow(() -> {
	 * mockMvc.perform(post("/login").with(csrf()).param("name",
	 * mockCredentials.getName()).param("password",
	 * mockCredentials.getPassword())).andExpect(status().isOk()).andExpect(view().
	 * name("login")); });
	 * 
	 * verify(userService, times(1)).getLoggedUser(); verify(userService,
	 * times(1)).authenticate(mockCredentials.getName(),
	 * mockCredentials.getPassword()); verifyNoMoreInteractions(userService); }
	 * 
	 * @Test void logInPasswordDoNotMatchTest() throws DataMismatchException,
	 * NotFoundException { final Credentials mockCredentials = new Credentials();
	 * mockCredentials.setName("filmmaker");
	 * mockCredentials.setPassword("wrong password");
	 * 
	 * when(userService.getLoggedUser()).thenReturn(Optional.empty());
	 * when(userService.authenticate(anyString(), anyString())) .thenThrow(new
	 * DataMismatchException("", Utils.hashSet("password")));
	 * 
	 * assertDoesNotThrow(() -> {
	 * mockMvc.perform(post("/login").with(csrf()).param("name",
	 * mockCredentials.getName()).param("password",
	 * mockCredentials.getPassword())).andExpect(status().isOk()).andExpect(view().
	 * name("login")); });
	 * 
	 * verify(userService, times(1)).getLoggedUser(); verify(userService,
	 * times(1)).authenticate(mockCredentials.getName(),
	 * mockCredentials.getPassword()); verifyNoMoreInteractions(userService); }
	 * 
	 * @Test void logInValidatorMissingData() { final Credentials mockCredentials =
	 * new Credentials(); mockCredentials.setName("");
	 * mockCredentials.setPassword("wrong password");
	 * 
	 * when(userService.getLoggedUser()).thenReturn(Optional.empty());
	 * 
	 * assertDoesNotThrow(() -> {
	 * mockMvc.perform(post("/login").with(csrf()).param("name",
	 * mockCredentials.getName()).param("password",
	 * mockCredentials.getPassword())).andExpect(status().isOk()).andExpect(view().
	 * name("login")); });
	 * 
	 * verify(userService, only()).getLoggedUser(); }
	 * 
	 * @Test void logInUserIsLogged() { final Credentials mockCredentials = new
	 * Credentials(); mockCredentials.setName("filmmaker");
	 * mockCredentials.setPassword("wrong password");
	 * 
	 * when(userService.getLoggedUser()).thenReturn(Optional.of(new User()));
	 * 
	 * assertDoesNotThrow(() -> {
	 * mockMvc.perform(post("/login").with(csrf()).param("name",
	 * mockCredentials.getName()).param("password",
	 * mockCredentials.getPassword())).andExpect(status().isFound()).andExpect(
	 * redirectedUrl("/")); });
	 * 
	 * verify(userService, only()).getLoggedUser(); }
	 */
	@Test
	void manageAccountUserFilmmakerTest() {
		when(userService.getLoggedUser()).thenReturn(Optional.of(new Filmmaker()));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/account")).andExpect(status().isFound())
					.andExpect(redirectedUrl("/account/filmmaker"));
		});

		verify(userService, only()).getLoggedUser();
	}

	@Test
	void manageAccountUserCompanyTest() {
		when(userService.getLoggedUser()).thenReturn(Optional.of(new Company()));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/account")).andExpect(status().isFound()).andExpect(redirectedUrl("/account/company"));
		});

		verify(userService, only()).getLoggedUser();
	}

	@Test
	void manageAccountUserIsNotLoggedViewTest() {
		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/account")).andExpect(status().isFound()).andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
	}

	@Test
	void getProfileViewCompanyTest() {
		when(userService.getLoggedUser()).thenReturn(Optional.of(new Company()));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/profile")).andExpect(status().isFound()).andExpect(redirectedUrl("/account"));
		});

		verify(userService, only()).getLoggedUser();
	}

	@Test
	void getProfileViewFilmmakerTest() {
		Filmmaker filmmaker = new Filmmaker();
		filmmaker.setId(1L);
		when(userService.getLoggedUser()).thenReturn(Optional.of(filmmaker));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/profile")).andExpect(status().isFound()).andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, only()).getLoggedUser();
	}

	@Test
	void getProfileViewIsNotLoggedTest() {
		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/profile")).andExpect(status().isFound()).andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
	}
	
	@Test
	void getFeedViewFilmmakerTest() {
		final Filmmaker filmmaker = new Filmmaker();
		filmmaker.setId(1L);
		filmmaker.setName("filmmaker1");
		filmmaker.setFullname("Filmmaker1");
		filmmaker.setCountry("Spain");
		filmmaker.setCity("Seville");
		filmmaker.setPhone("678543167");
		filmmaker.setConfiguration(new NotificationConfiguration());
		
		final List<ShortFilm> followedShortFilms = new ArrayList<ShortFilm>();
		
		when(userService.getLoggedUser()).thenReturn(Optional.of(filmmaker));
		when(shortFilmService.getFollowedShortFilmsCount(filmmaker.getId())).thenReturn(followedShortFilms.size());
		when(shortFilmService.getFollowedShortFilms(eq(filmmaker.getId()), any(PageRequest.class)))
		.thenReturn(new PageImpl<ShortFilm>(followedShortFilms));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("feed"));
		});
		
		verify(userService, times(1)).getLoggedUser();
		verifyNoMoreInteractions(userService);
		verify(shortFilmService, times(1)).getFollowedShortFilmsCount(filmmaker.getId());
		verify(shortFilmService, times(1)).getFollowedShortFilms(eq(filmmaker.getId()), any(PageRequest.class));
		verifyNoMoreInteractions(shortFilmService);
	}
	
	@Test
	void getFeedViewCompanyTest() {
		final Company company = new Company();
		company.setName("user1");
		company.setBusinessPhone("675849765");
		company.setCompanyName("Company1");
		company.setOfficeAddress("Calle Manzanita 3");
		company.setTaxIDNumber("123-78-1234567");
		company.setConfiguration(new NotificationConfiguration());
		
		final List<ShortFilm> followedShortFilms = new ArrayList<ShortFilm>();
		
		when(userService.getLoggedUser()).thenReturn(Optional.of(company));
		when(shortFilmService.getFollowedShortFilmsCount(company.getId())).thenReturn(followedShortFilms.size());
		when(shortFilmService.getFollowedShortFilms(eq(company.getId()), any(PageRequest.class)))
		.thenReturn(new PageImpl<ShortFilm>(followedShortFilms));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("feed"));
		});
		
		verify(userService, times(1)).getLoggedUser();
		verifyNoMoreInteractions(userService);
		verify(shortFilmService, times(1)).getFollowedShortFilmsCount(company.getId());
		verify(shortFilmService, times(1)).getFollowedShortFilms(eq(company.getId()), any(PageRequest.class));
		verifyNoMoreInteractions(shortFilmService);
	}
	
	@Test
	void getFeedViewIsNotLoggedTest() {
		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/")).andExpect(status().isFound()).andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
	}
}
