package io.github.fourfantastics.standby.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import io.github.fourfantastics.standby.model.Comment;
import io.github.fourfantastics.standby.repository.CommentRepository;

public class CommentService {
	
	@Autowired 
	CommentRepository commentRepository;
	
	public Optional<Comment> getCommentById(Long id){
		return commentRepository.findById(id);
	}
	
	public void saveComment(Comment comment) {
		commentRepository.save(comment);
	}
	
}
