package io.github.fourfantastic.standby.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.model.form.FilmmakerRegisterData;
import io.github.fourfantastics.standby.repository.FilmmakerRepository;
import io.github.fourfantastics.standby.service.FilmmakerService;
import io.github.fourfantastics.standby.service.NotificationConfigurationService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.NotUniqueException;

@SpringBootTest(classes = StandbyApplication.class)
public class FilmmakerServiceTest {
	FilmmakerService filmmakerService;

	@Mock
	FilmmakerRepository filmmakerRepository;
	
	@Mock
	UserService userService;
	
	@Mock
	NotificationConfigurationService notificationConfigurationService;

	@BeforeEach
	public void setup() throws NotUniqueException {
		filmmakerService = new FilmmakerService(filmmakerRepository, notificationConfigurationService, userService);
		
		when(userService.register(any(Filmmaker.class))).then(AdditionalAnswers.returnsFirstArg());
		when(notificationConfigurationService.saveNotificationConfiguration(any(NotificationConfiguration.class)))
			.then(AdditionalAnswers.returnsFirstArg());
		when(userService.saveUser(any(Filmmaker.class))).then(AdditionalAnswers.returnsFirstArg());
	}
	
	@Test
	void registerFilmmakerTest() {
		FilmmakerRegisterData filmmakerRegisterData = new FilmmakerRegisterData();
		filmmakerRegisterData.setName("filmmaker1");
		filmmakerRegisterData.setEmail("filmmaker1@gmail.com");
		filmmakerRegisterData.setPassword("password");
		filmmakerRegisterData.setConfirmPassword("password");
		filmmakerRegisterData.setFullname("Filmmaker1");
		filmmakerRegisterData.setCountry("Spain");
		filmmakerRegisterData.setCity("Seville");
		filmmakerRegisterData.setPhone("678543167");
		
		assertDoesNotThrow(() -> {
			Filmmaker filmmaker = filmmakerService.registerFilmmaker(filmmakerRegisterData);
			
			assertThat(filmmaker.getName()).isEqualTo(filmmakerRegisterData.getName());
			assertThat(filmmaker.getEmail()).isEqualTo(filmmakerRegisterData.getEmail());
			assertThat(filmmaker.getPassword()).isEqualTo(filmmakerRegisterData.getPassword());

			assertThat(filmmaker.getFullname()).isEqualTo(filmmakerRegisterData.getFullname());
			assertThat(filmmaker.getCountry()).isEqualTo(filmmakerRegisterData.getCountry());
			assertThat(filmmaker.getCity()).isEqualTo(filmmakerRegisterData.getCity());
			assertThat(filmmaker.getPhone()).isEqualTo(filmmakerRegisterData.getPhone());

			assertTrue(filmmaker.getComments().isEmpty());
			assertTrue(filmmaker.getFavouriteShortFilms().isEmpty());
			assertTrue(filmmaker.getFilmmakersSubscribedTo().isEmpty());
			assertTrue(filmmaker.getNotifications().isEmpty());
			
			verify(userService, times(1)).register(filmmaker);
			verify(notificationConfigurationService, only()).saveNotificationConfiguration(any(NotificationConfiguration.class));
			verify(userService, times(1)).saveUser(filmmaker);
			verifyNoMoreInteractions(userService);
		});
	}
}
