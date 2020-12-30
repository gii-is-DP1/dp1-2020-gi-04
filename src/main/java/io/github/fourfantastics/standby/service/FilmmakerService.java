package io.github.fourfantastics.standby.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.NotificationConfiguration;
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

	public Optional<Filmmaker> getFilmmmakerById(Long id) {
		return filmmakerRepository.findById(id);
	}

	public Optional<Filmmaker> getFilmmmakerByName(String name) {
		return filmmakerRepository.findByName(name);
	}

	public void saveFilmmaker(Filmmaker filmmaker) {
		filmmakerRepository.save(filmmaker);
	}

	public Filmmaker registerFilmmaker(FilmmakerRegisterData filmmakerRegisterData) throws NotUniqueException {
		Filmmaker filmmaker = filmmakerRegisterData.toFilmmaker();
		filmmaker = (Filmmaker) userService.register(filmmaker);

		NotificationConfiguration configuration = new NotificationConfiguration();
		configuration.setUser(filmmaker);
		configuration.setByPrivacyRequests(false);
		configuration = configurationService.saveNotificationConfiguration(configuration);
		filmmaker.setConfiguration(configuration);

		return (Filmmaker) userService.saveUser(filmmaker);
	}


}
