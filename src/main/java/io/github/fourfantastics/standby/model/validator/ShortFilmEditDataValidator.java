package io.github.fourfantastics.standby.model.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import io.github.fourfantastics.standby.model.form.ShortFilmEditData;

@Component
public class ShortFilmEditDataValidator implements Validator {
	boolean validateShortFilmData = true;
	boolean validateNewTag = false;
	boolean validateNewRole = false;
	
	public void setValidationTargets(boolean validateShortFilmData, boolean validateNewTag, boolean validateNewRole) {
		this.validateShortFilmData = validateShortFilmData;
		this.validateNewTag = validateNewTag;
		this.validateNewRole = validateNewRole;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return ShortFilmEditData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		if (this.validateShortFilmData) {
			ShortFilmEditData shortFilmEditData = (ShortFilmEditData) target;
			
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "", "Title cannot be empty!");
			
			if (errors.getFieldErrorCount("title") == 0 && shortFilmEditData.getTitle().length() > 128) {
				errors.rejectValue("title", "", "Title cannot be longer than 128 characters");
			}
			
			if (shortFilmEditData.getTitle() != null && shortFilmEditData.getDescription().length() > 10000) {
				errors.rejectValue("description", "", "Description cannot be longer than 10000 characters");
			}
		}
		
		if (this.validateNewTag) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newTagName", "", "Tag cannot be blank!");
		}
		
		if (this.validateNewRole) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newRoleFilmmaker", "", "Filmmaker's username cannot be blank!");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newRoleType", "", "Role type cannot be blank!");
		}
	}
}
