package io.github.fourfantastics.standby.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	public Integer getUnreadNotifications(User user) {
		return notificationRepository.countByUserAndReadDate(user, null);
	}

	public Page<Notification> getPaginatedNotifications(User user, Pageable pageable) {
		return notificationRepository.findByUser(user, pageable);
	}

	public Integer countNotifications(User user) {
		return notificationRepository.countByUser(user);
	}

	public void readNotifications(List<Notification> notifications) {
		for (Notification notification : notifications) {
			if (notification.getReadDate() == null) {
				notification.setReadDate(Instant.now().toEpochMilli());
				saveNotification(notification);
			}
		}
	}
}
