package io.github.fourfantastics.standby.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Notification;
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

	public void saveNotification(Notification notification) {
		notificationRepository.save(notification);
	}
}
