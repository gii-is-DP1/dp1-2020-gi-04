package io.github.fourfantastics.standby.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.github.fourfantastics.standby.model.ShortFilm;

@Repository
public interface ShortFilmRepository extends CrudRepository<ShortFilm, Long> {
	Optional<ShortFilm> findByTitle(String title);
}
