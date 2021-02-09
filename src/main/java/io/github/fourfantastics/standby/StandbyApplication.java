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
import io.github.fourfantastics.standby.service.SubscriptionService;

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
		SubscriptionService subscriptionService;
		
		@Autowired
		public CommandLineAppStartupRunner(FilmmakerService filmmakerService, CompanyService companyService,
				ShortFilmRepository shortFilmRepository,
				NotificationConfigurationService notificationConfigurationService,
				NotificationService notificationService, RatingService ratingService,
				SubscriptionService subscriptionService) {
			this.filmmakerService = filmmakerService;
			this.companyService = companyService;
			this.shortFilmRepository = shortFilmRepository;
			this.notificationConfigurationService = notificationConfigurationService;
			this.notificationService = notificationService;
			this.ratingService = ratingService;
			this.subscriptionService = subscriptionService;
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
			
			ShortFilm shortFilm1 = new ShortFilm();
			shortFilm1.setTitle("Test film2");
			shortFilm1.setVideoUrl("as2d.mp4");
			shortFilm1.setUploadDate(10043256450000L);
			shortFilm1.setDescription("An Awesome Description");
			shortFilm1.setViewCount(0L);
			shortFilm1.setUploader(filmmaker);
			shortFilmRepository.save(shortFilm1);
			
			ShortFilm shortFilm3 = new ShortFilm();
			shortFilm3.setTitle("The Hip Hop");
			shortFilm3.setVideoUrl("as4d.mp4");
			shortFilm3.setUploadDate(1000000L);
			shortFilm3.setDescription("A boring Description");
			shortFilm3.setViewCount(0L);
			shortFilm3.setUploader(filmmaker);
			shortFilmRepository.save(shortFilm3);
			
			ShortFilm shortFilm4 = new ShortFilm();
			shortFilm4.setTitle("Test 300");
			shortFilm4.setVideoUrl("aswd.mp4");
			shortFilm4.setUploadDate(1000343562500L);
			shortFilm4.setDescription("An enthrolling Description");
			shortFilm4.setViewCount(0L);
			shortFilm4.setUploader(filmmaker);
			shortFilmRepository.save(shortFilm4);
			
			ShortFilm shortFilm5 = new ShortFilm();
			shortFilm5.setTitle("Titanic");
			shortFilm5.setVideoUrl("as2qwd.mp4");
			shortFilm5.setUploadDate(100034200L);
			shortFilm5.setDescription("An interesting Description");
			shortFilm5.setViewCount(0L);
			shortFilm5.setUploader(filmmaker);
			shortFilmRepository.save(shortFilm5);
		
			ShortFilm shortFilm6 = new ShortFilm();
			shortFilm6.setTitle("The Tactician");
			shortFilm6.setVideoUrl("as2qwd.mp4");
			shortFilm6.setUploadDate(100034200L);
			shortFilm6.setDescription("A wise Description");
			shortFilm6.setViewCount(0L);
			shortFilm6.setUploader(filmmaker);
			shortFilmRepository.save(shortFilm6);

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
			
			FilmmakerRegisterData filmmakerRegisterData1 = new FilmmakerRegisterData();
			filmmakerRegisterData1.setName("Adelle");
			filmmakerRegisterData1.setPassword("contraseña");
			filmmakerRegisterData1.setEmail("Adelle@gmail.com");
			filmmakerRegisterData1.setCity("Narnia");
			filmmakerRegisterData1.setCountry("Roshar");
			filmmakerRegisterData1.setFullname("Adelle Rodríguez Pérez");
			filmmakerRegisterData1.setPhone("601070926");
			Filmmaker filmmaker1 = filmmakerService.registerFilmmaker(filmmakerRegisterData1);
			
			ShortFilm shortFilm12 = new ShortFilm();
			shortFilm12.setTitle("El lobo de la pared-calle");
			shortFilm12.setVideoUrl("as34d.mp4");
			shortFilm12.setUploadDate(11435245653756L);
			shortFilm12.setDescription("Awesome sinopsis");
			shortFilm12.setViewCount(0L);
			shortFilm12.setUploader(filmmaker1);
			shortFilmRepository.save(shortFilm12);
			
			ShortFilm shortFilm13 = new ShortFilm();
			shortFilm13.setTitle("God is ours");
			shortFilm13.setVideoUrl("asw2d.mp4");
			shortFilm13.setUploadDate(100000900L);
			shortFilm13.setDescription("A pure Description");
			shortFilm13.setViewCount(0L);
			shortFilm13.setUploader(filmmaker1);
			shortFilmRepository.save(shortFilm13);
			
			ShortFilm shortFilm30 = new ShortFilm();
			shortFilm30.setTitle("Abatar");
			shortFilm30.setVideoUrl("as4qd.mp4");
			shortFilm30.setUploadDate(1000002340L);
			shortFilm30.setDescription("A hilarous Description");
			shortFilm30.setViewCount(0L);
			shortFilm30.setUploader(filmmaker1);
			shortFilmRepository.save(shortFilm30);
			
			ShortFilm shortFilm40 = new ShortFilm();
			shortFilm40.setTitle("Almost 299");
			shortFilm40.setVideoUrl("aswd.mp4");
			shortFilm40.setUploadDate(1000342432500L);
			shortFilm40.setDescription("An enthrolling Description");
			shortFilm40.setViewCount(0L);
			shortFilm40.setUploader(filmmaker1);
			shortFilmRepository.save(shortFilm40);
			
			ShortFilm shortFilm50 = new ShortFilm();
			shortFilm50.setTitle("Titan22ic");
			shortFilm50.setVideoUrl("as2qwd.mp4");
			shortFilm50.setUploadDate(1000134534200L);
			shortFilm50.setDescription("An interesting Description");
			shortFilm50.setViewCount(0L);
			shortFilm50.setUploader(filmmaker1);
			shortFilmRepository.save(shortFilm50);
		
			ShortFilm shortFilm60 = new ShortFilm();
			shortFilm60.setTitle("El Bromeador");
			shortFilm60.setVideoUrl("as2qwd.mp4");
			shortFilm60.setUploadDate(10132450034200L);
			shortFilm60.setDescription("A wise Description");
			shortFilm60.setViewCount(0L);
			shortFilm60.setUploader(filmmaker1);
			shortFilmRepository.save(shortFilm60);
			
			subscriptionService.subscribeTo(company, filmmaker1);
			subscriptionService.subscribeTo(company, filmmaker);
			
			FilmmakerRegisterData filmmakerRegisterData2 = new FilmmakerRegisterData();
			filmmakerRegisterData2.setName("Henry");
			filmmakerRegisterData2.setPassword("password");
			filmmakerRegisterData2.setEmail("geralt@gmail.com");
			filmmakerRegisterData2.setCity("Rivia");
			filmmakerRegisterData2.setCountry("Roshar");
			filmmakerRegisterData2.setFullname("Henry Cavill");
			filmmakerRegisterData2.setPhone("60323370926");
			Filmmaker filmmaker2 = filmmakerService.registerFilmmaker(filmmakerRegisterData2);
			
			ShortFilm shortFilm19 = new ShortFilm();
			shortFilm19.setTitle("Lord of the earrings");
			shortFilm19.setVideoUrl("as3w4d.mp4");
			shortFilm19.setUploadDate(11435245653756L);
			shortFilm19.setDescription("Fearless sinopsis");
			shortFilm19.setViewCount(0L);
			shortFilm19.setUploader(filmmaker2);
			shortFilmRepository.save(shortFilm19);
			
			ShortFilm shortFilm17 = new ShortFilm();
			shortFilm17.setTitle("La madrina");
			shortFilm17.setVideoUrl("aswas2d.mp4");
			shortFilm17.setUploadDate(100054600900L);
			shortFilm17.setDescription("A aesthetic Description");
			shortFilm17.setViewCount(0L);
			shortFilm17.setUploader(filmmaker2);
			shortFilmRepository.save(shortFilm17);
			
			ShortFilm shortFilm31 = new ShortFilm();
			shortFilm31.setTitle("The unbroken blade");
			shortFilm31.setVideoUrl("as4qqewd.mp4");
			shortFilm31.setUploadDate(100030020L);
			shortFilm31.setDescription("An essential Description");
			shortFilm31.setViewCount(0L);
			shortFilm31.setUploader(filmmaker2);
			shortFilmRepository.save(shortFilm31);
			
			ShortFilm shortFilm41 = new ShortFilm();
			shortFilm41.setTitle("Trigonometry class");
			shortFilm41.setVideoUrl("aswasd.mp4");
			shortFilm41.setUploadDate(100034432500L);
			shortFilm41.setDescription("A wonderful Description");
			shortFilm41.setViewCount(0L);
			shortFilm41.setUploader(filmmaker2);
			shortFilmRepository.save(shortFilm41);
			
			ShortFilm shortFilm51 = new ShortFilm();
			shortFilm51.setTitle("The sound of the bees");
			shortFilm51.setVideoUrl("as2wsdqwd.mp4");
			shortFilm51.setUploadDate(1000134534200L);
			shortFilm51.setDescription("An intrusmentral Description");
			shortFilm51.setViewCount(0L);
			shortFilm51.setUploader(filmmaker2);
			shortFilmRepository.save(shortFilm51);
		
			ShortFilm shortFilm61 = new ShortFilm();
			shortFilm61.setTitle("Two pieces");
			shortFilm61.setVideoUrl("as2q2wd3.mp4");
			shortFilm61.setUploadDate(10132450034200L);
			shortFilm61.setDescription("A marvelous Description about piracy");
			shortFilm61.setViewCount(0L);
			shortFilm61.setUploader(filmmaker2);
			shortFilmRepository.save(shortFilm61);
		}
	}
}
