package io.github.fourfantastics.standby.model.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import io.github.fourfantastics.standby.model.form.FilmmakerRegisterData;

@Component
public class FilmmakerRegisterDataValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return FilmmakerRegisterData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		FilmmakerRegisterData filmmakerRegisterData = (FilmmakerRegisterData) target;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "", "Name cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "", "Email cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "", "Password cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "",
				"Password confirmation cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fullname", "", "Full name cannot be empty!");
		
		if (!filmmakerRegisterData.getPassword().equals(filmmakerRegisterData.getConfirmPassword())) {
			errors.rejectValue("confirmPassword", "", "Passwords don't match!");
		}
	}
}
