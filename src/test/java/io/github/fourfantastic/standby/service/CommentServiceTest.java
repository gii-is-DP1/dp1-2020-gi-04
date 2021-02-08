package io.github.fourfantastic.standby.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Comment;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.model.NotificationType;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.CommentRepository;
import io.github.fourfantastics.standby.service.CommentService;
import io.github.fourfantastics.standby.service.NotificationService;
import io.github.fourfantastics.standby.service.exception.NotFoundException;
import io.github.fourfantastics.standby.service.exception.UnauthorizedException;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class CommentServiceTest {
	CommentService commentService;

	@Mock
	CommentRepository commentRepository;
	
	@Mock
	NotificationService notificationService;

	@BeforeEach
	public void setup() {
		commentService = new CommentService(commentRepository, notificationService);
	}

	@Test
	public void commentShortFilmWithNotificationTest() {
		final ShortFilm mockShortFilm = new ShortFilm();
		final Filmmaker mockUploader = new Filmmaker();
		mockShortFilm.setUploader(mockUploader);
		final NotificationConfiguration configuration = new NotificationConfiguration();
		configuration.setByComments(true);
		mockUploader.setConfiguration(configuration);
		
		final User mockSender = new User();
		
		final String text = "Hey! this is an example comment\nWow, and multiline text";

		assertDoesNotThrow(() -> {
			Comment comment = commentService.commentShortFilm(text, mockShortFilm, mockSender);
			assertThat(comment.getText()).isEqualTo(text);
			assertThat(comment.getUser()).isEqualTo(mockSender);
			assertThat(comment.getShortFilm()).isEqualTo(mockShortFilm);
			assertNotNull(comment.getDate());
		});

		verify(commentRepository, only()).save(any(Comment.class));
		verify(notificationService, only()).sendNotification(eq(mockUploader), eq(NotificationType.COMMENT), anyString());
	}
	
	@Test
	public void commentShortFilmWithoutNotificationTest() {
		final ShortFilm mockShortFilm = new ShortFilm();
		final Filmmaker mockUploader = new Filmmaker();
		mockShortFilm.setUploader(mockUploader);
		final NotificationConfiguration configuration = new NotificationConfiguration();
		configuration.setByComments(false);
		mockUploader.setConfiguration(configuration);
		
		final User mockSender = new User();
		
		
		final String text = "Hey! this is an example comment\nWow, and multiline text";

		assertDoesNotThrow(() -> {
			Comment comment = commentService.commentShortFilm(text, mockShortFilm, mockSender);
			assertThat(comment.getText()).isEqualTo(text);
			assertThat(comment.getUser()).isEqualTo(mockSender);
			assertThat(comment.getShortFilm()).isEqualTo(mockShortFilm);
			assertNotNull(comment.getDate());
		});

		verify(commentRepository, only()).save(any(Comment.class));
		verifyNoInteractions(notificationService);
	}
	
	@Test
	public void commentYourOwnShortFilmWithoutNotificationTest() {
		final ShortFilm mockShortFilm = new ShortFilm();
		final Filmmaker mockUploader = new Filmmaker();
		mockShortFilm.setUploader(mockUploader);
		final NotificationConfiguration configuration = new NotificationConfiguration();
		configuration.setByComments(false);
		mockUploader.setConfiguration(configuration);
		
		
		final String text = "Hey! this is an example comment\nWow, and multiline text";

		assertDoesNotThrow(() -> {
			Comment comment = commentService.commentShortFilm(text, mockShortFilm, mockUploader);
			assertThat(comment.getText()).isEqualTo(text);
			assertThat(comment.getUser()).isEqualTo(mockUploader);
			assertThat(comment.getShortFilm()).isEqualTo(mockShortFilm);
			assertNotNull(comment.getDate());
		});

		verify(commentRepository, only()).save(any(Comment.class));
		verifyNoInteractions(notificationService);
	}
	
	@Test
	public void removeUserCommentTest() {
		final Long commentId = 1L;
		final User mockUser = new User();
		final Comment mockComment = new Comment();
		mockComment.setUser(mockUser);
		
		when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));
		
		assertDoesNotThrow(() -> {
			commentService.removeUserComment(commentId, mockUser);
		});

		verify(commentRepository, times(1)).findById(commentId);
		verify(commentRepository, times(1)).delete(mockComment);
		verifyNoMoreInteractions(commentRepository);
		verifyNoInteractions(notificationService);
	}
	
	@Test
	public void removeUserCommentInvalidComment() {
		final Long commentId = 1L;
		final User mockUser = new User();
		
		when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
		
		assertThrows(NotFoundException.class, () -> {
			commentService.removeUserComment(commentId, mockUser);
		});
		
		verify(commentRepository, only()).findById(commentId);
		verifyNoInteractions(notificationService);
	}
	
	@Test
	public void removeUserCommentUnauthorized() {
		final Long commentId = 1L;
		final User mockUser = new User();
		mockUser.setId(1L);
		final User senderUser = new User();
		senderUser.setId(2L);
		final Comment mockComment = new Comment();
		mockComment.setUser(senderUser);
		
		when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));
		
		assertThrows(UnauthorizedException.class, () -> {
			commentService.removeUserComment(commentId, mockUser);
		});
		
		verify(commentRepository, only()).findById(commentId);
		verifyNoInteractions(notificationService);
	}
}
