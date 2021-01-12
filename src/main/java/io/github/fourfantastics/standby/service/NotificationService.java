package io.github.fourfantastics.standby.service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.NotificationType;
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

	public void deleteNotification(Notification notification) {
		notificationRepository.delete(notification);
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

	public void sendNotification(User receiver, NotificationType type, String text) {
		Notification notification = new Notification();
		notification.setEmissionDate(new Date().getTime());
		notification.setText(text);
		notification.setUser(receiver);
		notification.setType(type);
		notificationRepository.save(notification);
	}

	public void sendPrivacyRequestNotification(String senderName, User receiver) {
		sendNotification(receiver, NotificationType.PRIVACY_REQUEST,
				String.format("%s wants to know more about you ;)", senderName));
	}

	public void sendPrivacyRequestResponseNotification(String senderName, User receiver, Boolean accepted) {
		sendNotification(receiver, NotificationType.PRIVACY_REQUEST,
				String.format("%s has %s your petition", senderName, accepted ? "accepted" : "declined"));
	}
}
