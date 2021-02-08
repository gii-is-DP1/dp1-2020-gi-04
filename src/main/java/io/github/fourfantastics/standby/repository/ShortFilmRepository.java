package io.github.fourfantastics.standby.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.ShortFilm;

public interface ShortFilmRepository extends CrudRepository<ShortFilm, Long>, JpaSpecificationExecutor<ShortFilm> {
	public Page<ShortFilm> findByUploader(Filmmaker uploader, Pageable pageable);
	
	@Query(value = "SELECT shortfilm FROM ShortFilm shortfilm JOIN shortfilm.roles roles JOIN roles.filmmaker filmmaker WHERE filmmaker.id = :filmmakerId", 
			countQuery = "SELECT count(shortfilm) from ShortFilm shortfilm JOIN shortfilm.roles roles JOIN roles.filmmaker filmmaker WHERE filmmaker.id = :filmmakerId", 
			nativeQuery = false)
	public Page<ShortFilm> findAttachedShortFilmByFilmmaker(@Param("filmmakerId") Long filmmakerId, Pageable pageable);
	
	@Query(value = "SELECT shortfilm FROM Subscription subscription JOIN subscription.subscriber subscriber JOIN subscription.filmmaker filmmaker JOIN filmmaker.uploadedShortFilms shortfilm WHERE subscriber.id = :userId", 
			countQuery = "SELECT count(shortfilm) FROM Subscription subscription JOIN subscription.subscriber subscriber JOIN subscription.filmmaker filmmaker JOIN filmmaker.uploadedShortFilms shortfilm WHERE subscriber.id = :userId", 
			nativeQuery = false)
	public Page<ShortFilm> followedShortFilms(@Param("userId") Long userId, Pageable pageable);
}
