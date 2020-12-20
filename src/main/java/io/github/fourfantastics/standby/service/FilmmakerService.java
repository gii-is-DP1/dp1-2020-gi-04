package io.github.fourfantastics.standby.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.model.form.FilmmakerRegisterData;
import io.github.fourfantastics.standby.repository.FilmmakerRepository;
import io.github.fourfantastics.standby.service.exceptions.DataMismatchException;
import io.github.fourfantastics.standby.service.exceptions.NotUniqueException;
import io.github.fourfantastics.standby.utils.Utils;

@Service
public class FilmmakerService {
	@Autowired
	FilmmakerRepository filmmakerRepository;

	@Autowired
	NotificationConfigurationService configurationService;

	@Autowired
	UserService userService;

	public Optional<Filmmaker> getFilmmmakerById(Long id) {
		return filmmakerRepository.findById(id);
	}
	
	public Optional<Filmmaker> getFilmmmakerByName(String name) {
		return filmmakerRepository.findByName(name);
	}

	public void saveFilmmaker(Filmmaker filmmaker) {
		filmmakerRepository.save(filmmaker);
	}

	public Filmmaker registerFilmmaker(FilmmakerRegisterData filmmakerRegisterData)
			throws DataMismatchException, NotUniqueException {
		if (!filmmakerRegisterData.getPassword().equals(filmmakerRegisterData.getConfirmPassword())) {
			throw new DataMismatchException("The password doesn't match", Utils.hashSet("password"));
		}

		Filmmaker filmmaker = filmmakerRegisterData.toFilmmaker();
		filmmaker = (Filmmaker) userService.register(filmmaker);

		NotificationConfiguration configuration = new NotificationConfiguration();
		configuration.setUser(filmmaker);
		configuration.setByPrivacyRequests(false);
		configuration = configurationService.saveNotificationConfiguration(configuration);

		filmmaker.setConfiguration(configuration);
		userService.saveUser(filmmaker);

		return filmmaker;
	}
}
