package io.github.fourfantastics.standby.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.repository.NotificationConfigurationRepository;

@Service
public class NotificationConfigurationService {
	NotificationConfigurationRepository notificationConfigurationRepository;

	@Autowired
	public NotificationConfigurationService(NotificationConfigurationRepository notificationConfigurationRepository) {
		this.notificationConfigurationRepository = notificationConfigurationRepository;
	}

	public Optional<NotificationConfiguration> getNotificationConfigurationById(Long id) {
		return notificationConfigurationRepository.findById(id);
	}

	public NotificationConfiguration saveNotificationConfiguration(
			NotificationConfiguration notificationConfiguration) {
		return notificationConfigurationRepository.save(notificationConfiguration);
	}
}
