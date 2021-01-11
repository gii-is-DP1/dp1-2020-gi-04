package io.github.fourfantastics.standby;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.model.NotificationType;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.service.NotificationConfigurationService;
import io.github.fourfantastics.standby.service.NotificationService;
import io.github.fourfantastics.standby.service.RatingService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.UserService;

@SpringBootApplication
public class StandbyApplication {
	public static void main(String[] args) {
		SpringApplication.run(StandbyApplication.class, args);
	}

	@Profile("!test")
	@Component
	public class CommandLineAppStartupRunner implements CommandLineRunner {
		UserService userService;
		ShortFilmService shortFilmService;
		NotificationConfigurationService notificationConfigurationService;
		NotificationService notificationService;
		RatingService ratingService;

		@Autowired
		public CommandLineAppStartupRunner(UserService userService, ShortFilmService shortFilmService,
				NotificationConfigurationService notificationConfigurationService,
				NotificationService notificationService, RatingService ratingService) {
			this.userService = userService;
			this.shortFilmService = shortFilmService;
			this.notificationConfigurationService = notificationConfigurationService;
			this.notificationService = notificationService;
			this.ratingService = ratingService;
		}

		@Override
		public void run(String... args) throws Exception {
			Filmmaker filmmaker = new Filmmaker();
			filmmaker.setName("filmmaker1");
			filmmaker.setPassword("password");
			filmmaker.setEmail("filmmaker@gmail.com");
			filmmaker.setPhotoUrl(null);
			filmmaker.setCity("Seville");
			filmmaker.setCountry("Spain");
			filmmaker.setFullname("Filmmaker Díaz García");
			filmmaker.setPhone("675987432");
			userService.register(filmmaker);

			NotificationConfiguration notificationConfiguration = new NotificationConfiguration();
			notificationConfiguration.setByComments(true);
			notificationConfiguration.setByRatings(true);
			notificationConfiguration.setBySubscriptions(true);
			notificationConfiguration.setByPrivacyRequests(false);
			notificationConfiguration.setUser(filmmaker);
			notificationConfigurationService.saveNotificationConfiguration(notificationConfiguration);
			filmmaker.setConfiguration(notificationConfiguration);
			userService.saveUser(filmmaker);

			Notification notification = new Notification();
			notification.setEmissionDate(Instant.now().toEpochMilli());
			notification.setText("Test notification 1");
			notification.setType(NotificationType.SUBSCRIPTION);
			notification.setUser(filmmaker);

			notificationService.saveNotification(notification);

			notification = new Notification();
			notification.setEmissionDate(Instant.now().toEpochMilli());
			notification.setText("Test notification 2");
			notification.setType(NotificationType.SUBSCRIPTION);
			notification.setUser(filmmaker);

			notificationService.saveNotification(notification);

			notification = new Notification();
			notification.setEmissionDate(Instant.now().toEpochMilli());
			notification.setText("Test notification 3");
			notification.setType(NotificationType.SUBSCRIPTION);
			notification.setUser(filmmaker);

			notificationService.saveNotification(notification);

			ShortFilm shortFilm = new ShortFilm();
			shortFilm.setTitle("Test film");
			shortFilm.setVideoUrl("asd.mp4");
			shortFilm.setUploadDate(1L);
			shortFilm.setDescription("");
			shortFilm.setViewCount(0L);
			shortFilm.setUploader(filmmaker);

			shortFilmService.save(shortFilm);

			ratingService.rateShortFilm(shortFilm, filmmaker, 3);

			Company company = new Company();
			company.setName("company1");
			company.setPassword("password");
			company.setEmail("business@company.com");
			company.setPhotoUrl(null);
			company.setBusinessPhone("612345678");
			company.setCompanyName("Company Studios");
			company.setOfficeAddress("Calle Manzana 4");
			company.setTaxIDNumber("123-45-1234567");
			userService.register(company);

			ratingService.rateShortFilm(shortFilm, company, 3);

			notificationConfiguration = new NotificationConfiguration();
			notificationConfiguration.setByComments(false);
			notificationConfiguration.setByRatings(false);
			notificationConfiguration.setBySubscriptions(false);
			notificationConfiguration.setByPrivacyRequests(true);
			notificationConfiguration.setUser(company);
			notificationConfigurationService.saveNotificationConfiguration(notificationConfiguration);
			company.setConfiguration(notificationConfiguration);
			userService.saveUser(company);
		}
	}
}
