package io.github.fourfantastics.standby.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Rating;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.RatingRepository;

@Service
public class RatingService {

	RatingRepository ratingRepository;

	ShortFilmService shortFilmService;

	@Autowired
	public RatingService(RatingRepository ratingRepository, ShortFilmService shortFilmService) {
		this.ratingRepository = ratingRepository;
		this.shortFilmService = shortFilmService;
	}

	public Long getRatingCount(ShortFilm shortFilm) {
		return ratingRepository.countByShortFilm(shortFilm);
	}

	private Double getAverageRating(ShortFilm shortFilm) {
		Double rating = ratingRepository.averageShortFilmRating(shortFilm.getId());
		return rating == null ? 0 : rating;
	}

	public Rating rateShortFilm(ShortFilm shortFilm, User user, Integer rate) {
		Rating rating = ratingRepository.findByUserAndShortFilm(user, shortFilm).orElse(null);
		if (rating == null) {
			rating = new Rating();
			rating.setUser(user);
			rating.setShortFilm(shortFilm);
		}

		rating.setDate(Instant.now().toEpochMilli());
		rating.setGrade(rate);

		Rating savedRating = ratingRepository.save(rating);

		Double avgRating = getAverageRating(shortFilm);
		shortFilm.setRatingAverage(avgRating);

		shortFilmService.save(shortFilm);
		return savedRating;
	}

	public Rating getRatingByUserAndShortFilm(User user, ShortFilm shortFilm) {
		return ratingRepository.findByUserAndShortFilm(user, shortFilm).orElse(null);
	}

	public void removeRating(User user, ShortFilm shortFilm) {
		ratingRepository.deleteByUserAndShortFilm(user, shortFilm);
	}
}
