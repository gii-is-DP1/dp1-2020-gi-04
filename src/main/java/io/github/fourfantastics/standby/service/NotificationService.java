package io.github.fourfantastics.standby.service;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.NotificationRepository;

@Service
public class NotificationService {
	NotificationRepository notificationRepository;

	@Autowired
	public NotificationService(NotificationRepository notificationRepository) {
		super();
		this.notificationRepository = notificationRepository;
	}

	public Optional<Notification> getNotificationById(Long id) {
		return notificationRepository.findById(id);
	}

	public Notification saveNotification(Notification notification) {
		return notificationRepository.save(notification);
	}

	public Set<Notification> getUnreadNotifications(User user) {
		return notificationRepository.findByUserAndReadDate(user, null);
	}

	public void readNotifications(User user) {
		Set<Notification> notifications = user.getNotifications();
		for (Notification notification : notifications) {
			if (notification.getReadDate() == null) {
				notification.setReadDate(Instant.now().toEpochMilli());
				saveNotification(notification);
			}
		}
	}
}
