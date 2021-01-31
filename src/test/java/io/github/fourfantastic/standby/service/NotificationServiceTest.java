package io.github.fourfantastic.standby.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.NotificationType;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.NotificationRepository;
import io.github.fourfantastics.standby.service.NotificationService;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class NotificationServiceTest {
	NotificationService notificationService;
	
	@Mock
	NotificationRepository notificationRepository;
	
	@BeforeEach
	public void setup() {
		notificationService = new NotificationService(notificationRepository);
		
		when(notificationRepository.save(any(Notification.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
	}
	
	@Test
	public void readNotificationsTest() {
		final List<Notification> notifications = new ArrayList<Notification>();
		notifications.add(new Notification(1L, "Notification example 1", 1L, null, NotificationType.COMMENT, new User()));
		notifications.add(new Notification(2L, "Notification example 2", 1L, null, NotificationType.PRIVACY_REQUEST, new User()));
		notifications.add(new Notification(3L, "Notification example 3", 1L, null, NotificationType.RATING, new User()));
		
		assertDoesNotThrow(() -> {
			notificationService.readNotifications(notifications);
		});
		
		for (Notification notification : notifications) {
			assertNotNull(notification.getReadDate());
		}
	}
	
	@Test
	public void readAlreadySeenNotificationsTest() {
		final List<Notification> notifications = new ArrayList<Notification>();
		notifications.add(new Notification(1L, "Notification example 1", 1L, 7L, NotificationType.COMMENT, new User()));
		notifications.add(new Notification(2L, "Notification example 2", 1L, 7L, NotificationType.PRIVACY_REQUEST, new User()));
		notifications.add(new Notification(3L, "Notification example 3", 1L, 7L, NotificationType.RATING, new User()));
		
		assertDoesNotThrow(() -> {
			notificationService.readNotifications(notifications);
		});
		
		for (Notification notification : notifications) {
			assertThat(notification.getReadDate()).isEqualTo(7L);
		}
	}
	
	@Test
	void sendNotificationTest() {
		final User mockUserReceiver = new User();
		final NotificationType type = NotificationType.PRIVACY_REQUEST;
		final String text = "This is a notification of type PrivacyRequest";

		assertDoesNotThrow(() -> {
			Notification notification = notificationService.sendNotification(mockUserReceiver, type, text);

			assertThat(notification.getUser()).isEqualTo(mockUserReceiver);
			assertThat(notification.getType()).isEqualTo(type);
			assertThat(notification.getText()).isEqualTo(text);
			assertNotNull(notification.getEmissionDate());
			assertNull(notification.getReadDate());
			
			verify(notificationRepository, only()).save(notification);
		});
	}
	
	@Test
	void sendPrivacyRequestTest() {
		final User mockUserReceiver = new User();
		final String senderName = "Pepito";
		
		assertDoesNotThrow(() -> {
			Notification notification = notificationService.sendPrivacyRequestNotification(senderName, mockUserReceiver);

			assertThat(notification.getUser()).isEqualTo(mockUserReceiver);
			assertThat(notification.getType()).isEqualTo(NotificationType.PRIVACY_REQUEST);
			assertThat(notification.getText()).contains(senderName);
			assertNotNull(notification.getEmissionDate());
			assertNull(notification.getReadDate());
			
			verify(notificationRepository, only()).save(notification);
		});
	}
	
	@Test
	void sendPrivacyRequestResponseTest() {
		final User mockUserReceiver = new User();
		final String senderName = "Pepito";
		final Boolean accepted = true;
		
		assertDoesNotThrow(() -> {
			Notification notification = notificationService.sendPrivacyRequestResponseNotification(senderName, mockUserReceiver, accepted);

			assertThat(notification.getUser()).isEqualTo(mockUserReceiver);
			assertThat(notification.getType()).isEqualTo(NotificationType.PRIVACY_REQUEST);
			assertThat(notification.getText()).contains(senderName);
			assertNotNull(notification.getEmissionDate());
			assertNull(notification.getReadDate());
			
			verify(notificationRepository, only()).save(notification);
		});
	}
}