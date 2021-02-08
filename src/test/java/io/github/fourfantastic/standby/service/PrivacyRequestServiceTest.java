package io.github.fourfantastic.standby.service;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.model.PrivacyRequest;
import io.github.fourfantastics.standby.model.RequestStateType;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.repository.PrivacyRequestRepository;
import io.github.fourfantastics.standby.service.NotificationService;
import io.github.fourfantastics.standby.service.PrivacyRequestService;
import io.github.fourfantastics.standby.service.exception.NotFoundException;
import io.github.fourfantastics.standby.service.exception.UnauthorizedException;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class PrivacyRequestServiceTest {
	PrivacyRequestService privacyRequestService;

	@Mock
	PrivacyRequestRepository privacyRequestRepository;

	@Mock
	NotificationService notificationService;

	@BeforeEach
	public void setup() {
		privacyRequestService = new PrivacyRequestService(privacyRequestRepository, notificationService);

		when(privacyRequestRepository.save(any(PrivacyRequest.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
	}

	@Test
	void companySendPrivacyRequestToFilmmakerTest() {
		final Company mockCompanySender = new Company();
		mockCompanySender.setType(UserType.Company);

		final Filmmaker mockFilmmakerReceiver = new Filmmaker();
		mockFilmmakerReceiver.setType(UserType.Filmmaker);
		
		when(notificationService.sendPrivacyRequestResponseNotification(mockCompanySender.getName(), mockFilmmakerReceiver, true))
		.thenReturn(new Notification());

		assertDoesNotThrow(() -> {
			privacyRequestService.sendPrivacyRequest(mockCompanySender, mockFilmmakerReceiver);
		});

		verify(privacyRequestRepository, times(1)).save(any(PrivacyRequest.class));
		verify(privacyRequestRepository, times(1)).findByFilmmakerAndCompany(mockFilmmakerReceiver, mockCompanySender);
		verifyNoMoreInteractions(privacyRequestRepository);
		verify(notificationService, only()).sendPrivacyRequestNotification(mockCompanySender.getName(),
				mockFilmmakerReceiver);
	}
	
	@Test
	void filmmakerSendPrivacyRequestToFilmmakerTest() {
		final Filmmaker mockFilmmakerSender = new Filmmaker();
		mockFilmmakerSender.setType(UserType.Filmmaker);

		final Filmmaker mockFilmmakerReceiver = new Filmmaker();
		mockFilmmakerReceiver.setType(UserType.Filmmaker);

		UnauthorizedException exception = assertThrows(UnauthorizedException.class , () -> privacyRequestService
				.sendPrivacyRequest(mockFilmmakerSender, mockFilmmakerReceiver));
		assertEquals(exception.getMessage(),"You must be logged as a company to perform this action");
		
		verifyNoInteractions(privacyRequestRepository);
		verifyNoInteractions(notificationService);
	}

	@Test
	void companySendPrivacyRequestToCompanyTest() {
		final Company mockCompanySender = new Company();
		mockCompanySender.setType(UserType.Company);

		final Company mockCompanyReceiver = new Company();
		mockCompanyReceiver.setType(UserType.Company);

		UnauthorizedException exception = assertThrows(UnauthorizedException.class , () -> privacyRequestService
				.sendPrivacyRequest(mockCompanySender, mockCompanyReceiver));
		assertEquals(exception.getMessage(),"Privacy requests can only be sent to filmmakers");
		
		verifyNoInteractions(privacyRequestRepository);
		verifyNoInteractions(notificationService);
	}
	
	@Test
	void filmmakerSendPrivacyRequestToCompanyTest() {
		final Filmmaker mockFilmmakerSender = new Filmmaker();
		mockFilmmakerSender.setType(UserType.Filmmaker);

		final Company mockCompanyReceiver = new Company();
		mockCompanyReceiver.setType(UserType.Company);

		UnauthorizedException exception = assertThrows(UnauthorizedException.class , () -> privacyRequestService
				.sendPrivacyRequest(mockCompanyReceiver, mockCompanyReceiver));
		assertEquals(exception.getMessage(),"Privacy requests can only be sent to filmmakers");
		
		verifyNoInteractions(privacyRequestRepository);
		verifyNoInteractions(notificationService);
	}

	@Test
	void sendPrivacyRequestMoreThanOnceTest() {
		final Filmmaker mockFilmmakerReceiver = new Filmmaker();
		final Company mockCompanySender = new Company();

		final PrivacyRequest request = new PrivacyRequest();
		request.setFilmmaker(mockFilmmakerReceiver);
		request.setCompany(mockCompanySender);
		request.setId(1L);

		when(privacyRequestRepository.findByFilmmakerAndCompany(mockFilmmakerReceiver, mockCompanySender))
		.thenReturn(Optional.of(request));
		
		UnauthorizedException exception = assertThrows(UnauthorizedException.class , () -> privacyRequestService
				.sendPrivacyRequest(mockCompanySender, mockFilmmakerReceiver));
		assertEquals(exception.getMessage(),"Privacy requests can only be sent to filmmakers once");
		
		verify(privacyRequestRepository, only()).findByFilmmakerAndCompany(mockFilmmakerReceiver, mockCompanySender);
		verifyNoInteractions(notificationService);
	}
	
	@Test
	void filmmakerAcceptPrivacytRequestWithNotificationConfigurationTest() throws Exception {
		final Company mockCompany = new Company();
		mockCompany.setConfiguration(new NotificationConfiguration());
		
		final Filmmaker mockFilmmaker = new Filmmaker();
		
		final PrivacyRequest request = new PrivacyRequest();
		request.setId(1L);
		request.setRequestState(RequestStateType.PENDING);
		request.setCompany(mockCompany);
		request.setFilmmaker(mockFilmmaker);
		
		when(privacyRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
		when(notificationService.sendPrivacyRequestResponseNotification(request.getFilmmaker().getName(), request.getCompany(), true))
		.thenReturn(new Notification());
		
		assertDoesNotThrow(() -> {
			privacyRequestService.acceptPrivacyRequest(mockFilmmaker, request.getId());
		});
		
		verify(privacyRequestRepository,times(1)).findById(request.getId());
		verify(privacyRequestRepository,times(1)).save(request);
		verifyNoMoreInteractions(privacyRequestRepository);
		verify(notificationService,only()).sendPrivacyRequestResponseNotification(request.getFilmmaker().getName(), request.getCompany(), true);
	}
	
	@Test
	void filmmakerAcceptPrivacytRequestWithoutNotificationConfigurationTest() throws Exception {
		final NotificationConfiguration notificationConfiguration =new NotificationConfiguration();
		notificationConfiguration.setByPrivacyRequests(false);
		
		final Company mockCompany = new Company();
		mockCompany.setConfiguration(notificationConfiguration);
		
		final Filmmaker mockFilmmaker = new Filmmaker();
		
		final PrivacyRequest request = new PrivacyRequest();
		request.setId(1L);
		request.setRequestState(RequestStateType.PENDING);
		request.setCompany(mockCompany);
		request.setFilmmaker(mockFilmmaker);
		
		when(privacyRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
		
		assertDoesNotThrow(() -> {
			privacyRequestService.acceptPrivacyRequest(mockFilmmaker, request.getId());
		});
		
		verify(privacyRequestRepository,times(1)).findById(request.getId());
		verify(privacyRequestRepository,times(1)).save(request);
		verifyNoMoreInteractions(privacyRequestRepository);
		verifyNoInteractions(notificationService);
		
	}
	
	@Test
	void filmmakerAcceptNullPrivacytRequestTest() throws Exception {
		final Filmmaker mockFilmmaker = new Filmmaker();
		final Long requestId = 1L;
		
		when(privacyRequestRepository.findById(requestId)).thenReturn(Optional.empty());
		
		assertThrows(NotFoundException.class , () -> privacyRequestService.acceptPrivacyRequest(mockFilmmaker, requestId));
		
		verify(privacyRequestRepository,only()).findById(requestId);
		verifyNoInteractions(notificationService);
	}
	
	@Test
	void companyAcceptPrivacytRequestTest() {
		final Company mockCompany = new Company();
		
		final PrivacyRequest request = new PrivacyRequest();
		request.setId(1L);
		
		when(privacyRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
		
		UnauthorizedException exception = assertThrows(UnauthorizedException.class , () -> privacyRequestService
				.acceptPrivacyRequest(mockCompany, request.getId()));
		assertEquals(exception.getMessage(),"You must be a Filmmaker in order to accept a request");
		
		verify(privacyRequestRepository,only()).findById(request.getId());
		verifyNoInteractions(notificationService);
	}
	
	@Test
	void filmmakerAcceptPrivacytRequestNotPendingRequestStateTest() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		
		final PrivacyRequest request = new PrivacyRequest();
		request.setId(1L);
		request.setRequestState(RequestStateType.ACCEPTED);
		
		when(privacyRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
		
		UnauthorizedException exception = assertThrows(UnauthorizedException.class , () -> privacyRequestService
				.acceptPrivacyRequest(mockFilmmaker, request.getId()));
		assertEquals(exception.getMessage(),"Request already answered");
		
		verify(privacyRequestRepository,only()).findById(request.getId());
		verifyNoInteractions(notificationService);
	}
	
	@Test
	void filmmakerAcceptPrivacytRequestNotBelongToTest() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setId(1L);
		
		final Filmmaker mockOtherFilmmaker = new Filmmaker();
		mockOtherFilmmaker.setId(2L);
		
		final PrivacyRequest request = new PrivacyRequest();
		request.setId(1L);
		request.setFilmmaker(mockOtherFilmmaker);
		request.setRequestState(RequestStateType.PENDING);
		
		when(privacyRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
		
		UnauthorizedException exception = assertThrows(UnauthorizedException.class , () -> privacyRequestService
				.acceptPrivacyRequest(mockFilmmaker, request.getId()));
		assertEquals(exception.getMessage(),"This request does not belong to you");
		
		verify(privacyRequestRepository,only()).findById(request.getId());
		verifyNoInteractions(notificationService);
	}
	
	@Test
	void filmmakerDeclinePrivacytRequestWithNotificationConfigurationTest() throws Exception {
		final Company mockCompany = new Company();
		mockCompany.setConfiguration(new NotificationConfiguration());
		
		final Filmmaker mockFilmmaker = new Filmmaker();
		
		final PrivacyRequest request = new PrivacyRequest();
		request.setId(1L);
		request.setRequestState(RequestStateType.PENDING);
		request.setCompany(mockCompany);
		request.setFilmmaker(mockFilmmaker);
		
		when(privacyRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
		when(notificationService.sendPrivacyRequestResponseNotification(request.getFilmmaker().getName(), request.getCompany(), false))
		.thenReturn(new Notification());
		
		assertDoesNotThrow(() -> {
			privacyRequestService.declinePrivacyRequest(mockFilmmaker, request.getId());
		});
		
		verify(privacyRequestRepository,times(1)).findById(request.getId());
		verify(privacyRequestRepository,times(1)).save(request);
		verifyNoMoreInteractions(privacyRequestRepository);
		verify(notificationService,only()).sendPrivacyRequestResponseNotification(request.getFilmmaker().getName(), request.getCompany(), false);
	}
	
	@Test
	void filmmakerDeclinePrivacytRequestWithoutNotificationConfigurationTest() throws Exception {
		final NotificationConfiguration notificationConfiguration =new NotificationConfiguration();
		notificationConfiguration.setByPrivacyRequests(false);
		
		final Company mockCompany = new Company();
		mockCompany.setConfiguration(notificationConfiguration);
		
		final Filmmaker mockFilmmaker = new Filmmaker();
		
		final PrivacyRequest request = new PrivacyRequest();
		request.setId(1L);
		request.setRequestState(RequestStateType.PENDING);
		request.setCompany(mockCompany);
		request.setFilmmaker(mockFilmmaker);
		
		when(privacyRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
		
		assertDoesNotThrow(() -> {
			privacyRequestService.declinePrivacyRequest(mockFilmmaker, request.getId());
		});
		
		verify(privacyRequestRepository,times(1)).findById(request.getId());
		verify(privacyRequestRepository,times(1)).save(request);
		verifyNoMoreInteractions(privacyRequestRepository);
		verifyNoInteractions(notificationService);
		
	}
	
	@Test
	void filmmakerDeclineNullPrivacytRequestTest() throws Exception {
		final Filmmaker mockFilmmaker = new Filmmaker();
		final Long requestId = 1L;
		
		when(privacyRequestRepository.findById(requestId)).thenReturn(Optional.empty());
		
		assertThrows(NotFoundException.class , () -> privacyRequestService.declinePrivacyRequest(mockFilmmaker, requestId));
		
		verify(privacyRequestRepository,only()).findById(requestId);
		verifyNoInteractions(notificationService);
	}
	
	@Test
	void companyDeclinePrivacytRequestTest() {
		final Company mockCompany = new Company();
		
		final PrivacyRequest request = new PrivacyRequest();
		request.setId(1L);
		
		when(privacyRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
		
		UnauthorizedException exception = assertThrows(UnauthorizedException.class , () -> privacyRequestService
				.declinePrivacyRequest(mockCompany, request.getId()));
		assertEquals(exception.getMessage(),"You must be a Filmmaker in order to accept a request");
		
		verify(privacyRequestRepository,only()).findById(request.getId());
		verifyNoInteractions(notificationService);
	}
	
	@Test
	void filmmakerDeclinePrivacytRequestNotPendingRequestStateTest() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		
		final PrivacyRequest request = new PrivacyRequest();
		request.setId(1L);
		request.setRequestState(RequestStateType.ACCEPTED);
		
		when(privacyRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
		
		UnauthorizedException exception = assertThrows(UnauthorizedException.class , () -> privacyRequestService
				.declinePrivacyRequest(mockFilmmaker, request.getId()));
		assertEquals(exception.getMessage(),"Request already answered");
		
		verify(privacyRequestRepository,only()).findById(request.getId());
		verifyNoInteractions(notificationService);
	}
	
	@Test
	void filmmakerDeclinePrivacytRequestNotBelongToTest() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setId(1L);
		
		final Filmmaker mockOtherFilmmaker = new Filmmaker();
		mockOtherFilmmaker.setId(2L);
		
		final PrivacyRequest request = new PrivacyRequest();
		request.setId(1L);
		request.setFilmmaker(mockOtherFilmmaker);
		request.setRequestState(RequestStateType.PENDING);
		
		when(privacyRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));
		
		UnauthorizedException exception = assertThrows(UnauthorizedException.class , () -> privacyRequestService
				.declinePrivacyRequest(mockFilmmaker, request.getId()));
		assertEquals(exception.getMessage(),"This request does not belong to you");
		
		verify(privacyRequestRepository,only()).findById(request.getId());
		verifyNoInteractions(notificationService);
	}
	
}
