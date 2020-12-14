package io.github.fourfantastics.standby.model.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import io.github.fourfantastics.standby.model.form.CompanyRegisterData;

@Component
public class CompanyRegisterDataValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return CompanyRegisterData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "", "Name cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "companyName", "", "Company name cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "taxIDNumber", "", "Tax ID number cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "businessPhone", "", "Business phone cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "officeAddress", "", "Office address cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "", "Email cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "", "Password cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "",
				"Password confirmation cannot be empty!");
	}
}
