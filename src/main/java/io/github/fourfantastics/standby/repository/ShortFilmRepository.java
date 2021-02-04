package io.github.fourfantastics.standby.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.ShortFilm;

public interface ShortFilmRepository extends CrudRepository<ShortFilm, Long> {
	Optional<ShortFilm> findByTitle(String title);

	public Integer countByUploader(Filmmaker uploader);

	public Page<ShortFilm> findByUploader(Filmmaker uploader, Pageable pageable);

	@Query("SELECT count(shortfilm) from ShortFilm shortfilm JOIN shortfilm.roles roles JOIN roles.filmmaker filmmaker WHERE filmmaker.id = :filmmakerId")
	public Integer countAttachedShortFilmByFilmmaker(@Param("filmmakerId") Long filmmakerId);
	
	@Query(value = "SELECT shortfilm from ShortFilm shortfilm JOIN shortfilm.roles roles JOIN roles.filmmaker filmmaker WHERE filmmaker.id = :filmmakerId", 
			countQuery = "SELECT count(shortfilm) from ShortFilm shortfilm JOIN shortfilm.roles roles JOIN roles.filmmaker filmmaker WHERE filmmaker.id = :filmmakerId", 
			nativeQuery = false)
	public Page<ShortFilm> findAttachedShortFilmByFilmmaker(@Param("filmmakerId") Long filmmakerId, Pageable pageable);

	@Query(value = "SELECT shortfilm from User user JOIN user.filmmakersSubscribedTo subscribed JOIN subscribed.uploadedShortFilms shortfilm WHERE user.id = :userId", 
			countQuery = "SELECT count(shortfilm) from User user JOIN user.filmmakersSubscribedTo subscribed JOIN subscribed.uploadedShortFilms shortfilm WHERE user.id = :userId", 
			nativeQuery = false)
	public Page<ShortFilm> followedShortFilms(@Param("userId") Long userId, Pageable pageable);


}
