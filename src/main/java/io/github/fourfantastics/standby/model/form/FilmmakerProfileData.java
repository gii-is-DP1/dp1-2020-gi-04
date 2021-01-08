package io.github.fourfantastics.standby.model.form;

import java.util.Set;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FilmmakerProfileData {

	Long id;
	
	String name;
	
	String fullname;

	String photoUrl;

	Set<User> filmmakerSubscribers;// My followers

	Set<Filmmaker> filmmakersSubscribedTo;

	Set<ShortFilm> uploadedShortFilms;

	Set<ShortFilm> attachedShortFilms;

	public static FilmmakerProfileData fromFilmmaker(Filmmaker filmmaker) {
		FilmmakerProfileData filmmakerProfileData = new FilmmakerProfileData();
		filmmakerProfileData.setId(filmmaker.getId());
		filmmakerProfileData.setName(filmmaker.getName());
		filmmakerProfileData.setFullname(filmmaker.getFullname());
		filmmakerProfileData.setPhotoUrl(filmmaker.getPhotoUrl());
		filmmakerProfileData.setFilmmakerSubscribers(filmmaker.getFilmmakerSubscribers());
		filmmakerProfileData.setFilmmakersSubscribedTo(filmmaker.getFilmmakersSubscribedTo());
		filmmakerProfileData.setUploadedShortFilms(filmmaker.getUploadedShortFilms());
		return filmmakerProfileData;
	}
}
