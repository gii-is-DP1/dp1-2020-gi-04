package io.github.fourfantastics.standby.model.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import io.github.fourfantastics.standby.model.form.ShortFilmViewData;

@Component
public class ShortFilmViewDataValidator implements Validator{
	@Override
	public boolean supports(Class<?> clazz) {
		return ShortFilmViewData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newCommentText", "", "Comment text cannot be empty!");
	}
}
