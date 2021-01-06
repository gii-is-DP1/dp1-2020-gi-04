package io.github.fourfantastics.standby.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import io.github.fourfantastics.standby.model.Tag;

public interface TagRepository extends CrudRepository<Tag, Long> {
	public Optional<Tag> findByName(String name);
}
