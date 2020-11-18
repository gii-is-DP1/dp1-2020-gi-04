package io.github.fourfantastics.standby.repository;

import org.springframework.data.repository.CrudRepository;



import io.github.fourfantastics.standby.model.Comment;

public interface CommentRepository extends CrudRepository<Comment, Long> {

}
