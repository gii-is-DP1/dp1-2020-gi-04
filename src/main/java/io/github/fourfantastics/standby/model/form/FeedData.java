package io.github.fourfantastics.standby.model.form;

import java.util.List;

import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FeedData {
	User user;
	
	Pagination followedShortFilmsPag = Pagination.empty();
	
	List<ShortFilm> followedShortFilms;
}
