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
		CompanyRegisterData companyRegisterData = (CompanyRegisterData) target;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "", "Name cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "companyName", "", "Company name cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "taxIDNumber", "", "Tax ID number cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "businessPhone", "", "Business phone cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "officeAddress", "", "Office address cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "", "Email cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "", "Password cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "",
				"Password confirmation cannot be empty!");
		
		if (errors.getFieldErrorCount("name") == 0 && companyRegisterData.getName().length() < 5 || companyRegisterData.getName().length() > 64) {
			errors.rejectValue("name", "", "Name length is out of bounds! (between 5 and 64 characters)");
		}
		
		if (errors.getFieldErrorCount("password") == 0 && errors.getFieldErrorCount("confirmPassword") == 0 && !companyRegisterData.getPassword().equals(companyRegisterData.getConfirmPassword())) {
			errors.rejectValue("confirmPassword", "", "Passwords don't match!");
		}
	}
}
