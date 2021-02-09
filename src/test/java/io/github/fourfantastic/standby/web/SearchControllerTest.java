package io.github.fourfantastic.standby.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.form.DateFilter;
import io.github.fourfantastics.standby.model.form.SearchData;
import io.github.fourfantastics.standby.model.form.SortOrder;
import io.github.fourfantastics.standby.model.form.SortType;
import io.github.fourfantastics.standby.service.FilmmakerService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.exception.BadRequestException;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
@AutoConfigureMockMvc
public class SearchControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	ShortFilmService shortFilmService;

	@MockBean
	FilmmakerService filmmakerService;

	@Test
	public void doQuickSearchTest() throws BadRequestException {
		final SearchData mockSearchData = new SearchData();
		mockSearchData.setQ("test");

		when(shortFilmService.searchShortFilms(mockSearchData)).thenReturn(new PageImpl<ShortFilm>(new ArrayList<ShortFilm>()));
		when(filmmakerService.findFirst3ByNameLike(mockSearchData.getQ())).thenReturn(new ArrayList<Filmmaker>());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/search").with(csrf()).param("q", mockSearchData.getQ())).andExpect(status().isOk())
					.andExpect(model().attributeExists("search", "shortFilms", "filmmakers"))
					.andExpect(view().name("search"));
		});
		
		verify(shortFilmService, only()).searchShortFilms(mockSearchData);
		verify(filmmakerService, only()).findFirst3ByNameLike(mockSearchData.getQ());
	}
	
	@Test
	public void doSearchTest() throws BadRequestException {
		final SearchData mockSearchData = new SearchData();
		mockSearchData.setQ("test");
		mockSearchData.setDateFilter(DateFilter.ALL);
		mockSearchData.setSortOrder(SortOrder.ASCENDING);
		mockSearchData.setSortType(SortType.UPLOAD_DATE);
		
		when(shortFilmService.searchShortFilms(mockSearchData)).thenReturn(new PageImpl<ShortFilm>(new ArrayList<ShortFilm>()));
		when(filmmakerService.findFirst3ByNameLike(mockSearchData.getQ())).thenReturn(new ArrayList<Filmmaker>());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/search").with(csrf())
					.param("q", mockSearchData.getQ())
					.param("dateFilter", mockSearchData.getDateFilter().toString())
					.param("sortOrder", mockSearchData.getSortOrder().toString())
					.param("sortType", mockSearchData.getSortType().toString()))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("search", "shortFilms", "filmmakers"))
			.andExpect(view().name("search"));
		});
		
		verify(shortFilmService, only()).searchShortFilms(mockSearchData);
		verify(filmmakerService, only()).findFirst3ByNameLike(mockSearchData.getQ());
	}
	
	@Test
	public void doInvalidSearchTest() throws BadRequestException {
		final SearchData mockSearchData = new SearchData();
		mockSearchData.setQ("");
		
		when(shortFilmService.searchShortFilms(mockSearchData)).thenThrow(BadRequestException.class);
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/search").with(csrf())
					.param("q", mockSearchData.getQ()))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("/"));
		});
		
		verify(shortFilmService, only()).searchShortFilms(mockSearchData);
		verifyNoInteractions(filmmakerService);
	}
}
