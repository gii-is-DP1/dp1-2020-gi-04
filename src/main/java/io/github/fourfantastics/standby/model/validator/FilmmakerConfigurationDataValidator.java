package io.github.fourfantastics.standby.model.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import io.github.fourfantastics.standby.model.form.FilmmakerConfigurationData;

@Component
public class FilmmakerConfigurationDataValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return FilmmakerConfigurationData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fullname", "", "Full name cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "byComments", "", "You must do a selection!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "byRatings", "", "You must do a selection!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "bySubscriptions", "", "You must do a selection!");
	}
}
