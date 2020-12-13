package io.github.fourfantastics.standby.model.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import io.github.fourfantastics.standby.model.form.CompanyConfigurationData;

@Component
public class CompanyConfigurationDataValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return CompanyConfigurationData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "companyName", "", "Company name cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "taxIDNumber", "", "Tax ID number cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "businessPhone", "", "Business phone cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "officeAddress", "", "Office address cannot be empty!");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "byPrivacyRequests", "", "You must do a selection!");
	}
}
