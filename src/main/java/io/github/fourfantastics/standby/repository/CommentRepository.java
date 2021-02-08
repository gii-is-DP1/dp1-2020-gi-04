package io.github.fourfantastics.standby.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import io.github.fourfantastics.standby.model.Comment;
import io.github.fourfantastics.standby.model.ShortFilm;

public interface CommentRepository extends CrudRepository<Comment, Long> {
	public Integer countByShortFilm(ShortFilm shortFilm);
	
	public Page<Comment> findByShortFilm(ShortFilm shortFilm, Pageable pageable);
}
