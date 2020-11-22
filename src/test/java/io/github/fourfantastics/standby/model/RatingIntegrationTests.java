package io.github.fourfantastics.standby.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.CommentRepository;
import io.github.fourfantastics.standby.repository.CompanyRepository;
import io.github.fourfantastics.standby.repository.FilmmakerRepository;
import io.github.fourfantastics.standby.repository.NotificationConfigurationRepository;
import io.github.fourfantastics.standby.repository.NotificationRepository;
import io.github.fourfantastics.standby.repository.PrivacyRequestRepository;
import io.github.fourfantastics.standby.repository.RatingRepository;
import io.github.fourfantastics.standby.repository.ShortFilmRepository;
import io.github.fourfantastics.standby.repository.TagRepository;
import io.github.fourfantastics.standby.repository.UserRepository;

@SpringBootTest
class RatingIntegrationTests {

	@Autowired
	CompanyRepository companyRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RatingRepository ratingRepository;
	
	@Autowired
	ShortFilmRepository shortFilmRepository;

	
	@Test
	void contextLoads() {
		Company company = new Company();
		company.setEmail("company@gmail.com");
		company.setName("companyusername");
		company.setPassword("Very strong password");
		company.setPhotoUrl("foto url");
		company.setCreationDate(1L);
		company.setBussinessPhone("6125125125");
		company.setCompanyName("The boring company");
		company.setOfficeAddress("Sillicon Valley");
		company.setTaxIDNumber(1231521512);
		companyRepository.save(company);

		Company savedCompany = companyRepository.findById(1L).orElse(null);
		assertNotNull(savedCompany);

		User savedUser = userRepository.findById(1L).orElse(null);
		assertNotNull(savedUser);
		
		ShortFilm shortFilm = new ShortFilm();
		shortFilm.setName("A new awesome video");
		shortFilm.setFileUrl("file://video.mp4");
		shortFilm.setUploadDate(1L);
		shortFilm.setDescription("Description");
		
		

		ShortFilm savedFilm = shortFilmRepository.save(shortFilm);
	
		
		ShortFilm savedShortFilm = shortFilmRepository.findById(savedFilm.getId()).orElse(null);
		assertNotNull(savedShortFilm);
		
		
		Rating rating = new Rating();

		rating.setUser(company);
		rating.setShortFilm(shortFilm);
		rating.setGrade(3);
		rating.setDate(1L);

		Rating savedRating = ratingRepository.save(rating);
		
		Rating returnedRating = ratingRepository.findById(savedRating.getId()).orElse(null);
		assertNotNull(returnedRating);
		
		Rating commonRating = ratingRepository.findByUserAndShortFilm(company, shortFilm).orElse(null);
		assertNotNull(commonRating);
		

		

	}

}
