package io.github.fourfantastics.standby.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.NotificationType;
import io.github.fourfantastics.standby.model.Subscription;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.SubscriptionRepository;

@Service
public class SubscriptionService {
	SubscriptionRepository subscriptionRepository;
	NotificationService notificationService;
	
	@Autowired
	public SubscriptionService(SubscriptionRepository subscriptionRepository, NotificationService notificationService) {
		this.subscriptionRepository = subscriptionRepository;
		this.notificationService = notificationService;
	}
	
	public void subscribeTo(User follower, Filmmaker followed) {
		if (subscriptionRepository.findBySubscriberAndFilmmaker(follower, followed).isPresent()) {
			return;
		}
		
		Subscription subscription = new Subscription();
		subscription.setFilmmaker(followed);
		subscription.setSubscriber(follower);
		subscriptionRepository.save(subscription);
		
		if (followed.getConfiguration().getBySubscriptions()) {
			notificationService.sendNotification(followed, NotificationType.SUBSCRIPTION,
					String.format("%s has subscribed to your profile.", follower.getName()));
		}
	}

	public void unsubscribeTo(User follower, Filmmaker followed) {
		Subscription subscription = subscriptionRepository.findBySubscriberAndFilmmaker(follower, followed).orElse(null);
		if (subscription == null) {
			return;
		}
		
		subscriptionRepository.delete(subscription);
		
		List<Notification> notification = followed.getNotifications().stream()
				.filter(x -> x.getText().equals(String.format("%s has subscribed to your profile.", follower.getName())))
				.collect(Collectors.toList());
		if (!notification.isEmpty()) {
			followed.getNotifications().remove(notification.get(0));
			notificationService.deleteNotification(notification.get(0));
		}
	}
	
	public Integer getFollowerCount(Filmmaker filmmaker) {
		return subscriptionRepository.countByFilmmaker(filmmaker);
	}
	
	public Integer getFollowedCount(User user) {
		return subscriptionRepository.countBySubscriber(user);
	}
	
	public Boolean isAlreadySubscribedTo(User user, Filmmaker filmmaker) {
		return subscriptionRepository.findBySubscriberAndFilmmaker(user, filmmaker).isPresent();
	}
}
