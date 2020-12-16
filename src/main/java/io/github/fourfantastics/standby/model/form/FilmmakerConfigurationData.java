package io.github.fourfantastics.standby.model.form;

import io.github.fourfantastics.standby.model.Filmmaker;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FilmmakerConfigurationData {
	//NotNull
	String fullname;

	//NotNull
	String country;

	//NotNull
	String city;

	//NotNull
	String phone;

	//NotNull
	Boolean byComments;

	//NotNull
	Boolean byRatings;

	//NotNull
	Boolean bySubscriptions;

	public static FilmmakerConfigurationData fromFilmmaker(Filmmaker filmmaker) {
		FilmmakerConfigurationData filmmakerConfigurationData = new FilmmakerConfigurationData();
		filmmakerConfigurationData.setFullname(filmmaker.getFullname());
		filmmakerConfigurationData.setPhone(filmmaker.getPhone());
		filmmakerConfigurationData.setCountry(filmmaker.getCountry());
		filmmakerConfigurationData.setCity(filmmaker.getCity());
		filmmakerConfigurationData.setByComments(filmmaker.getConfiguration().getByComments());
		filmmakerConfigurationData.setByRatings(filmmaker.getConfiguration().getByRatings());
		filmmakerConfigurationData.setBySubscriptions(filmmaker.getConfiguration().getBySubscriptions());
		return filmmakerConfigurationData;
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
