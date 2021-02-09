package io.github.fourfantastic.standby.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.model.NotificationType;
import io.github.fourfantastics.standby.model.Rating;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.RatingRepository;
import io.github.fourfantastics.standby.service.NotificationService;
import io.github.fourfantastics.standby.service.RatingService;
import io.github.fourfantastics.standby.service.ShortFilmService;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class RatingServiceTest {
	RatingService ratingService;

	@Mock
	RatingRepository ratingRepository;
	
	@Mock
	ShortFilmService shortFilmService;
	
	@Mock
	NotificationService notificationService;

	@BeforeEach
	public void setup() {
		ratingService = new RatingService(ratingRepository, shortFilmService, notificationService);
		
		when(ratingRepository.save(any(Rating.class))).then(AdditionalAnswers.returnsFirstArg());
	}

	@Test
	public void rateShortFilmWithNotificationTest() {
		final ShortFilm mockShortFilm = new ShortFilm();
		final Filmmaker mockUploader = new Filmmaker();
		mockShortFilm.setUploader(mockUploader);
		final NotificationConfiguration configuration = new NotificationConfiguration();
		configuration.setByRatings(true);
		mockUploader.setConfiguration(configuration);
		
		final User mockUser = new User();
		final Integer grade = 5;

		when(ratingRepository.findByUserAndShortFilm(mockUser, mockShortFilm)).thenReturn(Optional.empty());

		Rating rating = ratingService.rateShortFilm(mockShortFilm, mockUser, grade);

		assertThat(rating.getGrade()).isEqualTo(grade);
		assertThat(rating.getUser()).isEqualTo(mockUser);
		assertThat(rating.getShortFilm()).isEqualTo(mockShortFilm);
		assertNotNull(rating.getDate());

		verify(ratingRepository, times(1)).findByUserAndShortFilm(mockUser, mockShortFilm);
		verify(ratingRepository, times(1)).save(rating);
		verify(ratingRepository, times(1)).averageShortFilmRating(mockShortFilm.getId());
		verifyNoMoreInteractions(ratingRepository);
		verify(shortFilmService, only()).save(mockShortFilm);
		verify(notificationService, only()).sendNotification(eq(mockUploader), eq(NotificationType.RATING), anyString());
	}
	
	@Test
	public void rateShortFilmWithoutNotificationTest() {
		final ShortFilm mockShortFilm = new ShortFilm();
		final Filmmaker mockUploader = new Filmmaker();
		mockShortFilm.setUploader(mockUploader);
		final NotificationConfiguration configuration = new NotificationConfiguration();
		configuration.setByRatings(false);
		mockUploader.setConfiguration(configuration);
		
		final User mockUser = new User();
		final Integer grade = 5;

		when(ratingRepository.findByUserAndShortFilm(mockUser, mockShortFilm)).thenReturn(Optional.empty());

		Rating rating = ratingService.rateShortFilm(mockShortFilm, mockUser, grade);

		assertThat(rating.getGrade()).isEqualTo(grade);
		assertThat(rating.getUser()).isEqualTo(mockUser);
		assertThat(rating.getShortFilm()).isEqualTo(mockShortFilm);
		assertNotNull(rating.getDate());

		verify(ratingRepository, times(1)).findByUserAndShortFilm(mockUser, mockShortFilm);
		verify(ratingRepository, times(1)).save(rating);
		verify(ratingRepository, times(1)).averageShortFilmRating(mockShortFilm.getId());
		verifyNoMoreInteractions(ratingRepository);
		verify(shortFilmService, only()).save(mockShortFilm);
		verifyNoInteractions(notificationService);
	}

	@Test
	public void rateShortFilmAlreadyExistedTest() {
		final ShortFilm mockShortFilm = new ShortFilm();
		final Filmmaker mockUploader = new Filmmaker();
		mockShortFilm.setUploader(mockUploader);
		final NotificationConfiguration configuration = new NotificationConfiguration();
		configuration.setByRatings(true);
		mockUploader.setConfiguration(configuration);
		
		final User mockUser = new User();
		final Integer grade = 5;
		final Rating previousRating = new Rating();
		previousRating.setGrade(2);
		previousRating.setShortFilm(mockShortFilm);
		previousRating.setUser(mockUser);

		when(ratingRepository.findByUserAndShortFilm(mockUser, mockShortFilm)).thenReturn(Optional.of(previousRating));

		Rating rating = ratingService.rateShortFilm(mockShortFilm, mockUser, grade);

		assertThat(rating.getGrade()).isEqualTo(grade);
		assertThat(rating.getUser()).isEqualTo(mockUser);
		assertThat(rating.getShortFilm()).isEqualTo(mockShortFilm);
		assertNotNull(rating.getDate());

		verify(ratingRepository, times(1)).findByUserAndShortFilm(mockUser, mockShortFilm);
		verify(ratingRepository, times(1)).save(rating);
		verify(ratingRepository, times(1)).averageShortFilmRating(mockShortFilm.getId());
		verifyNoMoreInteractions(ratingRepository);
		verify(shortFilmService, only()).save(mockShortFilm);
		verify(notificationService, only()).sendNotification(eq(mockUploader), eq(NotificationType.RATING), anyString());
	}
}
