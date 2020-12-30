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
import io.github.fourfantastics.standby.model.form.FilmmakerConfigurationData;
import io.github.fourfantastics.standby.model.validator.FilmmakerConfigurationDataValidator;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class FilmmakerConfigurationDataValidatorTest {
	@Autowired
	FilmmakerConfigurationDataValidator validator;
	
	@Test
	public void supportTest() {
		assertTrue(validator.supports(FilmmakerConfigurationData.class));
		assertFalse(validator.supports(Object.class));
	}
	
	@Test
	public void validateTest() {
		final FilmmakerConfigurationData mockData = new FilmmakerConfigurationData();
		mockData.setCity("Seville");
		mockData.setCountry("Spain");
		mockData.setFullname("Filmmaker Díaz García");
		mockData.setPhone("675987432");
		mockData.setByComments(true);
		mockData.setByRatings(true);
		mockData.setBySubscriptions(true);
		
		BindException errors = new BindException(mockData, "FilmmakerConfigurationData");
		validator.validate(mockData, errors);
		
		assertFalse(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertFalse(errors.hasFieldErrors());
	}
	
	@Test
	public void validateMissingDataTest() {
		final FilmmakerConfigurationData mockData = new FilmmakerConfigurationData();
		mockData.setCity("Seville");
		mockData.setCountry("Spain");
		mockData.setFullname("              ");
		mockData.setPhone("675987432");
		mockData.setByComments(true);
		mockData.setBySubscriptions(true);
		
		BindException errors = new BindException(mockData, "FilmmakerConfigurationData");
		validator.validate(mockData, errors);
		
		assertTrue(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertTrue(errors.hasFieldErrors());
		
		assertThat(errors.getFieldErrorCount("city")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("country")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("fullname")).isEqualTo(1);
		assertThat(errors.getFieldErrorCount("phone")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("byComments")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("byRatings")).isEqualTo(1);
		assertThat(errors.getFieldErrorCount("bySubscriptions")).isEqualTo(0);
	}
}
