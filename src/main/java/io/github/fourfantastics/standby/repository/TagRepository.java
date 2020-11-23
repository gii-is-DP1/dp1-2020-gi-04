package io.github.fourfantastics.standby.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.Tag;

public interface TagRepository extends CrudRepository<Tag, Long> {

}
