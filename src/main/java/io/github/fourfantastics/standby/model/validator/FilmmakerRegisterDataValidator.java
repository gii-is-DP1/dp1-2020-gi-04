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
		
		if (errors.getFieldErrorCount("name") == 0 && filmmakerRegisterData.getName().length() < 5 || filmmakerRegisterData.getName().length() > 64) {
			errors.rejectValue("name", "", "Name length is out of bounds! (between 5 and 64 characters)");
		}
		
		if (errors.getFieldErrorCount("password") == 0 && errors.getFieldErrorCount("confirmPassword") == 0 && !filmmakerRegisterData.getPassword().equals(filmmakerRegisterData.getConfirmPassword())) {
			errors.rejectValue("confirmPassword", "", "Passwords don't match!");
		}
	}
}
