package io.github.fourfantastics.standby.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.model.form.FilmmakerConfigurationData;
import io.github.fourfantastics.standby.model.form.FilmmakerRegisterData;
import io.github.fourfantastics.standby.repository.FilmmakerRepository;
import io.github.fourfantastics.standby.service.exception.NotUniqueException;

@Service
public class FilmmakerService {
	FilmmakerRepository filmmakerRepository;
	NotificationConfigurationService configurationService;
	UserService userService;

	@Autowired
	public FilmmakerService(FilmmakerRepository filmmakerRepository,
			NotificationConfigurationService configurationService, UserService userService) {
		this.filmmakerRepository = filmmakerRepository;
		this.configurationService = configurationService;
		this.userService = userService;
	}

	public Filmmaker registerFilmmaker(FilmmakerRegisterData filmmakerRegisterData) throws NotUniqueException {
		Filmmaker filmmaker = filmmakerRegisterData.toFilmmaker();
		filmmaker = (Filmmaker) userService.register(filmmaker);

		NotificationConfiguration configuration = new NotificationConfiguration();
		configuration.setUser(filmmaker);
		configuration.setByPrivacyRequests(false);
		configuration = configurationService.saveNotificationConfiguration(configuration);
		filmmaker.setConfiguration(configuration);

		return filmmakerRepository.save(filmmaker);
	}
	
	public void updateFilmmakerData(Filmmaker filmmaker, FilmmakerConfigurationData filmmakerConfigurationData) {
		filmmakerConfigurationData.copyToFilmmaker(filmmaker);
		filmmakerRepository.save(filmmaker);
	}
	
	public List<Filmmaker> findFirst3ByNameLike(String name) {
		return filmmakerRepository.findByNameContainingIgnoreCase(name);
	}
}
