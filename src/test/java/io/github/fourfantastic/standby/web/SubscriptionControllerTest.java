package io.github.fourfantastic.standby.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.service.SubscriptionService;
import io.github.fourfantastics.standby.service.UserService;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
@AutoConfigureMockMvc
public class SubscriptionControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	UserService userService;
	
	@MockBean
	SubscriptionService subscriptionService;
	
	@Test
	void filmmakerSubscribesToFilmmakerTest() throws Exception {
		final Filmmaker mockFollower = new Filmmaker();
		mockFollower.setName("user1");
		mockFollower.setFullname("Filmmaker1");
		mockFollower.setCountry("Spain");
		mockFollower.setCity("Seville");
		mockFollower.setPhone("678543167");
		mockFollower.setConfiguration(new NotificationConfiguration());

		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		mockFilmmakerFollowed.setId(1L);
		mockFilmmakerFollowed.setName("filmmaker1");
		mockFilmmakerFollowed.setFullname("Filmmaker1");
		mockFilmmakerFollowed.setCountry("Spain");
		mockFilmmakerFollowed.setCity("Seville");
		mockFilmmakerFollowed.setPhone("678543167");
		mockFilmmakerFollowed.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFollower));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerFollowed));
		when(subscriptionService.isAlreadySubscribedTo(mockFollower, mockFilmmakerFollowed)).thenReturn(false);

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/profile/%d/subscription", mockFilmmakerFollowed.getId())).with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(mockFilmmakerFollowed.getId());
		verifyNoMoreInteractions(userService);
		verify(subscriptionService, times(1)).isAlreadySubscribedTo(mockFollower, mockFilmmakerFollowed);
		verify(subscriptionService, times(1)).subscribeTo(mockFollower, mockFilmmakerFollowed);
		verifyNoMoreInteractions(subscriptionService);
	}

	@Test
	void companySubscribesToFilmmakerTest() throws Exception {
		final Company mockFollower = new Company();
		mockFollower.setName("user1");
		mockFollower.setBusinessPhone("675849765");
		mockFollower.setCompanyName("Company1");
		mockFollower.setOfficeAddress("Calle Manzanita 3");
		mockFollower.setTaxIDNumber("123-78-1234567");
		mockFollower.setConfiguration(new NotificationConfiguration());

		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		mockFilmmakerFollowed.setId(1L);
		mockFilmmakerFollowed.setName("filmmaker1");
		mockFilmmakerFollowed.setFullname("Filmmaker1");
		mockFilmmakerFollowed.setCountry("Spain");
		mockFilmmakerFollowed.setCity("Seville");
		mockFilmmakerFollowed.setPhone("678543167");
		mockFilmmakerFollowed.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFollower));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerFollowed));
		when(subscriptionService.isAlreadySubscribedTo(mockFollower, mockFilmmakerFollowed)).thenReturn(false);
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/profile/%d/subscription", mockFilmmakerFollowed.getId())).with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(mockFilmmakerFollowed.getId());
		verifyNoMoreInteractions(userService);
		verify(subscriptionService, times(1)).isAlreadySubscribedTo(mockFollower, mockFilmmakerFollowed);
		verify(subscriptionService, times(1)).subscribeTo(mockFollower, mockFilmmakerFollowed);
		verifyNoMoreInteractions(subscriptionService);
	}

	@Test
	void unregisterUserSubscribesToFilmmakerTest() throws Exception {
		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		mockFilmmakerFollowed.setId(1L);
		mockFilmmakerFollowed.setName("filmmaker1");
		mockFilmmakerFollowed.setFullname("Filmmaker1");
		mockFilmmakerFollowed.setCountry("Spain");
		mockFilmmakerFollowed.setCity("Seville");
		mockFilmmakerFollowed.setPhone("678543167");
		mockFilmmakerFollowed.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/profile/%d/subscription", mockFilmmakerFollowed.getId())).with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(subscriptionService);
	}

	@Test
	void userSubscribesToCompanyTest() throws Exception {
		final Filmmaker mockFollower = new Filmmaker();
		mockFollower.setName("user1");
		mockFollower.setFullname("Filmmaker1");
		mockFollower.setCountry("Spain");
		mockFollower.setCity("Seville");
		mockFollower.setPhone("678543167");
		mockFollower.setConfiguration(new NotificationConfiguration());

		final Company mockCompanyFollowed = new Company();
		mockCompanyFollowed.setId(1L);
		mockCompanyFollowed.setName("user1");
		mockCompanyFollowed.setBusinessPhone("675849765");
		mockCompanyFollowed.setCompanyName("Company1");
		mockCompanyFollowed.setOfficeAddress("Calle Manzanita 3");
		mockCompanyFollowed.setTaxIDNumber("123-78-1234567");
		mockCompanyFollowed.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFollower));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockCompanyFollowed));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/profile/%d/subscription", mockCompanyFollowed.getId())).with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl(String.format("/profile/%d", mockCompanyFollowed.getId())));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(mockCompanyFollowed.getId());
		verifyNoMoreInteractions(userService);
		verifyNoInteractions(subscriptionService);
	}

	@Test
	void filmmakerSubscribesToItselfTest() throws Exception {
		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		mockFilmmakerFollowed.setId(1L);
		mockFilmmakerFollowed.setName("filmmaker1");
		mockFilmmakerFollowed.setFullname("Filmmaker1");
		mockFilmmakerFollowed.setCountry("Spain");
		mockFilmmakerFollowed.setCity("Seville");
		mockFilmmakerFollowed.setPhone("678543167");
		mockFilmmakerFollowed.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmakerFollowed));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerFollowed));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/profile/%d/subscription", mockFilmmakerFollowed.getId())).with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl(String.format("/profile/%d", mockFilmmakerFollowed.getId())));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(mockFilmmakerFollowed.getId());
		verifyNoMoreInteractions(userService);
		verifyNoInteractions(subscriptionService);
	}

	@Test
	void alreadySubscribedUserSubscribesToFilmmakerTest() throws Exception {
		final Filmmaker mockFollower = new Filmmaker();
		mockFollower.setName("user1");
		mockFollower.setFullname("Filmmaker1");
		mockFollower.setCountry("Spain");
		mockFollower.setCity("Seville");
		mockFollower.setPhone("678543167");
		mockFollower.setConfiguration(new NotificationConfiguration());

		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		mockFilmmakerFollowed.setId(1L);
		mockFilmmakerFollowed.setName("filmmaker1");
		mockFilmmakerFollowed.setFullname("Filmmaker1");
		mockFilmmakerFollowed.setCountry("Spain");
		mockFilmmakerFollowed.setCity("Seville");
		mockFilmmakerFollowed.setPhone("678543167");
		mockFilmmakerFollowed.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFollower));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerFollowed));
		when(subscriptionService.isAlreadySubscribedTo(mockFollower, mockFilmmakerFollowed)).thenReturn(true);

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/profile/%d/subscription", mockFilmmakerFollowed.getId())).with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl(String.format("/profile/%d", mockFilmmakerFollowed.getId())));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(mockFilmmakerFollowed.getId());
		verifyNoMoreInteractions(userService);
		verify(subscriptionService, only()).isAlreadySubscribedTo(mockFollower, mockFilmmakerFollowed);
	}

	@Test
	void filmmakerUnsubscribesToFilmmakerTest() throws Exception {
		final Filmmaker mockFollower = new Filmmaker();
		mockFollower.setName("user1");
		mockFollower.setFullname("Filmmaker1");
		mockFollower.setCountry("Spain");
		mockFollower.setCity("Seville");
		mockFollower.setPhone("678543167");
		mockFollower.setConfiguration(new NotificationConfiguration());

		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		mockFilmmakerFollowed.setId(1L);
		mockFilmmakerFollowed.setName("filmmaker1");
		mockFilmmakerFollowed.setFullname("Filmmaker2");
		mockFilmmakerFollowed.setCountry("Spain");
		mockFilmmakerFollowed.setCity("Seville");
		mockFilmmakerFollowed.setPhone("678543167");
		mockFilmmakerFollowed.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFollower));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerFollowed));
		when(subscriptionService.isAlreadySubscribedTo(mockFollower, mockFilmmakerFollowed)).thenReturn(true);

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/profile/%d/unsubscription", mockFilmmakerFollowed.getId())).with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl(String.format("/profile/%d", mockFilmmakerFollowed.getId())));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(mockFilmmakerFollowed.getId());
		verifyNoMoreInteractions(userService);
		verify(subscriptionService, times(1)).isAlreadySubscribedTo(mockFollower, mockFilmmakerFollowed);
		verify(subscriptionService, times(1)).unsubscribeTo(mockFollower, mockFilmmakerFollowed);
		verifyNoMoreInteractions(subscriptionService);
	}
	
	@Test
	void filmmakerUnsubscribesToCompanyTest() throws Exception {
		final Filmmaker mockFollower = new Filmmaker();
		mockFollower.setName("user1");
		mockFollower.setFullname("Filmmaker1");
		mockFollower.setCountry("Spain");
		mockFollower.setCity("Seville");
		mockFollower.setPhone("678543167");
		mockFollower.setConfiguration(new NotificationConfiguration());

		final Company mockCompanyFollowed = new Company();
		mockCompanyFollowed.setId(2L);
		
		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFollower));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockCompanyFollowed));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/profile/%d/unsubscription", mockCompanyFollowed.getId())).with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl(String.format("/profile/%d", mockCompanyFollowed.getId())));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(mockCompanyFollowed.getId());
		verifyNoMoreInteractions(userService);
		verifyNoInteractions(subscriptionService);
	}

	@Test
	void companyUnsubscribesToFilmmakerTest() throws Exception {
		final Company mockFollower = new Company();
		mockFollower.setName("user1");
		mockFollower.setBusinessPhone("675849765");
		mockFollower.setCompanyName("Company1");
		mockFollower.setOfficeAddress("Calle Manzanita 3");
		mockFollower.setTaxIDNumber("123-78-1234567");
		mockFollower.setConfiguration(new NotificationConfiguration());

		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		mockFilmmakerFollowed.setId(1L);
		mockFilmmakerFollowed.setName("filmmaker1");
		mockFilmmakerFollowed.setFullname("Filmmaker2");
		mockFilmmakerFollowed.setCountry("Spain");
		mockFilmmakerFollowed.setCity("Seville");
		mockFilmmakerFollowed.setPhone("678543167");
		mockFilmmakerFollowed.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFollower));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerFollowed));
		when(subscriptionService.isAlreadySubscribedTo(mockFollower, mockFilmmakerFollowed)).thenReturn(true);

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/profile/%d/unsubscription", mockFilmmakerFollowed.getId())).with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl(String.format("/profile/%d", mockFilmmakerFollowed.getId())));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(mockFilmmakerFollowed.getId());
		verifyNoMoreInteractions(userService);
		verify(subscriptionService, times(1)).isAlreadySubscribedTo(mockFollower, mockFilmmakerFollowed);
		verify(subscriptionService, times(1)).unsubscribeTo(mockFollower, mockFilmmakerFollowed);
		verifyNoMoreInteractions(subscriptionService);
	}

	@Test
	void filmmakerUnsubscribesToItselfTest() throws Exception {
		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		mockFilmmakerFollowed.setId(1L);
		mockFilmmakerFollowed.setName("filmmaker1");
		mockFilmmakerFollowed.setFullname("Filmmaker1");
		mockFilmmakerFollowed.setCountry("Spain");
		mockFilmmakerFollowed.setCity("Seville");
		mockFilmmakerFollowed.setPhone("678543167");
		mockFilmmakerFollowed.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmakerFollowed));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerFollowed));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/profile/%d/unsubscription", mockFilmmakerFollowed.getId())).with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl(String.format("/profile/%d", mockFilmmakerFollowed.getId())));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(mockFilmmakerFollowed.getId());
		verifyNoMoreInteractions(userService);
		verifyNoInteractions(subscriptionService);
	}

	@Test
	void userUnsubscribesToCompanyTest() throws Exception {
		final Filmmaker mockFollower = new Filmmaker();
		mockFollower.setName("user1");
		mockFollower.setFullname("Filmmaker1");
		mockFollower.setCountry("Spain");
		mockFollower.setCity("Seville");
		mockFollower.setPhone("678543167");
		mockFollower.setConfiguration(new NotificationConfiguration());

		final Company mockCompanyFollowed = new Company();
		mockCompanyFollowed.setId(1l);
		mockCompanyFollowed.setName("user1");
		mockCompanyFollowed.setBusinessPhone("675849765");
		mockCompanyFollowed.setCompanyName("Company1");
		mockCompanyFollowed.setOfficeAddress("Calle Manzanita 3");
		mockCompanyFollowed.setTaxIDNumber("123-78-1234567");
		mockCompanyFollowed.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFollower));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockCompanyFollowed));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/profile/%d/subscription", mockCompanyFollowed.getId())).with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl(String.format("/profile/%d", mockCompanyFollowed.getId())));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(mockCompanyFollowed.getId());
		verifyNoMoreInteractions(userService);
		verifyNoInteractions(subscriptionService);
	}

	@Test
	void notSubscribedUserUnsubscribesToFilmmakerTest() throws Exception {
		final Filmmaker mockFollower = new Filmmaker();
		mockFollower.setName("user1");
		mockFollower.setFullname("Filmmaker1");
		mockFollower.setCountry("Spain");
		mockFollower.setCity("Seville");
		mockFollower.setPhone("678543167");
		mockFollower.setConfiguration(new NotificationConfiguration());

		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		mockFilmmakerFollowed.setId(1L);
		mockFilmmakerFollowed.setName("filmmaker1");
		mockFilmmakerFollowed.setFullname("Filmmaker1");
		mockFilmmakerFollowed.setCountry("Spain");
		mockFilmmakerFollowed.setCity("Seville");
		mockFilmmakerFollowed.setPhone("678543167");
		mockFilmmakerFollowed.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFollower));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerFollowed));
		when(subscriptionService.isAlreadySubscribedTo(mockFollower, mockFilmmakerFollowed)).thenReturn(false);
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/profile/%d/unsubscription", mockFilmmakerFollowed.getId())).with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl(String.format("/profile/%d", mockFilmmakerFollowed.getId())));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(mockFilmmakerFollowed.getId());
		verifyNoMoreInteractions(userService);
		verify(subscriptionService, only()).isAlreadySubscribedTo(mockFollower, mockFilmmakerFollowed);
	}

	@Test
	void unregisterUserUnsubscribesToFilmmakerTest() throws Exception {
		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		mockFilmmakerFollowed.setId(1L);
		mockFilmmakerFollowed.setName("filmmaker1");
		mockFilmmakerFollowed.setFullname("Filmmaker1");
		mockFilmmakerFollowed.setCountry("Spain");
		mockFilmmakerFollowed.setCity("Seville");
		mockFilmmakerFollowed.setPhone("678543167");
		mockFilmmakerFollowed.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/profile/%d/unsubscription", mockFilmmakerFollowed.getId())).with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(subscriptionService);
	}
}
