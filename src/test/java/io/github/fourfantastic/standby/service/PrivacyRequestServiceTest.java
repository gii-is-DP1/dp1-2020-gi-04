package io.github.fourfantastic.standby.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.repository.PrivacyRequestRepository;
import io.github.fourfantastics.standby.service.NotificationService;
import io.github.fourfantastics.standby.service.PrivacyRequestService;
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
	void sendPrivacyRequestTest() {
		final Company mockCompanySender = new Company();
		mockCompanySender.setType(UserType.Company);

		final Filmmaker mockFilmmakerReceiver = new Filmmaker();
		mockFilmmakerReceiver.setType(UserType.Filmmaker);

		assertDoesNotThrow(() -> {
			privacyRequestService.sendPrivacyRequest(mockCompanySender, mockFilmmakerReceiver);
		});

		verify(privacyRequestRepository, only()).save(any(PrivacyRequest.class));
		verify(notificationService, only()).sendPrivacyRequestNotification(mockCompanySender.getName(),
				mockFilmmakerReceiver);
	}
	
	@Test
	void sendPrivacyRequestAsFilmmakerTest() {
		final Filmmaker mockFilmmakerSender = new Filmmaker();
		mockFilmmakerSender.setType(UserType.Filmmaker);

		final Filmmaker mockFilmmakerReceiver = new Filmmaker();
		mockFilmmakerReceiver.setType(UserType.Filmmaker);

		assertThrows(UnauthorizedException.class , () -> privacyRequestService.sendPrivacyRequest(mockFilmmakerSender, mockFilmmakerReceiver));

		verifyNoInteractions(privacyRequestRepository);
		verifyNoInteractions(notificationService);
	}

	@Test
	void sendPrivacyRequestToACompanyTest() {
		final Company mockCompanySender = new Company();
		mockCompanySender.setType(UserType.Company);

		final Company mockCompanyReceiver = new Company();
		mockCompanyReceiver.setType(UserType.Company);

		assertThrows(UnauthorizedException.class , () -> privacyRequestService.sendPrivacyRequest(mockCompanySender, mockCompanyReceiver));

		verifyNoInteractions(privacyRequestRepository);
		verifyNoInteractions(notificationService);
	}

	@Test
	void sendPrivacyRequestMoreThanOnceTest() {
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

		assertThrows(UnauthorizedException.class , () -> privacyRequestService.sendPrivacyRequest(mockSenderCompany, mockFilmmakerReceiver));

		verifyNoInteractions(privacyRequestRepository);
		verifyNoInteractions(notificationService);
	}
}
