package io.github.fourfantastic.standby.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.form.FilmmakerRegisterData;
import io.github.fourfantastics.standby.service.FilmmakerService;
import io.github.fourfantastics.standby.service.exceptions.DataMismatchException;
import io.github.fourfantastics.standby.service.exceptions.NotUniqueException;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = StandbyApplication.class)
public class FilmmakerServiceTest {
	@Autowired
	protected FilmmakerService filmmakerService;

	@Test
	void getFilmmmakerByIdTest() {
		Optional<Filmmaker> filmmaker1 = this.filmmakerService.getFilmmmakerById(1L);
		assertThat(filmmaker1.isPresent()).isEqualTo(true);

		Optional<Filmmaker> filmmaker2 = this.filmmakerService.getFilmmmakerById(84L);
		assertThat(filmmaker2.isPresent()).isEqualTo(false);
	}

	@Test
	void registerFilmmakerTest() throws DataMismatchException, NotUniqueException {
		FilmmakerRegisterData filmmakerRegisterData = new FilmmakerRegisterData();
		filmmakerRegisterData.setName("Filmmaker1");
		filmmakerRegisterData.setFullname("Filmmaker1 Surname");
		filmmakerRegisterData.setCity("Seville");
		filmmakerRegisterData.setCountry("Spain");
		filmmakerRegisterData.setEmail("filmmaker1@gmail.com");
		filmmakerRegisterData.setPhone("678543167");
		filmmakerRegisterData.setPassword("patata");
		filmmakerRegisterData.setConfirmPassword("patata");
		Filmmaker filmmaker = this.filmmakerService.registerFilmmaker(filmmakerRegisterData);

		Optional<Filmmaker> filmmaker1 = this.filmmakerService.getFilmmmakerById(filmmaker.getId());

		assertThat(filmmaker1.get().getCity()).isEqualTo("Seville");
		assertThat(filmmaker1.get().getFullname()).doesNotMatch("Filmmaker2 Surname");
		assertThat(filmmaker1.get().getComments().isEmpty()).isEqualTo(true);

	}

}
