package io.github.fourfantastics.standby.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Comment;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.repository.CommentRepository;

@Service
public class CommentService {
	CommentRepository commentRepository;

	@Autowired
	public CommentService(CommentRepository commentRepository) {
		this.commentRepository = commentRepository;
	}

	public Optional<Comment> getCommentById(Long id) {
		return commentRepository.findById(id);
	}
	
	public Integer getCommentCountByShortFilm(ShortFilm shortFilm) {
		return commentRepository.countByShortFilm(shortFilm);
	}
	
	public Page<Comment> getCommentsByShortFilm(ShortFilm shortFilm, Pageable pageable) {
		return commentRepository.findByShortFilm(shortFilm, pageable);
	}

	public void saveComment(Comment comment) {
		commentRepository.save(comment);
	}
}
