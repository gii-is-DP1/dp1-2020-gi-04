package io.github.fourfantastics.standby.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import io.github.fourfantastics.standby.model.PrivacyRequest;

public interface PrivacyRequestRepository extends CrudRepository<PrivacyRequest, Long> {

	@Query(value = "SELECT privacyRequest FROM Filmmaker filmmaker JOIN filmmaker.receivedRequests privacyRequest WHERE filmmaker.id = :filmmakerId",
			countQuery = "SELECT count(privacyRequest) FROM Filmmaker filmmaker JOIN filmmaker.receivedRequests privacyRequest WHERE filmmaker.id = :filmmakerId",
			nativeQuery = false)
	public Page<PrivacyRequest> getPrivacyRequestOfFilmmaker(@Param("filmmakerId") Long filmmakerId, Pageable pageable);
	
	@Query("SELECT count(privacyRequest) FROM Filmmaker filmmaker JOIN filmmaker.receivedRequests privacyRequest WHERE filmmaker.id = :filmmakerId")
	public Integer getCountPrivacyRequestOfFilmmaker(@Param("filmmakerId") Long filmmakerId);
}
