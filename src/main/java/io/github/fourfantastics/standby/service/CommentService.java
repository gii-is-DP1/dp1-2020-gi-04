package io.github.fourfantastics.standby.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Comment;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.CommentRepository;
import io.github.fourfantastics.standby.service.exception.NotFoundException;
import io.github.fourfantastics.standby.service.exception.UnauthorizedException;

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

	public void removeComment(Long commentId) {
		commentRepository.deleteById(commentId);
	}
	
	public Comment commentShortFilm(String text, ShortFilm shortFilm, User sender) {
		Comment comment = new Comment();
		comment.setText(text);
		comment.setShortFilm(shortFilm);
		comment.setUser(sender);
		comment.setDate(new Date().getTime());
		saveComment(comment);
		return comment;
	}

	public void removeUserComment(Long commentId, User user) throws NotFoundException, UnauthorizedException {
		Comment comment = commentRepository.findById(commentId).orElse(null);

		if (comment == null) {
			throw new NotFoundException("Comment was not found!");
		}

		if (!comment.getUser().equals(user)) {
			throw new UnauthorizedException("Not authorized to delete comment!");
		}
		
		commentRepository.delete(comment);
	}
}
