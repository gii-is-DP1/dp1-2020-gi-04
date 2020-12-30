package io.github.fourfantastics.standby.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import io.github.fourfantastics.standby.model.Company;

public interface CompanyRepository extends CrudRepository<Company, Long> {
	public Optional<Company> findByName(String name);
}
