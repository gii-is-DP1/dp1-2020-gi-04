package io.github.fourfantastics.standby.model.form;

import java.util.ArrayList;


import java.util.List;

import io.github.fourfantastics.standby.model.Favourite;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserFavouriteShortFilmsData {
	List<Favourite> favouriteShortFilms= new ArrayList<Favourite>();
	
	Pagination favouriteShortFilmPagination = Pagination.empty();

}
