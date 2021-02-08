package io.github.fourfantastic.standby.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.form.Pagination;
import io.github.fourfantastics.standby.model.form.UserFavouriteShortFilmsData;
import io.github.fourfantastics.standby.service.FavouriteService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.UserService;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
@AutoConfigureMockMvc
public class FavouriteShortFilmControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	ShortFilmService shortFilmService;

	@MockBean
	UserService userService;

	@MockBean
	FavouriteService favouriteService;

	@Test
	public void addFavouriteShortFilmTest() {
		final ShortFilm mockFilm = new ShortFilm();
		mockFilm.setId(6L);
		final User mockUser = new User();

		when(shortFilmService.getShortFilmById(mockFilm.getId())).thenReturn(Optional.of(mockFilm));
		when(userService.getLoggedUser()).thenReturn(Optional.of(mockUser));

		assertDoesNotThrow(() -> {
			mockMvc.perform(
					post(String.format("/shortfilm/%d", mockFilm.getId())).with(csrf()).param("favouriteShortfilm", ""))
					.andExpect(status().isFound()).andExpect(redirectedUrl("/shortfilm/6"));
		});

		verify(shortFilmService, only()).getShortFilmById(mockFilm.getId());
		verify(userService, only()).getLoggedUser();
		verify(favouriteService, only()).favouriteShortFilm(mockFilm, mockUser);
	}

	@Test
	public void addFavouriteNonexistanceShortFilmTest() {
		final Long NonexistanceId = 9L;
		final User mockUser = new User();

		when(shortFilmService.getShortFilmById(NonexistanceId)).thenReturn(Optional.empty());
		when(userService.getLoggedUser()).thenReturn(Optional.of(mockUser));

		assertDoesNotThrow(() -> {
			mockMvc.perform(
					post(String.format("/shortfilm/%d", NonexistanceId)).with(csrf()).param("favouriteShortfilm", ""))
					.andExpect(status().isFound()).andExpect(redirectedUrl("/"));
		});

		verify(shortFilmService, only()).getShortFilmById(NonexistanceId);
		verifyNoInteractions(userService);
		verifyNoInteractions(favouriteService);
	}

	@Test
	public void addFavouriteShortFilmAsUnregisteredUserTest() {
		final ShortFilm mockFilm = new ShortFilm();
		mockFilm.setId(6L);

		when(shortFilmService.getShortFilmById(mockFilm.getId())).thenReturn(Optional.of(mockFilm));
		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(
					post(String.format("/shortfilm/%d", mockFilm.getId())).with(csrf()).param("favouriteShortfilm", ""))
					.andExpect(status().isFound()).andExpect(redirectedUrl("/login"));
		});

		verify(shortFilmService, only()).getShortFilmById(mockFilm.getId());
		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(favouriteService);
	}

	@Test
	public void deleteFavouriteShortFilmTest() {
		final ShortFilm mockFilm = new ShortFilm();
		mockFilm.setId(6L);
		final User mockUser = new User();

		when(shortFilmService.getShortFilmById(mockFilm.getId())).thenReturn(Optional.of(mockFilm));
		when(userService.getLoggedUser()).thenReturn(Optional.of(mockUser));
		doNothing().when(favouriteService).removeFavouriteShortFilm(any(ShortFilm.class), any(User.class));

		assertDoesNotThrow(() -> {
			mockMvc.perform(
					post(String.format("/shortfilm/%d", mockFilm.getId())).with(csrf()).param("deleteFavourite", ""))
					.andExpect(status().isFound()).andExpect(redirectedUrl("/shortfilm/6"));
		});

		verify(shortFilmService, only()).getShortFilmById(mockFilm.getId());
		verify(userService, only()).getLoggedUser();
		verify(favouriteService, only()).removeFavouriteShortFilm(mockFilm, mockUser);
	}

	@Test
	public void deleteFavouriteNonexistanceShortFilmTest() {
		final Long NonexistanceId = 9L;
		final User mockUser = new User();

		when(shortFilmService.getShortFilmById(NonexistanceId)).thenReturn(Optional.empty());
		when(userService.getLoggedUser()).thenReturn(Optional.of(mockUser));
		doNothing().when(favouriteService).removeFavouriteShortFilm(any(ShortFilm.class), any(User.class));

		assertDoesNotThrow(() -> {
			mockMvc.perform(
					post(String.format("/shortfilm/%d", NonexistanceId)).with(csrf()).param("deleteFavourite", ""))
					.andExpect(status().isFound()).andExpect(redirectedUrl("/"));
		});

		verify(shortFilmService, only()).getShortFilmById(NonexistanceId);
		verifyNoInteractions(userService);
		verifyNoInteractions(favouriteService);
	}

	@Test
	public void deleteFavouriteShortFilmAsUnregisteredUserTest() {
		final ShortFilm mockFilm = new ShortFilm();
		mockFilm.setId(6L);

		when(shortFilmService.getShortFilmById(mockFilm.getId())).thenReturn(Optional.of(mockFilm));
		when(userService.getLoggedUser()).thenReturn(Optional.empty());
		doNothing().when(favouriteService).removeFavouriteShortFilm(any(ShortFilm.class), any(User.class));

		assertDoesNotThrow(() -> {
			mockMvc.perform(
					post(String.format("/shortfilm/%d", mockFilm.getId())).with(csrf()).param("deleteFavourite", ""))
					.andExpect(status().isFound()).andExpect(redirectedUrl("/login"));
		});

		verify(shortFilmService, only()).getShortFilmById(mockFilm.getId());
		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(favouriteService);
	}

	@Test
	public void getFavouritesViewTest() {
		final UserFavouriteShortFilmsData userFavouriteShortFilmsData = new UserFavouriteShortFilmsData();
		userFavouriteShortFilmsData.setFavouriteShortFilmPagination(Pagination.empty());
		userFavouriteShortFilmsData.setFavouriteShortFilms(new ArrayList<ShortFilm>());
		userFavouriteShortFilmsData.getFavouriteShortFilmPagination().setTotalElements(0);
		final User mockUser = new User();

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockUser));
		when(favouriteService.getFavouriteShortFilmsByUser(mockUser, Pagination.empty().getPageRequest())).thenReturn(Page.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/favourites"))
					.andExpect(model().attribute("userFavouriteShortFilmsData", userFavouriteShortFilmsData))
					.andExpect(status().isOk()).andExpect(view().name("favourites"));
		});
		
		verify(userService, only()).getLoggedUser();
		verify(favouriteService, only()).getFavouriteShortFilmsByUser(mockUser, Pagination.empty().getPageRequest());
	}
	
	@Test
	public void getFavouritesViewAsUnregisteredUserTest() {
		when(userService.getLoggedUser()).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/favourites"))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("/login"));
		});
		
		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(favouriteService);
	}
}
