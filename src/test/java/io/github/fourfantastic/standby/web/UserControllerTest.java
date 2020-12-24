package io.github.fourfantastic.standby.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import io.github.fourfantastics.standby.model.form.Credentials;
import io.github.fourfantastics.standby.service.NotificationConfigurationService;
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

	@MockBean
	NotificationConfigurationService notificationConfigurationService;

	@Test
	void loginViewTest() {
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/login"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("credentials", new Credentials()))
			.andExpect(view().name("login"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(notificationConfigurationService);
	}

	@Test
	void loginViewUserIsPresentTest() {
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(new User()));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/login"))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("/"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(notificationConfigurationService);
	}

	@Test
	void logInTest() throws NotFoundException, DataMismatchException {
		final Credentials mockCredentials = new Credentials();
		mockCredentials.setName("filmmmaker1");
		mockCredentials.setPassword("password");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		when(userService.authenticate(anyString(), anyString())).thenReturn(new User());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/login").with(csrf())
					.param("name", mockCredentials.getName())
					.param("password", mockCredentials.getPassword()))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("/"));
		});
		
		verify(userService, times(1)).getLoggedUser(any(HttpSession.class));
		verify(userService, times(1)).authenticate(mockCredentials.getName(), mockCredentials.getPassword());
		verify(userService, times(1)).logIn(any(HttpSession.class), eq(new User()));
		verifyNoMoreInteractions(userService);
		verifyNoInteractions(notificationConfigurationService);
	}
	
	@Test
	void logInUserIsPresentTest() {
		final Credentials mockCredentials = new Credentials();
		mockCredentials.setName("filmmaker");
		mockCredentials.setPassword("wrong password");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(new User()));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/login").with(csrf())
					.param("name", mockCredentials.getName())
					.param("password", mockCredentials.getPassword()))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("/"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(notificationConfigurationService);
	}
	
	@Test
	void logInMissingDataTest() {
		final Credentials mockCredentials = new Credentials();
		mockCredentials.setName("");
		mockCredentials.setPassword("wrong password");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/login").with(csrf())
					.param("name", mockCredentials.getName())
					.param("password", mockCredentials.getPassword()))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("credentials"))
			.andExpect(view().name("login"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(notificationConfigurationService);
	}
	
	@Test
	void logInUserNotFoundTest() throws NotFoundException, DataMismatchException {
		final Credentials mockCredentials = new Credentials();
		mockCredentials.setName("inventedName");
		mockCredentials.setPassword("password");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		when(userService.authenticate(anyString(), anyString())).thenThrow(new NotFoundException("", Utils.hashSet("name")));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/login").with(csrf())
					.param("name", mockCredentials.getName())
					.param("password", mockCredentials.getPassword()))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("credentials", "name"))
			.andExpect(view().name("login"));
		});
		
		verify(userService, times(1)).getLoggedUser(any(HttpSession.class));
		verify(userService, times(1)).authenticate(mockCredentials.getName(), mockCredentials.getPassword());
		verifyNoMoreInteractions(userService);
		verifyNoInteractions(notificationConfigurationService);
	}
	
	@Test
	void logInPasswordDoNotMatchTest() throws DataMismatchException, NotFoundException {
		final Credentials mockCredentials = new Credentials();
		mockCredentials.setName("filmmaker");
		mockCredentials.setPassword("wrong password");
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		when(userService.authenticate(anyString(), anyString())).thenThrow(new DataMismatchException("", Utils.hashSet("password")));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/login").with(csrf())
					.param("name", mockCredentials.getName())
					.param("password", mockCredentials.getPassword()))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("credentials", "password"))
			.andExpect(view().name("login"));
		});
		
		verify(userService, times(1)).getLoggedUser(any(HttpSession.class));
		verify(userService, times(1)).authenticate(mockCredentials.getName(), mockCredentials.getPassword());
		verifyNoMoreInteractions(userService);
		verifyNoInteractions(notificationConfigurationService);
	}
}
