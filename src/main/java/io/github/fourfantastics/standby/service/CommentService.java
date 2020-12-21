package io.github.fourfantastics.standby.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Comment;
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

	public void saveComment(Comment comment) {
		commentRepository.save(comment);
	}
}
