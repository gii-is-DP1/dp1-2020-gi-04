package io.github.fourfantastics.standby.repository;

import java.util.List;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import io.github.fourfantastics.standby.model.Comment;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;

public interface CommentRepository extends CrudRepository<Comment, Long> {
	public List<Comment> findByUser(User u);

	public List<Comment> findByShortFilm(ShortFilm shortFilm);

	public Optional<Comment> findByUserAndShortFilm(User user, ShortFilm shortFilm);
}
