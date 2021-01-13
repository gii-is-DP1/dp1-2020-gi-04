package io.github.fourfantastic.standby.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.model.PrivacyRequest;
import io.github.fourfantastics.standby.model.RequestStateType;
import io.github.fourfantastics.standby.repository.PrivacyRequestRepository;
import io.github.fourfantastics.standby.service.NotificationService;
import io.github.fourfantastics.standby.service.PrivacyRequestService;

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
	void sendPrivacyRequestTest() {
		final Company mockCompanySender = new Company();
		mockCompanySender.setCompanyName("CompanyName");

		final Filmmaker mockFilmmakerReceiver = new Filmmaker();

		privacyRequestService.sendPrivacyRequest(mockCompanySender, mockFilmmakerReceiver);

		verify(privacyRequestRepository, only()).save(any(PrivacyRequest.class));
		verify(notificationService, only()).sendPrivacyRequestNotification(mockCompanySender.getName(),
				mockFilmmakerReceiver);
	}

	@Test
	void acceptPrivacyRequestWithNotificationTest() {
		final NotificationConfiguration notificationConfiguration = new NotificationConfiguration();
		notificationConfiguration.setByPrivacyRequests(true);

		final Company mockCompanyReceiver = new Company();
		mockCompanyReceiver.setCompanyName("CompanyName");
		mockCompanyReceiver.setConfiguration(notificationConfiguration);

		final Filmmaker mockFilmmakerSender = new Filmmaker();
		mockFilmmakerSender.setName("Filomena");

		final PrivacyRequest mockRequest = new PrivacyRequest();
		mockRequest.setCompany(mockCompanyReceiver);
		mockRequest.setFilmmaker(mockFilmmakerSender);
		mockRequest.setRequestDate(new Date().getTime());
		mockRequest.setRequestState(RequestStateType.PENDING);

		privacyRequestService.acceptPrivacyRequest(mockRequest);

		verify(privacyRequestRepository, only()).save(any(PrivacyRequest.class));
		verify(notificationService, only()).sendPrivacyRequestResponseNotification(mockFilmmakerSender.getName(),
				mockCompanyReceiver, true);
	}

	@Test
	void acceptPrivacyRequestWithoutNotificationTest() {
		final NotificationConfiguration notificationConfiguration = new NotificationConfiguration();
		notificationConfiguration.setByPrivacyRequests(false);

		final Company mockCompanyReceiver = new Company();
		mockCompanyReceiver.setCompanyName("CompanyName");
		mockCompanyReceiver.setConfiguration(notificationConfiguration);

		final Filmmaker mockFilmmakerSender = new Filmmaker();
		mockFilmmakerSender.setName("Filomena");

		final PrivacyRequest mockRequest = new PrivacyRequest();
		mockRequest.setCompany(mockCompanyReceiver);
		mockRequest.setFilmmaker(mockFilmmakerSender);
		mockRequest.setRequestDate(new Date().getTime());
		mockRequest.setRequestState(RequestStateType.PENDING);

		privacyRequestService.acceptPrivacyRequest(mockRequest);

		verify(privacyRequestRepository, only()).save(any(PrivacyRequest.class));
		verifyNoInteractions(notificationService);
	}

	@Test
	void declinePrivacyRequestWithNotificationTest() {
		final NotificationConfiguration notificationConfiguration = new NotificationConfiguration();
		notificationConfiguration.setByPrivacyRequests(true);

		final Company mockCompanyReceiver = new Company();
		mockCompanyReceiver.setCompanyName("CompanyName");
		mockCompanyReceiver.setConfiguration(notificationConfiguration);

		final Filmmaker mockFilmmakerSender = new Filmmaker();
		mockFilmmakerSender.setName("Filomena");

		final PrivacyRequest mockRequest = new PrivacyRequest();
		mockRequest.setCompany(mockCompanyReceiver);
		mockRequest.setFilmmaker(mockFilmmakerSender);
		mockRequest.setRequestDate(new Date().getTime());
		mockRequest.setRequestState(RequestStateType.PENDING);

		privacyRequestService.declinePrivacyRequest(mockRequest);

		verify(privacyRequestRepository, only()).save(any(PrivacyRequest.class));
		verify(notificationService, only()).sendPrivacyRequestResponseNotification(mockFilmmakerSender.getName(),
				mockCompanyReceiver, false);
	}

	@Test
	void declinePrivacyRequestWithoutNotificationTest() {
		final NotificationConfiguration notificationConfiguration = new NotificationConfiguration();
		notificationConfiguration.setByPrivacyRequests(false);

		final Company mockCompanyReceiver = new Company();
		mockCompanyReceiver.setCompanyName("CompanyName");
		mockCompanyReceiver.setConfiguration(notificationConfiguration);

		final Filmmaker mockFilmmakerSender = new Filmmaker();
		mockFilmmakerSender.setName("Filomena");

		final PrivacyRequest mockRequest = new PrivacyRequest();
		mockRequest.setCompany(mockCompanyReceiver);
		mockRequest.setFilmmaker(mockFilmmakerSender);
		mockRequest.setRequestDate(new Date().getTime());
		mockRequest.setRequestState(RequestStateType.PENDING);

		privacyRequestService.declinePrivacyRequest(mockRequest);

		verify(privacyRequestRepository, only()).save(any(PrivacyRequest.class));
		verifyNoInteractions(notificationService);
	}
}
