package io.github.fourfantastics.standby.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import io.github.fourfantastics.standby.model.Rating;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;

public interface RatingRepository extends CrudRepository<Rating, Long> {
	public List<Rating> findByUser(User u);

	public List<Rating> findByShortFilm(ShortFilm shortFilm);

	public Optional<Rating> findByUserAndShortFilm(User user, ShortFilm shortFilm);

	public Long countByShortFilm(ShortFilm shortFilm);

	@Query("SELECT avg(rating.grade) from Rating rating JOIN rating.shortFilm shortFilm WHERE shortFilm.id = :shortFilmId")
	public Double averageShortFilmRating(@Param("shortFilmId") Long shortFilmId);

	public void deleteByUserAndShortFilm(User user, ShortFilm shortFilm);
}
