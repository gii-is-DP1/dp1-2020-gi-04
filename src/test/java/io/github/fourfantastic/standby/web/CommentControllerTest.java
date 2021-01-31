package io.github.fourfantastic.standby.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.form.ShortFilmViewData;
import io.github.fourfantastics.standby.service.CommentService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.NotFoundException;
import io.github.fourfantastics.standby.service.exception.UnauthorizedException;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
@AutoConfigureMockMvc
public class CommentControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	CommentService commentService;

	@MockBean
	UserService userService;

	@MockBean
	ShortFilmService shortFilmService;

	@Test
	public void postCommentTest() {
		final Long shortFilmId = 1L;
		final String viewShortFilmUrl = String.format("/shortfilm/%s", shortFilmId);
		final ShortFilmViewData mockShortFilmViewData = new ShortFilmViewData();
		mockShortFilmViewData.setNewCommentText("This is an example new comment text");
		final ShortFilmViewData returnedShortFilmViewData = new ShortFilmViewData();
		returnedShortFilmViewData.setNewCommentText("");

		when(shortFilmService.getShortFilmById(shortFilmId)).thenReturn(Optional.of(new ShortFilm()));
		when(userService.getLoggedUser()).thenReturn(Optional.of(new User()));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(viewShortFilmUrl).with(csrf()).param("postComment", "").param("newCommentText",
					mockShortFilmViewData.getNewCommentText())).andExpect(status().isFound())
					.andExpect(redirectedUrl(viewShortFilmUrl))
					.andExpect(flash().attribute("shortFilmViewData", returnedShortFilmViewData));
		});

		verify(shortFilmService, only()).getShortFilmById(shortFilmId);
		verify(userService, only()).getLoggedUser();
		verify(commentService, only()).commentShortFilm(mockShortFilmViewData.getNewCommentText(), new ShortFilm(),
				new User());
	}

	@Test
	public void postCommentInvalidShortFilmTest() {
		final Long shortFilmId = 1L;
		final String viewShortFilmUrl = String.format("/shortfilm/%s", shortFilmId);
		final ShortFilmViewData mockShortFilmViewData = new ShortFilmViewData();
		mockShortFilmViewData.setNewCommentText("This is an example new comment text");

		when(shortFilmService.getShortFilmById(shortFilmId)).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(viewShortFilmUrl).with(csrf()).param("postComment", "").param("newCommentText",
					mockShortFilmViewData.getNewCommentText())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/"));
		});

		verify(shortFilmService, only()).getShortFilmById(shortFilmId);
		verifyNoInteractions(userService);
		verifyNoInteractions(commentService);
	}

	@Test
	public void postCommentNotLoggedTest() {
		final Long shortFilmId = 1L;
		final String viewShortFilmUrl = String.format("/shortfilm/%s", shortFilmId);
		final ShortFilmViewData mockShortFilmViewData = new ShortFilmViewData();
		mockShortFilmViewData.setNewCommentText("This is an example new comment text");

		when(shortFilmService.getShortFilmById(shortFilmId)).thenReturn(Optional.of(new ShortFilm()));
		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(viewShortFilmUrl).with(csrf()).param("postComment", "").param("newCommentText",
					mockShortFilmViewData.getNewCommentText())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/login"));
		});

		verify(shortFilmService, only()).getShortFilmById(shortFilmId);
		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(commentService);
	}

	@Test
	public void postCommentInvalidTextTest() {
		final Long shortFilmId = 1L;
		final String viewShortFilmUrl = String.format("/shortfilm/%s", shortFilmId);
		final ShortFilmViewData mockShortFilmViewData = new ShortFilmViewData();
		mockShortFilmViewData.setNewCommentText("           ");

		when(shortFilmService.getShortFilmById(shortFilmId)).thenReturn(Optional.of(new ShortFilm()));
		when(userService.getLoggedUser()).thenReturn(Optional.of(new User()));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(viewShortFilmUrl).with(csrf()).param("postComment", "").param("newCommentText",
					mockShortFilmViewData.getNewCommentText())).andExpect(status().isFound())
					.andExpect(redirectedUrl(viewShortFilmUrl)).andExpect(flash().attributeExists("errors"));
		});

		verify(shortFilmService, only()).getShortFilmById(shortFilmId);
		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(commentService);
	}

	@Test
	public void removeCommentTest() throws NotFoundException, UnauthorizedException {
		final Long shortFilmId = 1L;
		final String viewShortFilmUrl = String.format("/shortfilm/%s", shortFilmId);
		final Long commentId = 2L;
		final ShortFilmViewData mockShortFilmViewData = new ShortFilmViewData();
		mockShortFilmViewData.setWatcherName("Hope the returned data keeps this variable...");

		when(shortFilmService.getShortFilmById(shortFilmId)).thenReturn(Optional.of(new ShortFilm()));
		when(userService.getLoggedUser()).thenReturn(Optional.of(new User()));

		assertDoesNotThrow(() -> {
			mockMvc.perform(
					post(viewShortFilmUrl).with(csrf()).param("watcherName", mockShortFilmViewData.getWatcherName())
							.param("deleteComment", commentId.toString()))
					.andExpect(status().isFound()).andExpect(redirectedUrl(viewShortFilmUrl))
					.andExpect(flash().attribute("shortFilmViewData", mockShortFilmViewData));
		});

		verify(shortFilmService, only()).getShortFilmById(shortFilmId);
		verify(userService, only()).getLoggedUser();
		verify(commentService, only()).removeUserComment(commentId, new User());
	}

	@Test
	public void removeCommentInvalidShortFilmTest() {
		final Long shortFilmId = 1L;
		final String viewShortFilmUrl = String.format("/shortfilm/%s", shortFilmId);
		final Long commentId = 2L;

		when(shortFilmService.getShortFilmById(shortFilmId)).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(viewShortFilmUrl).with(csrf()).param("deleteComment", commentId.toString()))
					.andExpect(status().isFound()).andExpect(redirectedUrl("/"));
		});

		verify(shortFilmService, only()).getShortFilmById(shortFilmId);
		verifyNoInteractions(userService);
		verifyNoInteractions(commentService);
	}

	@Test
	public void removeCommentNotLoggedTest() {
		final Long shortFilmId = 1L;
		final String viewShortFilmUrl = String.format("/shortfilm/%s", shortFilmId);
		final Long commentId = 2L;

		when(shortFilmService.getShortFilmById(shortFilmId)).thenReturn(Optional.of(new ShortFilm()));
		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(viewShortFilmUrl).with(csrf()).param("deleteComment", commentId.toString()))
					.andExpect(status().isFound()).andExpect(redirectedUrl("/login"));
		});

		verify(shortFilmService, only()).getShortFilmById(shortFilmId);
		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(commentService);
	}
}
