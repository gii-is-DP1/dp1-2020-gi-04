package io.github.fourfantastic.standby.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

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
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.form.Credentials;
import io.github.fourfantastics.standby.service.UserService;

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
	void manageAccountUserFilmmaker() {
		when(userService.getLoggedUser()).thenReturn(Optional.of(new Filmmaker()));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/account")).andExpect(status().isFound())
					.andExpect(redirectedUrl("/account/filmmaker"));
		});

		verify(userService, only()).getLoggedUser();
	}

	@Test
	void manageAccountUserCompany() {
		when(userService.getLoggedUser()).thenReturn(Optional.of(new Company()));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/account")).andExpect(status().isFound()).andExpect(redirectedUrl("/account/company"));
		});

		verify(userService, only()).getLoggedUser();
	}

	@Test
	void manageAccountUserIsNotLoggedView() {
		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/account")).andExpect(status().isFound()).andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
	}

	@Test
	void getProfileViewCompany() {
		when(userService.getLoggedUser()).thenReturn(Optional.of(new Company()));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/profile")).andExpect(status().isFound()).andExpect(redirectedUrl("/account"));
		});

		verify(userService, only()).getLoggedUser();
	}

	@Test
	void getProfileViewFilmmaker() {
		Filmmaker filmmaker = new Filmmaker();
		filmmaker.setId(1L);
		when(userService.getLoggedUser()).thenReturn(Optional.of(filmmaker));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/profile")).andExpect(status().isFound()).andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, only()).getLoggedUser();
	}

	@Test
	void getProfileViewIsNotLogged() {
		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/profile")).andExpect(status().isFound()).andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
	}
}
