package io.github.fourfantastic.standby.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.form.FilmmakerRegisterData;
import io.github.fourfantastics.standby.service.FilmmakerService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exceptions.DataMismatchException;
import io.github.fourfantastics.standby.service.exceptions.NotUniqueException;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = StandbyApplication.class)
public class FilmmakerServiceTest {
	@Autowired
	FilmmakerService filmmakerService;

	@Autowired
	UserService userService;

	@Test
	void registerFilmmakerTest() throws DataMismatchException, NotUniqueException {
		final String name = "Filmmaker1";
		final String password = "patata";
		
		FilmmakerRegisterData filmmakerRegisterData = new FilmmakerRegisterData();
		filmmakerRegisterData.setName(name);
		filmmakerRegisterData.setEmail("filmmaker1@gmail.com");
		filmmakerRegisterData.setPassword(password);
		filmmakerRegisterData.setConfirmPassword(password);
		filmmakerRegisterData.setFullname("Filmmaker1 Surname");
		filmmakerRegisterData.setCountry("Spain");
		filmmakerRegisterData.setCity("Seville");
		filmmakerRegisterData.setPhone("678543167");
		
		assertDoesNotThrow(() -> {
			filmmakerService.registerFilmmaker(filmmakerRegisterData);
		});
		
		Optional<Filmmaker> optionalFilmmaker = this.filmmakerService.getFilmmmakerByName(name);
		assertTrue(optionalFilmmaker.isPresent());
		Filmmaker filmmaker = optionalFilmmaker.get();
		
		assertThat(filmmaker.getName().equals(filmmakerRegisterData.getName()));
		assertThat(filmmaker.getEmail().equals(filmmakerRegisterData.getEmail()));
		assertTrue(userService.getEncoder().matches(password, filmmaker.getPassword()));
		
		assertThat(filmmaker.getFullname()).isEqualTo(filmmakerRegisterData.getFullname());
		assertThat(filmmaker.getCountry()).isEqualTo(filmmakerRegisterData.getCountry());
		assertThat(filmmaker.getCity()).isEqualTo(filmmakerRegisterData.getCity());
		assertThat(filmmaker.getPhone()).isEqualTo(filmmakerRegisterData.getPhone());
		
		assertTrue(filmmaker.getUploadedShortFilms().isEmpty());
		assertTrue(filmmaker.getParticipateAs().isEmpty());
		assertTrue(filmmaker.getComments().isEmpty());
		assertTrue(filmmaker.getFavouriteShortFilms().isEmpty());
		assertTrue(filmmaker.getFilmmakersSubscribedTo().isEmpty());
		assertTrue(filmmaker.getNotifications().isEmpty());
	}
	
	@Test
	void registerFilmmakerDuplicatedName() {
		final String name = "Filmmaker1";
		final String password = "patata";
		
		FilmmakerRegisterData filmmakerRegisterData = new FilmmakerRegisterData();
		filmmakerRegisterData.setName(name);
		filmmakerRegisterData.setEmail("filmmaker1@gmail.com");
		filmmakerRegisterData.setPassword(password);
		filmmakerRegisterData.setConfirmPassword(password);
		filmmakerRegisterData.setFullname("Filmmaker1 Surname");
		filmmakerRegisterData.setCountry("Spain");
		filmmakerRegisterData.setCity("Seville");
		filmmakerRegisterData.setPhone("678543167");
		
		assertDoesNotThrow(() -> {
			filmmakerService.registerFilmmaker(filmmakerRegisterData);
		});
		
		assertThrows(NotUniqueException.class, () -> {
			filmmakerService.registerFilmmaker(filmmakerRegisterData);
		});
	}
}
