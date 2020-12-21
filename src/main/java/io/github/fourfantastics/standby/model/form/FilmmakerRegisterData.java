package io.github.fourfantastics.standby.model.form;

import io.github.fourfantastics.standby.model.Filmmaker;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FilmmakerRegisterData {
	// NotNull
	String name;

	// NotNull
	String email;

	// NotNull
	String password;

	// NotNull
	String confirmPassword;

	// NotNull
	String fullname;

	String country;

	String city;

	String phone;

	public Filmmaker toFilmmaker() {
		Filmmaker filmmaker = new Filmmaker();
		filmmaker.setCity(getCity());
		filmmaker.setCountry(getCountry());
		filmmaker.setEmail(getEmail());
		filmmaker.setFullname(getFullname());
		filmmaker.setPassword(getPassword());
		filmmaker.setName(getName());
		filmmaker.setPhone(getPhone());
		return filmmaker;
	}
}
