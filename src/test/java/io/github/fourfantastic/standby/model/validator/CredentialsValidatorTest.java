package io.github.fourfantastic.standby.model.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindException;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.form.Credentials;
import io.github.fourfantastics.standby.model.validator.CredentialsValidator;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class CredentialsValidatorTest {
	@Autowired
	CredentialsValidator validator;
	
	@Test
	public void supportTest() {
		assertTrue(validator.supports(Credentials.class));
		assertFalse(validator.supports(Object.class));
	}
	
	@Test
	public void validateTest() {
		Credentials mockData = new Credentials();
		mockData.setName("company1");
		mockData.setPassword("password");
		
		BindException errors = new BindException(mockData, "Credentials");
		validator.validate(mockData, errors);
		
		assertFalse(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertFalse(errors.hasFieldErrors());
	}
	
	@Test
	public void validateMissingDataTest() {
		Credentials mockData = new Credentials();
		mockData.setName("company1");
		
		BindException errors = new BindException(mockData, "CompanyRegisterData");
		validator.validate(mockData, errors);
		
		assertTrue(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertTrue(errors.hasFieldErrors());
		
		assertThat(errors.getFieldErrorCount("name")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("password")).isEqualTo(1);
	}
}
