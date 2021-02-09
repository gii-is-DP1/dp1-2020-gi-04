package io.github.fourfantastic.standby.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.model.NotificationType;
import io.github.fourfantastics.standby.model.Subscription;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.SubscriptionRepository;
import io.github.fourfantastics.standby.service.NotificationService;
import io.github.fourfantastics.standby.service.SubscriptionService;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class SubscriptionServiceTest {
	SubscriptionService subscriptionService;
	
	@Mock
	SubscriptionRepository subscriptionRepository;
	
	@Mock
	NotificationService notificationService;
	
	@BeforeEach
	public void setup() {
		subscriptionService = new SubscriptionService(subscriptionRepository, notificationService);
	}
	
	@Test
	void subscribesWithNotificationTest() {
		final User mockUserFollower = new User();
		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		final NotificationConfiguration notificationConfiguration = new NotificationConfiguration();
		notificationConfiguration.setBySubscriptions(true);
		mockFilmmakerFollowed.setConfiguration(notificationConfiguration);
		final Subscription finalSubscription = new Subscription();
		finalSubscription.setSubscriber(mockUserFollower);
		finalSubscription.setFilmmaker(mockFilmmakerFollowed);

		when(subscriptionRepository.findBySubscriberAndFilmmaker(mockUserFollower, mockFilmmakerFollowed)).thenReturn(Optional.empty());
		
		subscriptionService.subscribeTo(mockUserFollower, mockFilmmakerFollowed);

		verify(subscriptionRepository, times(1)).findBySubscriberAndFilmmaker(mockUserFollower, mockFilmmakerFollowed);
		verify(subscriptionRepository, times(1)).save(finalSubscription);
		verifyNoMoreInteractions(subscriptionRepository);
		verify(notificationService, only()).sendNotification(eq(mockFilmmakerFollowed),
				eq(NotificationType.SUBSCRIPTION), any(String.class));
	}
	
	@Test
	void subscribesAlreadySuscribedTest() {
		final User mockUserFollower = new User();
		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		final NotificationConfiguration notificationConfiguration = new NotificationConfiguration();
		notificationConfiguration.setBySubscriptions(true);
		mockFilmmakerFollowed.setConfiguration(notificationConfiguration);
		final Subscription finalSubscription = new Subscription();
		finalSubscription.setSubscriber(mockUserFollower);
		finalSubscription.setFilmmaker(mockFilmmakerFollowed);

		when(subscriptionRepository.findBySubscriberAndFilmmaker(mockUserFollower, mockFilmmakerFollowed)).thenReturn(Optional.of(new Subscription()));
		
		subscriptionService.subscribeTo(mockUserFollower, mockFilmmakerFollowed);

		verify(subscriptionRepository, only()).findBySubscriberAndFilmmaker(mockUserFollower, mockFilmmakerFollowed);
		verifyNoInteractions(notificationService);
	}

	@Test
	void subscribesWithoutNotificationTest() {
		final User mockUserFollower = new User();
		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		final NotificationConfiguration notificationConfiguration = new NotificationConfiguration();
		notificationConfiguration.setBySubscriptions(false);
		mockFilmmakerFollowed.setConfiguration(notificationConfiguration);
		final Subscription finalSubscription = new Subscription();
		finalSubscription.setSubscriber(mockUserFollower);
		finalSubscription.setFilmmaker(mockFilmmakerFollowed);

		when(subscriptionRepository.findBySubscriberAndFilmmaker(mockUserFollower, mockFilmmakerFollowed)).thenReturn(Optional.empty());
		
		subscriptionService.subscribeTo(mockUserFollower, mockFilmmakerFollowed);

		verify(subscriptionRepository, times(1)).findBySubscriberAndFilmmaker(mockUserFollower, mockFilmmakerFollowed);
		verify(subscriptionRepository, times(1)).save(finalSubscription);
		verifyNoMoreInteractions(subscriptionRepository);
		verifyNoInteractions(notificationService);
	}

	@Test
	void unsubscribesWithNotificationEliminationTest() {
		final User mockUserFollower = new User();
		mockUserFollower.setName("filmmaker1");
		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		
		final Subscription subscription = new Subscription();
		subscription.setSubscriber(mockUserFollower);
		subscription.setFilmmaker(mockFilmmakerFollowed);

		Notification notification = new Notification();
		notification.setText(String.format("%s has subscribed to your profile.", mockUserFollower.getName()));
		mockFilmmakerFollowed.getNotifications().add(notification);
		
		when(subscriptionRepository.findBySubscriberAndFilmmaker(mockUserFollower, mockFilmmakerFollowed)).thenReturn(Optional.of(subscription));
		
		subscriptionService.unsubscribeTo(mockUserFollower, mockFilmmakerFollowed);

		verify(subscriptionRepository, times(1)).findBySubscriberAndFilmmaker(mockUserFollower, mockFilmmakerFollowed);
		verify(subscriptionRepository, times(1)).delete(subscription);
		verifyNoMoreInteractions(subscriptionRepository);
		verify(notificationService, only()).deleteNotification(notification);
	}
	
	@Test
	void unsubscribesNotAlreadySuscribedTest() {
		final User mockUserFollower = new User();
		mockUserFollower.setName("filmmaker1");
		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		
		final Subscription subscription = new Subscription();
		subscription.setSubscriber(mockUserFollower);
		subscription.setFilmmaker(mockFilmmakerFollowed);

		Notification notification = new Notification();
		notification.setText(String.format("%s has subscribed to your profile.", mockUserFollower.getName()));
		mockFilmmakerFollowed.getNotifications().add(notification);
		
		when(subscriptionRepository.findBySubscriberAndFilmmaker(mockUserFollower, mockFilmmakerFollowed)).thenReturn(Optional.empty());
		
		subscriptionService.unsubscribeTo(mockUserFollower, mockFilmmakerFollowed);

		verify(subscriptionRepository, only()).findBySubscriberAndFilmmaker(mockUserFollower, mockFilmmakerFollowed);
		verifyNoInteractions(notificationService);
	}

	@Test
	void unsubscribesWithoutNotificationEliminationTest() {
		final User mockUserFollower = new User();
		mockUserFollower.setName("filmmaker1");
		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		
		final Subscription subscription = new Subscription();
		subscription.setSubscriber(mockUserFollower);
		subscription.setFilmmaker(mockFilmmakerFollowed);
		
		when(subscriptionRepository.findBySubscriberAndFilmmaker(mockUserFollower, mockFilmmakerFollowed)).thenReturn(Optional.of(subscription));
		
		subscriptionService.unsubscribeTo(mockUserFollower, mockFilmmakerFollowed);

		verify(subscriptionRepository, times(1)).findBySubscriberAndFilmmaker(mockUserFollower, mockFilmmakerFollowed);
		verify(subscriptionRepository, times(1)).delete(subscription);
		verifyNoMoreInteractions(subscriptionRepository);
		verifyNoInteractions(notificationService);
	}
}
