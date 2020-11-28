package io.github.fourfantastics.standby.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.Tag;

public interface TagRepository extends CrudRepository<Tag, Long> {

	@Query("SELECT tag.movies FROM Tag tag WHERE tag.id = :id")
	Set<ShortFilm> findMoviesByTagId(@Param("id") Long id);
}
