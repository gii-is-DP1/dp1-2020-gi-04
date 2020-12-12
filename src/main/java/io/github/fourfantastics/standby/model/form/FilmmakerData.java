package io.github.fourfantastics.standby.model.form;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import io.github.fourfantastics.standby.model.Filmmaker;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class FilmmakerData {	
	@NotNull
	String fullname;

	@NotNull
	String country;

	@NotNull
	String city;

	@NotNull
	String phone;
	
	@NotNull
	Boolean byComments;
	
	@NotNull
	Boolean byRatings;
	
	@NotNull
	Boolean bySubscriptions;
	
	public static FilmmakerData fromFilmmaker(Filmmaker filmmaker) {
		FilmmakerData filmmakerData = new FilmmakerData();
		filmmakerData.setFullname(filmmaker.getFullname());
		filmmakerData.setPhone(filmmaker.getPhone());
		filmmakerData.setCountry(filmmaker.getCountry());
		filmmakerData.setCity(filmmaker.getCity());
		filmmakerData.setByComments(filmmaker.getConfiguration().getByComments());
		filmmakerData.setByRatings(filmmaker.getConfiguration().getByRatings());
		filmmakerData.setBySubscriptions(filmmaker.getConfiguration().getBySubscriptions());
		return filmmakerData;
	}
	
	public void copyToFilmmaker(Filmmaker filmmaker) {
		filmmaker.setFullname(getFullname());
		filmmaker.setPhone(getPhone());
		filmmaker.setCountry(getCountry());
		filmmaker.setCity(getCity());
		filmmaker.getConfiguration().setByComments(getByComments());
		filmmaker.getConfiguration().setByRatings(getByRatings());
		filmmaker.getConfiguration().setBySubscriptions(getBySubscriptions());
	}
}
