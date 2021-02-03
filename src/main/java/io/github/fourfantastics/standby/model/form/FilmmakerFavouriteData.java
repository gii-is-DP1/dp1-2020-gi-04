package io.github.fourfantastics.standby.model.form;

import java.util.ArrayList;
import java.util.List;

import io.github.fourfantastics.standby.model.ShortFilm;

public class FilmmakerFavouriteData {

	List<ShortFilm> favouriteShortFilms= new ArrayList<ShortFilm>();
	
	Pagination favouriteShortFilmsPagination = Pagination.empty();
}
