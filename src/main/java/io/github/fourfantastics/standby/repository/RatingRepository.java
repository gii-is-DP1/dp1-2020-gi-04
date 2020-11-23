package io.github.fourfantastics.standby.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import io.github.fourfantastics.standby.model.Rating;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;

public interface RatingRepository extends CrudRepository<Rating,Long>{

	public List<Rating> findByUser(User u);
	
	public List<Rating> findByShortFilm(ShortFilm shortFilm);
	
	public Optional<Rating> findByUserAndShortFilm(User user, ShortFilm shortFilm);

}
