package io.github.fourfantastic.standby.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
import io.github.fourfantastics.standby.model.PrivacyRequest;
import io.github.fourfantastics.standby.service.PrivacyRequestService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.UnauthorizedException;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
@AutoConfigureMockMvc
public class PrivacyRequestControllerTest {
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	PrivacyRequestService privacyRequestService;
	
	@MockBean
	UserService userService;
	

	@Test
	void companySendsPrivacyRequestToFilmmakerTest() throws Exception {
		final Company mockSenderCompany = new Company();
		mockSenderCompany.setName("user1");
		mockSenderCompany.setBusinessPhone("675849765");
		mockSenderCompany.setCompanyName("Company1");
		mockSenderCompany.setOfficeAddress("Calle Manzanita 3");
		mockSenderCompany.setTaxIDNumber("123-78-1234567");


		final Filmmaker mockFilmmakerReceiver = new Filmmaker();
		mockFilmmakerReceiver.setId(1L);
		mockFilmmakerReceiver.setName("filmmaker1");
		mockFilmmakerReceiver.setFullname("Filmmaker1");
		mockFilmmakerReceiver.setCountry("Spain");
		mockFilmmakerReceiver.setCity("Seville");
		mockFilmmakerReceiver.setPhone("678543167");


		when(userService.getLoggedUser()).thenReturn(Optional.of(mockSenderCompany));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerReceiver));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/privacyrequest").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verify(privacyRequestService, only()).sendPrivacyRequest(mockSenderCompany, mockFilmmakerReceiver);
		verifyNoMoreInteractions(userService);
	}

	@Test
	void companySendsPrivacyRequestToCompanyTest() throws UnauthorizedException{
		final Company mockSenderCompany = new Company();
		mockSenderCompany.setName("user1");
		mockSenderCompany.setBusinessPhone("675849765");
		mockSenderCompany.setCompanyName("Company1");
		mockSenderCompany.setOfficeAddress("Calle Manzanita 3");
		mockSenderCompany.setTaxIDNumber("123-78-1234567");
		mockSenderCompany.setConfiguration(new NotificationConfiguration());

		final Company mockReceiverCompany = new Company();
		mockReceiverCompany.setId(1l);
		mockReceiverCompany.setName("user1");
		mockReceiverCompany.setBusinessPhone("675849765");
		mockReceiverCompany.setCompanyName("Company1");
		mockReceiverCompany.setOfficeAddress("Calle Manzanita 3");
		mockReceiverCompany.setTaxIDNumber("123-78-1234567");
		mockReceiverCompany.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockSenderCompany));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockReceiverCompany));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/privacyrequest").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verify(privacyRequestService, only()).sendPrivacyRequest(mockSenderCompany, mockReceiverCompany);
		verifyNoMoreInteractions(userService);
	}

	@Test
	void companySendsPrivacyRequestMoreThanOnceToFilmmakerTest() throws Exception {
		final Filmmaker mockFilmmakerReceiver = new Filmmaker();
		mockFilmmakerReceiver.setId(1L);
		mockFilmmakerReceiver.setName("filmmaker1");
		mockFilmmakerReceiver.setFullname("Filmmaker1");
		mockFilmmakerReceiver.setCountry("Spain");
		mockFilmmakerReceiver.setCity("Seville");
		mockFilmmakerReceiver.setPhone("678543167");
		mockFilmmakerReceiver.setConfiguration(new NotificationConfiguration());

		final PrivacyRequest request = new PrivacyRequest();
		request.setFilmmaker(mockFilmmakerReceiver);

		final Company mockSenderCompany = new Company();
		mockSenderCompany.setName("user1");
		mockSenderCompany.setBusinessPhone("675849765");
		mockSenderCompany.setCompanyName("Company1");
		mockSenderCompany.setOfficeAddress("Calle Manzanita 3");
		mockSenderCompany.setTaxIDNumber("123-78-1234567");
		mockSenderCompany.setConfiguration(new NotificationConfiguration());
		mockSenderCompany.getSentRequests().add(request);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockSenderCompany));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerReceiver));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/privacyrequest").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verify(privacyRequestService, only()).sendPrivacyRequest(mockSenderCompany, mockFilmmakerReceiver);
		verifyNoMoreInteractions(userService);
	}

	@Test
	void filmmakerSendsPrivacyRequestTest() throws Exception {
		final Filmmaker mockSenderFilmmaker = new Filmmaker();
		mockSenderFilmmaker.setName("user1");
		mockSenderFilmmaker.setFullname("Filmmaker1");
		mockSenderFilmmaker.setCountry("Spain");
		mockSenderFilmmaker.setCity("Seville");
		mockSenderFilmmaker.setPhone("678543167");

		final Filmmaker mockFilmmakerReceiver = new Filmmaker();
		mockFilmmakerReceiver.setId(1L);
		mockFilmmakerReceiver.setName("filmmaker1");
		mockFilmmakerReceiver.setFullname("Filmmaker1");
		mockFilmmakerReceiver.setCountry("Spain");
		mockFilmmakerReceiver.setCity("Seville");
		mockFilmmakerReceiver.setPhone("678543167");

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockSenderFilmmaker));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerReceiver));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/privacyrequest").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verify(privacyRequestService, only()).sendPrivacyRequest(mockSenderFilmmaker, mockFilmmakerReceiver);
	}

	@Test
	void companySendsPrivacyRequestToNobody() throws Exception {
		final Filmmaker mockFilmmakerReceiver = new Filmmaker();
		mockFilmmakerReceiver.setId(1L);
		mockFilmmakerReceiver.setName("filmmaker1");
		mockFilmmakerReceiver.setFullname("Filmmaker1");
		mockFilmmakerReceiver.setCountry("Spain");
		mockFilmmakerReceiver.setCity("Seville");
		mockFilmmakerReceiver.setPhone("678543167");

		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/privacyrequest").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(privacyRequestService);
	}
	@Test
	void unregisterSendsPrivacyRequestToFilmmakerTest() throws Exception {
		final Filmmaker mockSenderFilmmaker = new Filmmaker();
		mockSenderFilmmaker.setName("user1");
		mockSenderFilmmaker.setFullname("Filmmaker1");
		mockSenderFilmmaker.setCountry("Spain");
		mockSenderFilmmaker.setCity("Seville");
		mockSenderFilmmaker.setPhone("678543167");

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockSenderFilmmaker));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/privacyrequest").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(any(Long.class));
		verifyNoMoreInteractions(userService);
		verifyNoInteractions(privacyRequestService);
	}
}
