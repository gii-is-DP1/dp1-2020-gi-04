package io.github.fourfantastics.standby.model.form;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import io.github.fourfantastics.standby.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Credentials {
	@NotNull
	@Length(min = 5, max = 64)
	String name;

	@NotNull
	String password;

	public static Credentials fromUser(User user) {
		Credentials credentials = new Credentials();
		credentials.setName(user.getName());
		credentials.setPassword(user.getPassword());
		return credentials;
	}

	public void copyToUser(User user) {
		user.setName(getName());
		user.setPassword(getPassword());
	}
}
