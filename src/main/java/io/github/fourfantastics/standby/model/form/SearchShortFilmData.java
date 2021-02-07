package io.github.fourfantastics.standby.model.form;

import java.util.ArrayList;
import java.util.List;

import io.github.fourfantastics.standby.model.ShortFilm;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchShortFilmData {
	List<ShortFilm> searchShortFilms = new ArrayList<ShortFilm>();

	Pagination searchShortFilmPagination = Pagination.empty();

}
