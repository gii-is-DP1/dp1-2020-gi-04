package io.github.fourfantastics.standby.service;

import java.time.Instant;
import java.util.Date;
import java.util.List;

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

	public void deleteNotification(Notification notification) {
		notificationRepository.delete(notification);
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
				notificationRepository.save(notification);
			}
		}
	}

	public Notification sendNotification(User receiver, NotificationType type, String text) {
		Notification notification = new Notification();
		notification.setEmissionDate(new Date().getTime());
		notification.setText(text);
		notification.setUser(receiver);
		notification.setType(type);
		return notificationRepository.save(notification);
	}

	public Notification sendPrivacyRequestNotification(String senderName, User receiver) {
		return sendNotification(receiver, NotificationType.PRIVACY_REQUEST,
				String.format("%s wants to know more about you ;)", senderName));
	}

	public Notification sendPrivacyRequestResponseNotification(String senderName, User receiver, Boolean accepted) {
		return sendNotification(receiver, NotificationType.PRIVACY_REQUEST,
				String.format("%s has %s your petition", senderName, accepted ? "accepted" : "declined"));
	}
}
