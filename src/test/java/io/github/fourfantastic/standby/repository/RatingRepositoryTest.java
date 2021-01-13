package io.github.fourfantastic.standby.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.Rating;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.repository.FilmmakerRepository;
import io.github.fourfantastics.standby.repository.RatingRepository;
import io.github.fourfantastics.standby.repository.ShortFilmRepository;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class RatingRepositoryTest {
	@Autowired
	RatingRepository ratingRepository;
	
	@Autowired
	ShortFilmRepository shortFilmRepository;
	
	@Autowired
	FilmmakerRepository filmmakerRepository;
	
	@Test
	public void averageShortFilmRatingTest() {
		Filmmaker filmmaker = new Filmmaker();
		filmmaker.setName("filmmaker1");
		filmmaker.setPassword("password");
		filmmaker.setEmail("filmmaker@gmail.com");
		filmmaker.setPhotoUrl(null);
		filmmaker.setCity("Seville");
		filmmaker.setCountry("Spain");
		filmmaker.setFullname("Filmmaker Díaz García");
		filmmaker.setPhone("675987432");
		filmmaker.setCreationDate(new Date().getTime());
		filmmakerRepository.save(filmmaker);
		
		ShortFilm shortFilm = new ShortFilm();
		shortFilm.setTitle("Test film");
		shortFilm.setVideoUrl("example.mp4");
		shortFilm.setUploadDate(1L);
		shortFilm.setDescription("");
		shortFilm.setUploader(filmmaker);
		shortFilmRepository.save(shortFilm);
		
		for (int i = 1; i < 10; i++) {
			Rating rating = new Rating();
			rating.setGrade(i);
			rating.setDate(new Date().getTime());
			rating.setUser(filmmaker);
			rating.setShortFilm(shortFilm);
			ratingRepository.save(rating);
		}
		
		assertThat(ratingRepository.averageShortFilmRating(1L)).isEqualTo(5);
	}
	
	@Test
	public void averageShortFilmRatingNoRatingsTest() {
		Filmmaker filmmaker = new Filmmaker();
		filmmaker.setName("filmmaker1");
		filmmaker.setPassword("password");
		filmmaker.setEmail("filmmaker@gmail.com");
		filmmaker.setPhotoUrl(null);
		filmmaker.setCity("Seville");
		filmmaker.setCountry("Spain");
		filmmaker.setFullname("Filmmaker Díaz García");
		filmmaker.setPhone("675987432");
		filmmaker.setCreationDate(new Date().getTime());
		filmmakerRepository.save(filmmaker);
		
		ShortFilm shortFilm = new ShortFilm();
		shortFilm.setTitle("Test film");
		shortFilm.setVideoUrl("example.mp4");
		shortFilm.setUploadDate(1L);
		shortFilm.setDescription("");
		shortFilm.setUploader(filmmaker);
		shortFilmRepository.save(shortFilm);
		
		assertThat(ratingRepository.averageShortFilmRating(1L)).isEqualTo(null);
	}
}
