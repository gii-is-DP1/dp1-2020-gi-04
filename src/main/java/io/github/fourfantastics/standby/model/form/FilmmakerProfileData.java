package io.github.fourfantastics.standby.model.form;

import java.util.ArrayList;
import java.util.List;


import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FilmmakerProfileData {
	Filmmaker filmmaker;
	
	Integer totalShortFilms;
	
	List<User> filmmakerSubscribers;// My followers

	List<Filmmaker> filmmakersSubscribedTo;

	List<ShortFilm> uploadedShortFilms= new ArrayList<ShortFilm>();

	List<ShortFilm> attachedShortFilms= new ArrayList<ShortFilm>();
	
	Pagination uploadedShortFilmPagination = Pagination.empty();
	
	Pagination attachedShortFilmPagination = Pagination.empty();

	public static FilmmakerProfileData fromFilmmaker(Filmmaker filmmaker) {
		FilmmakerProfileData filmmakerProfileData = new FilmmakerProfileData();
		filmmakerProfileData.setFilmmaker(filmmaker);
		filmmakerProfileData.setFilmmakerSubscribers(new ArrayList<User>(filmmaker.getFilmmakerSubscribers()));
		filmmakerProfileData.setFilmmakersSubscribedTo(new ArrayList<Filmmaker>(filmmaker.getFilmmakersSubscribedTo()));
		return filmmakerProfileData;
	}
	
	public void  updateFromFilmmaker(Filmmaker filmmaker) {
		this.setFilmmaker(filmmaker);
		this.setFilmmakerSubscribers(new ArrayList<User>(filmmaker.getFilmmakerSubscribers()));
		this.setFilmmakersSubscribedTo(new ArrayList<Filmmaker>(filmmaker.getFilmmakersSubscribedTo()));
	}
}
