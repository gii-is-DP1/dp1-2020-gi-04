package io.github.fourfantastics.standby.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.Tag;

@Repository
public interface ShortFilmRepository extends CrudRepository<ShortFilm, Long> {
	
	@Query("SELECT tags FROM ShortFilm sf JOIN sf.tags tags WHERE sf.id = :id")
	List<Tag> findTagsByShortFilmId(@Param("id") Long id);
}
