package io.github.fourfantastic.standby.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
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

import javax.servlet.http.HttpSession;

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
import io.github.fourfantastics.standby.service.RatingService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.NotFoundException;
import io.github.fourfantastics.standby.service.exception.UnauthorizedException;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
@AutoConfigureMockMvc
public class RatingControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	RatingService ratingService;
	
	@MockBean
	UserService userService;

	@MockBean
	ShortFilmService shortFilmService;
	
	@Test
	public void rateShortFilmTest() {
		final Long shortFilmId = 1L;
		final String viewShortFilmUrl = String.format("/shortfilm/%s", shortFilmId);
		final ShortFilmViewData mockShortFilmViewData = new ShortFilmViewData();
		mockShortFilmViewData.setWatcherName("Hey! keep track of this string");
		final Integer grade = 6;
		
		when(shortFilmService.getShortFilmById(shortFilmId)).thenReturn(Optional.of(new ShortFilm()));
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(new User()));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post(viewShortFilmUrl).with(csrf())
					.param("rate", grade.toString())
					.param("watcherName", mockShortFilmViewData.getWatcherName()))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl(viewShortFilmUrl))
			.andExpect(flash().attribute("shortFilmViewData", mockShortFilmViewData));
		});
		
		verify(shortFilmService, only()).getShortFilmById(shortFilmId);
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verify(ratingService, only()).rateShortFilm(new ShortFilm(), new User(), grade);
	}
	
	@Test
	public void rateInvalidShortFilmTest() {
		final Long shortFilmId = 1L;
		final String viewShortFilmUrl = String.format("/shortfilm/%s", shortFilmId);
		final Integer grade = 6;
		
		when(shortFilmService.getShortFilmById(shortFilmId)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post(viewShortFilmUrl).with(csrf())
					.param("rate", grade.toString()))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("/"));
		});
		
		verify(shortFilmService, only()).getShortFilmById(shortFilmId);
		verifyNoInteractions(userService);
		verifyNoInteractions(ratingService);
	}
	
	@Test
	public void postCommentNotLoggedTest() {
		final Long shortFilmId = 1L;
		final String viewShortFilmUrl = String.format("/shortfilm/%s", shortFilmId);
		final Integer grade = 6;
		
		when(shortFilmService.getShortFilmById(shortFilmId)).thenReturn(Optional.of(new ShortFilm()));
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post(viewShortFilmUrl).with(csrf())
					.param("rate", grade.toString()))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("/login"));
		});
		
		verify(shortFilmService, only()).getShortFilmById(shortFilmId);
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(ratingService);
	}
	
	@Test
	public void rateShortFilmOutOfBoundsTest() {
		final Long shortFilmId = 1L;
		final String viewShortFilmUrl = String.format("/shortfilm/%s", shortFilmId);
		final ShortFilmViewData mockShortFilmViewData = new ShortFilmViewData();
		mockShortFilmViewData.setWatcherName("Hey! keep track of this string");
		final Integer grade = 45;
		
		when(shortFilmService.getShortFilmById(shortFilmId)).thenReturn(Optional.of(new ShortFilm()));
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(new User()));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post(viewShortFilmUrl).with(csrf())
					.param("rate", grade.toString())
					.param("watcherName", mockShortFilmViewData.getWatcherName()))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl(viewShortFilmUrl))
			.andExpect(flash().attribute("shortFilmViewData", mockShortFilmViewData));
		});
		
		verify(shortFilmService, only()).getShortFilmById(shortFilmId);
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verify(ratingService, only()).rateShortFilm(new ShortFilm(), new User(), 10);
	}
	
	@Test
	public void removeCommentTest() throws NotFoundException, UnauthorizedException {
		final Long shortFilmId = 1L;
		final String viewShortFilmUrl = String.format("/shortfilm/%s", shortFilmId);
		final ShortFilmViewData mockShortFilmViewData = new ShortFilmViewData();
		mockShortFilmViewData.setWatcherName("Hey! keep track of this string");
		
		when(shortFilmService.getShortFilmById(shortFilmId)).thenReturn(Optional.of(new ShortFilm()));
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(new User()));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post(viewShortFilmUrl).with(csrf())
					.param("deleteRating", "")
					.param("watcherName", mockShortFilmViewData.getWatcherName()))
					
			.andExpect(status().isFound())
			.andExpect(redirectedUrl(viewShortFilmUrl))
			.andExpect(flash().attribute("shortFilmViewData", mockShortFilmViewData));
		});
		
		verify(shortFilmService, only()).getShortFilmById(shortFilmId);
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verify(ratingService, only()).removeRating(new User(), new ShortFilm());
	}
	
	@Test
	public void removeRatingInvalidShortFilmTest() {
		final Long shortFilmId = 1L;
		final String viewShortFilmUrl = String.format("/shortfilm/%s", shortFilmId);
		final ShortFilmViewData mockShortFilmViewData = new ShortFilmViewData();
		mockShortFilmViewData.setWatcherName("Hey! keep track of this string");
		
		when(shortFilmService.getShortFilmById(shortFilmId)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post(viewShortFilmUrl).with(csrf())
					.param("deleteRating", ""))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("/"));
		});
		
		verify(shortFilmService, only()).getShortFilmById(shortFilmId);
		verifyNoInteractions(userService);
		verifyNoInteractions(ratingService);
	}
	
	@Test
	public void removeRatingNotLoggedTest() {
		final Long shortFilmId = 1L;
		final String viewShortFilmUrl = String.format("/shortfilm/%s", shortFilmId);
		final Long commentId = 2L;
		
		when(shortFilmService.getShortFilmById(shortFilmId)).thenReturn(Optional.of(new ShortFilm()));
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post(viewShortFilmUrl).with(csrf())
					.param("deleteRating", commentId.toString()))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("/login"));
		});
		
		verify(shortFilmService, only()).getShortFilmById(shortFilmId);
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(ratingService);
	}
}
