package io.github.fourfantastics.standby.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.PrivacyRequest;

public interface PrivacyRequestRepository extends CrudRepository<PrivacyRequest, Long> {
	public Optional<PrivacyRequest> findByFilmmakerAndCompany(Filmmaker filmmaker, Company company);
	
	@Query(value = "SELECT privacyRequest FROM Filmmaker filmmaker JOIN filmmaker.receivedRequests privacyRequest WHERE filmmaker.id = :filmmakerId",
			countQuery = "SELECT count(privacyRequest) FROM Filmmaker filmmaker JOIN filmmaker.receivedRequests privacyRequest WHERE filmmaker.id = :filmmakerId",
			nativeQuery = false)
	public Page<PrivacyRequest> getPrivacyRequestOfFilmmaker(@Param("filmmakerId") Long filmmakerId, Pageable pageable);
}
