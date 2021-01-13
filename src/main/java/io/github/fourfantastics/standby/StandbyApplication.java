package io.github.fourfantastics.standby;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.NotificationType;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.form.CompanyRegisterData;
import io.github.fourfantastics.standby.model.form.FilmmakerRegisterData;
import io.github.fourfantastics.standby.repository.ShortFilmRepository;
import io.github.fourfantastics.standby.service.CompanyService;
import io.github.fourfantastics.standby.service.FilmmakerService;
import io.github.fourfantastics.standby.service.NotificationConfigurationService;
import io.github.fourfantastics.standby.service.NotificationService;
import io.github.fourfantastics.standby.service.RatingService;

@SpringBootApplication
public class StandbyApplication {
	public static void main(String[] args) {
		SpringApplication.run(StandbyApplication.class, args);
	}

	@Profile("!test")
	@Component
	public class CommandLineAppStartupRunner implements CommandLineRunner {
		FilmmakerService filmmakerService;
		CompanyService companyService;
		ShortFilmRepository shortFilmRepository;
		NotificationConfigurationService notificationConfigurationService;
		NotificationService notificationService;
		RatingService ratingService;

		@Autowired
		public CommandLineAppStartupRunner(FilmmakerService filmmakerService, CompanyService companyService,
				ShortFilmRepository shortFilmRepository,
				NotificationConfigurationService notificationConfigurationService,
				NotificationService notificationService, RatingService ratingService) {
			this.filmmakerService = filmmakerService;
			this.companyService = companyService;
			this.shortFilmRepository = shortFilmRepository;
			this.notificationConfigurationService = notificationConfigurationService;
			this.notificationService = notificationService;
			this.ratingService = ratingService;
		}

		@Override
		public void run(String... args) throws Exception {
			FilmmakerRegisterData filmmakerRegisterData = new FilmmakerRegisterData();
			filmmakerRegisterData.setName("filmmaker1");
			filmmakerRegisterData.setPassword("password");
			filmmakerRegisterData.setEmail("filmmaker@gmail.com");
			filmmakerRegisterData.setCity("Seville");
			filmmakerRegisterData.setCountry("Spain");
			filmmakerRegisterData.setFullname("Filmmaker Díaz García");
			filmmakerRegisterData.setPhone("675987432");
			Filmmaker filmmaker = filmmakerService.registerFilmmaker(filmmakerRegisterData);

			notificationService.sendNotification(filmmaker, NotificationType.SUBSCRIPTION, "Test notification 1");
			notificationService.sendNotification(filmmaker, NotificationType.SUBSCRIPTION, "Test notification 2");
			notificationService.sendNotification(filmmaker, NotificationType.SUBSCRIPTION, "Test notification 3");

			ShortFilm shortFilm = new ShortFilm();
			shortFilm.setTitle("Test film");
			shortFilm.setVideoUrl("asd.mp4");
			shortFilm.setUploadDate(1L);
			shortFilm.setDescription("");
			shortFilm.setViewCount(0L);
			shortFilm.setUploader(filmmaker);
			shortFilmRepository.save(shortFilm);

			ratingService.rateShortFilm(shortFilm, filmmaker, 3);

			CompanyRegisterData companyRegisterData = new CompanyRegisterData();
			companyRegisterData.setName("company1");
			companyRegisterData.setPassword("password");
			companyRegisterData.setEmail("business@company.com");
			companyRegisterData.setBusinessPhone("612345678");
			companyRegisterData.setCompanyName("Company Studios");
			companyRegisterData.setOfficeAddress("Calle Manzana 4");
			companyRegisterData.setTaxIDNumber("123-45-1234567");
			Company company = companyService.registerCompany(companyRegisterData);

			ratingService.rateShortFilm(shortFilm, company, 3);
		}
	}
}
