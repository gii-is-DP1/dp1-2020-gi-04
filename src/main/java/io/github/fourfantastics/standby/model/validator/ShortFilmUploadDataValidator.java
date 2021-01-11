package io.github.fourfantastics.standby.model.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import io.github.fourfantastics.standby.model.form.ShortFilmUploadData;

@Component
public class ShortFilmUploadDataValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return ShortFilmUploadData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ShortFilmUploadData shortFilmUploadData = (ShortFilmUploadData) target;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "", "Title cannot be empty!");
		
		if (shortFilmUploadData.getFile() == null || shortFilmUploadData.getFile().isEmpty()) {
			errors.rejectValue("file", "", "You must choose a file to upload!");
		}
		
		if (errors.getFieldErrorCount("description") == 0 && shortFilmUploadData.getTitle().length() > 128) {
			errors.rejectValue("title", "", "Title cannot be longer than 128 characters");
		}
		
		if (shortFilmUploadData.getDescription() != null && shortFilmUploadData.getDescription().length() > 10000) {
			errors.rejectValue("description", "", "Description cannot be longer than 10000 characters");
		}
	}
}
