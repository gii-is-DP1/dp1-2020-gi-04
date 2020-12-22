package io.github.fourfantastics.standby.repository;

import org.springframework.data.repository.CrudRepository;

import io.github.fourfantastics.standby.model.Tag;

public interface TagRepository extends CrudRepository<Tag, Long> {

}
