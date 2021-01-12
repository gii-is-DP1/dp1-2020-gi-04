package io.github.fourfantastics.standby.service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Rating;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.RatingRepository;

@Service
public class RatingService {
	RatingRepository ratingRepository;

	@Autowired
	public RatingService(RatingRepository ratingRepository) {
		this.ratingRepository = ratingRepository;
	}

	public Optional<Rating> getShortRoleById(Long id) {
		return ratingRepository.findById(id);
	}

	public void saveRating(Rating rating) {
		ratingRepository.save(rating);
	}

	public Set<Rating> getAllGrades() {
		Set<Rating> grades = new HashSet<>();
		Iterator<Rating> iterator = ratingRepository.findAll().iterator();
		while (iterator.hasNext()) {
			grades.add(iterator.next());
		}
		return grades;
	}

	public Long getRatingCount(ShortFilm shortFilm) {
		return ratingRepository.countByShortFilm(shortFilm);
	}

	public Double getAverageRating(ShortFilm shortFilm) {
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
		
		return ratingRepository.save(rating);
	}

	public Rating getRatingByUserAndShortFilm(User user, ShortFilm shortFilm) {
		return ratingRepository.findByUserAndShortFilm(user, shortFilm).orElse(null);
	}

	public void removeRating(User user, ShortFilm shortFilm) {
		ratingRepository.deleteByUserAndShortFilm(user, shortFilm);
	}
}
