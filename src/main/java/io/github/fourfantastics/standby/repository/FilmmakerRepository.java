package io.github.fourfantastics.standby.repository;

import java.util.List;
import java.util.Optional;


import org.springframework.data.repository.CrudRepository;

import io.github.fourfantastics.standby.model.Filmmaker;

public interface FilmmakerRepository extends CrudRepository<Filmmaker, Long> {
	public Optional<Filmmaker> findByName(String name);
	
	public List<Filmmaker> findByNameContainingIgnoreCase(String name);
}