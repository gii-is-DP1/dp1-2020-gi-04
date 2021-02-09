package io.github.fourfantastics.standby.model.form;

import java.util.ArrayList;
import java.util.List;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.ShortFilm;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FilmmakerProfileData {
	Filmmaker filmmaker;
	
	Integer totalShortFilms;
	
	Integer followerCount;
	
	Integer followedCount;
	
	List<ShortFilm> uploadedShortFilms = new ArrayList<ShortFilm>();

	List<ShortFilm> attachedShortFilms = new ArrayList<ShortFilm>();
	
	Pagination uploadedShortFilmPagination = Pagination.empty();
	
	Pagination attachedShortFilmPagination = Pagination.empty();

	Integer openTab = 0;
}
