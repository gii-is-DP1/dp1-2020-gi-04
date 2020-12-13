package io.github.fourfantastics.standby.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Rating;
import io.github.fourfantastics.standby.repository.RatingRepository;

@Service
public class RatingService {
	@Autowired
	RatingRepository ratingRepository;

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
}
