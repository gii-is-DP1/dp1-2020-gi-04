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
import io.github.fourfantastics.standby.model.PrivacyRequest;
import io.github.fourfantastics.standby.model.RequestStateType;
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
	void getPrivacyRequestViewLoggedAsFilmmakerTest() {
		final Filmmaker filmmaker = new Filmmaker();
		filmmaker.setId(1L);

		final List<PrivacyRequest> privacyRequests = new ArrayList<PrivacyRequest>();

		when(userService.getLoggedUser()).thenReturn(Optional.of(filmmaker));
		when(privacyRequestService.getCountPrivacyRequestByFilmmaker(filmmaker.getId()))
				.thenReturn(privacyRequests.size());
		when(privacyRequestService.getPrivacyRequestByFilmmaker(eq(filmmaker.getId()), any(PageRequest.class)))
				.thenReturn(new PageImpl<PrivacyRequest>(privacyRequests));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/requests")).andExpect(status().isOk()).andExpect(view().name("requests"));
		});

		verify(userService, only()).getLoggedUser();
		verify(privacyRequestService, times(1)).getCountPrivacyRequestByFilmmaker(filmmaker.getId());
		verify(privacyRequestService, times(1)).getPrivacyRequestByFilmmaker(eq(filmmaker.getId()),
				any(PageRequest.class));
		verifyNoMoreInteractions(privacyRequestService);
	}

	@Test
	void getPrivacyRequestViewLoggedAsCompanyTest() {
		final Company company = new Company();
		company.setId(1L);

		when(userService.getLoggedUser()).thenReturn(Optional.of(company));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/requests")).andExpect(status().isFound()).andExpect(redirectedUrl("/"));
		});

		verify(userService, only()).getLoggedUser();
	}

	@Test
	void getPrivacyRequestViewIsNotLoggedTest() {
		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/requests")).andExpect(status().isFound()).andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
	}

	@Test
	void companySendsPrivacyRequestToFilmmakerTest() throws Exception {
		final Company mockSenderCompany = new Company();
		mockSenderCompany.setId(2L);

		final Filmmaker mockFilmmakerReceiver = new Filmmaker();
		mockFilmmakerReceiver.setId(1L);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockSenderCompany));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerReceiver));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/request").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verifyNoMoreInteractions(userService);
		verify(privacyRequestService, only()).sendPrivacyRequest(mockSenderCompany, mockFilmmakerReceiver);
	}

	@Test
	void companySendsPrivacyRequestToCompanyTest() throws UnauthorizedException {
		final Company mockSenderCompany = new Company();
		mockSenderCompany.setId(2L);

		final Company mockReceiverCompany = new Company();
		mockReceiverCompany.setId(1l);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockSenderCompany));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockReceiverCompany));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/request").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verifyNoMoreInteractions(userService);
		verify(privacyRequestService, only()).sendPrivacyRequest(mockSenderCompany, mockReceiverCompany);
	}

	@Test
	void companySendsPrivacyRequestMoreThanOnceToFilmmakerTest() throws Exception {
		final Filmmaker mockFilmmakerReceiver = new Filmmaker();
		mockFilmmakerReceiver.setId(1L);

		final PrivacyRequest request = new PrivacyRequest();
		request.setFilmmaker(mockFilmmakerReceiver);

		final Company mockSenderCompany = new Company();
		mockSenderCompany.setId(2L);
		mockSenderCompany.getSentRequests().add(request);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockSenderCompany));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerReceiver));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/request").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verifyNoMoreInteractions(userService);
		verify(privacyRequestService, only()).sendPrivacyRequest(mockSenderCompany, mockFilmmakerReceiver);
	}

	@Test
	void filmmakerSendsPrivacyRequestTest() throws Exception {
		final Filmmaker mockSenderFilmmaker = new Filmmaker();
		mockSenderFilmmaker.setId(2L);

		final Filmmaker mockFilmmakerReceiver = new Filmmaker();
		mockFilmmakerReceiver.setId(1L);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockSenderFilmmaker));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerReceiver));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/request").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verifyNoMoreInteractions(userService);
		verify(privacyRequestService, only()).sendPrivacyRequest(mockSenderFilmmaker, mockFilmmakerReceiver);
	}

	@Test
	void companySendsPrivacyRequestToNobody() throws Exception {
		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/request").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(privacyRequestService);
	}

	@Test
	void unregisterSendsPrivacyRequestToFilmmakerTest() throws Exception {
		final Filmmaker mockSenderFilmmaker = new Filmmaker();
		mockSenderFilmmaker.setId(1L);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockSenderFilmmaker));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/request").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(any(Long.class));
		verifyNoMoreInteractions(userService);
		verifyNoInteractions(privacyRequestService);
	}

	@Test
	void filmmakerAcceptPrivactRequestTest() throws Exception {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setId(1L);

		PrivacyRequest mockRequest = new PrivacyRequest();
		mockRequest.setId(1L);
		mockRequest.setRequestState(RequestStateType.ACCEPTED);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/requests/%d/accept", mockRequest.getId())).with(csrf()))
					.andExpect(status().isFound()).andExpect(redirectedUrl("/requests"));
		});

		verify(userService, only()).getLoggedUser();
		verify(privacyRequestService, only()).acceptPrivacyRequest(mockFilmmaker, mockRequest.getId());
	}

	@Test
	void companyAcceptPrivactRequestTest() throws Exception {
		final Company mockCompany = new Company();
		mockCompany.setId(1L);

		PrivacyRequest mockRequest = new PrivacyRequest();
		mockRequest.setId(1L);
		mockRequest.setRequestState(RequestStateType.ACCEPTED);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockCompany));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/requests/%d/accept", mockRequest.getId())).with(csrf()))
					.andExpect(status().isFound()).andExpect(redirectedUrl("/requests"));
		});

		verify(userService, only()).getLoggedUser();
		verify(privacyRequestService, only()).acceptPrivacyRequest(mockCompany, mockRequest.getId());
	}

	@Test
	void acceptPrivactRequestIsNotLoggedTest() {
		final Long requestId = 1L;

		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/requests/%d/accept", requestId)).with(csrf()))
					.andExpect(status().isFound()).andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(privacyRequestService);
	}

	@Test
	void filmmakerDeclinePrivactRequestTest() throws Exception {
		final Filmmaker mockFilmmaker = new Filmmaker();

		PrivacyRequest mockRequest = new PrivacyRequest();
		mockRequest.setId(1L);
		mockRequest.setRequestState(RequestStateType.ACCEPTED);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/requests/%d/decline", mockRequest.getId())).with(csrf()))
					.andExpect(status().isFound()).andExpect(redirectedUrl("/requests"));
		});

		verify(userService, only()).getLoggedUser();
		verify(privacyRequestService, only()).declinePrivacyRequest(mockFilmmaker, mockRequest.getId());
	}

	@Test
	void companyDeclinePrivactRequestTest() throws Exception {
		final Company mockCompany = new Company();

		PrivacyRequest mockRequest = new PrivacyRequest();
		mockRequest.setId(1L);
		mockRequest.setRequestState(RequestStateType.ACCEPTED);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockCompany));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/requests/%d/decline", mockRequest.getId())).with(csrf()))
					.andExpect(status().isFound()).andExpect(redirectedUrl("/requests"));
		});

		verify(userService, only()).getLoggedUser();
		verify(privacyRequestService, only()).declinePrivacyRequest(mockCompany, mockRequest.getId());
	}

	@Test
	void declinePrivactRequestIsNotLoggedTest() {
		final Long requestId = 1L;

		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/requests/%d/decline", requestId)).with(csrf()))
					.andExpect(status().isFound()).andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(privacyRequestService);
	}

}
