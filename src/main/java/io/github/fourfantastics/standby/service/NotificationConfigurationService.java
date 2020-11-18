package io.github.fourfantastics.standby.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.repository.NotificationConfigurationRepository;


public class NotificationConfigurationService {
	@Autowired
	NotificationConfigurationRepository notificationConfigurationRepository;
	
	public Optional<NotificationConfiguration> getNotificationConfigurationById(Long id) {
		return notificationConfigurationRepository.findById(id);
	}
	
	public void saveRating(NotificationConfiguration notificationConfiguration) {
		notificationConfigurationRepository.save(notificationConfiguration);
	}

}
