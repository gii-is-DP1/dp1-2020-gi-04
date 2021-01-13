package io.github.fourfantastic.standby.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import io.github.fourfantastics.standby.model.Rating;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.RatingRepository;
import io.github.fourfantastics.standby.service.RatingService;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class RatingServiceTest {
	RatingService ratingService;

	@Mock
	RatingRepository ratingRepository;

	@BeforeEach
	public void setup() {
		ratingService = new RatingService(ratingRepository);
		when(ratingRepository.save(any(Rating.class))).then(AdditionalAnswers.returnsFirstArg());
	}

	@Test
	public void rateShortFilmTest() {
		final ShortFilm mockShortFilm = new ShortFilm();
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
		verifyNoMoreInteractions(ratingRepository);

	}

	@Test
	public void rateShortFilmAlreadyExistedTest() {
		final ShortFilm mockShortFilm = new ShortFilm();
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
		verifyNoMoreInteractions(ratingRepository);

	}
}
