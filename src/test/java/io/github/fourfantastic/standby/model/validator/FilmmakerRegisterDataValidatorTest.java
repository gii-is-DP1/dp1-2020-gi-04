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
import io.github.fourfantastics.standby.model.form.FilmmakerRegisterData;
import io.github.fourfantastics.standby.model.validator.FilmmakerRegisterDataValidator;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class FilmmakerRegisterDataValidatorTest {
	@Autowired
	FilmmakerRegisterDataValidator validator;
	
	@Test
	public void supportTest() {
		assertTrue(validator.supports(FilmmakerRegisterData.class));
		assertFalse(validator.supports(Object.class));
	}
	
	@Test
	public void validateTest() {
		final FilmmakerRegisterData mockData = new FilmmakerRegisterData();
		mockData.setName("filmmaker1");
		mockData.setEmail("filmmaker@gmail.com");
		mockData.setPassword("password");
		mockData.setConfirmPassword("password");
		mockData.setCity("Seville");
		mockData.setCountry("Spain");
		mockData.setFullname("Filmmaker Díaz García");
		mockData.setPhone("675987432");
		
		BindException errors = new BindException(mockData, "FilmmakerRegisterData");
		validator.validate(mockData, errors);
		
		assertFalse(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertFalse(errors.hasFieldErrors());
	}
	
	@Test
	public void validatePasswordMismatchTest() {
		final FilmmakerRegisterData mockData = new FilmmakerRegisterData();
		mockData.setName("filmmaker1");
		mockData.setEmail("filmmaker@gmail.com");
		mockData.setPassword("password");
		mockData.setConfirmPassword("password2");
		mockData.setCity("Seville");
		mockData.setCountry("Spain");
		mockData.setFullname("Filmmaker Díaz García");
		mockData.setPhone("675987432");
		
		BindException errors = new BindException(mockData, "FilmmakerRegisterData");
		validator.validate(mockData, errors);
		
		assertTrue(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertTrue(errors.hasFieldErrors());
		
		assertThat(errors.getFieldErrorCount("name")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("email")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("password")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("confirmPassword")).isEqualTo(1);
		assertThat(errors.getFieldErrorCount("city")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("country")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("fullname")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("phone")).isEqualTo(0);
	}
	
	@Test
	public void validateMissingDataTest() {
		final FilmmakerRegisterData mockData = new FilmmakerRegisterData();
		mockData.setName("        ");
		mockData.setEmail("filmmaker@gmail.com");
		mockData.setPassword("         ");
		mockData.setConfirmPassword("password");
		mockData.setCity("Seville");
		mockData.setCountry("Spain");
		mockData.setFullname("Filmmaker Díaz García");
		mockData.setPhone("675987432");
		
		BindException errors = new BindException(mockData, "FilmmakerRegisterData");
		validator.validate(mockData, errors);
		
		assertTrue(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertTrue(errors.hasFieldErrors());
		
		assertThat(errors.getFieldErrorCount("name")).isEqualTo(1);
		assertThat(errors.getFieldErrorCount("email")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("password")).isEqualTo(1);
		assertThat(errors.getFieldErrorCount("confirmPassword")).isEqualTo(1); /* Due to password != confirm password */
		assertThat(errors.getFieldErrorCount("businessPhone")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("companyName")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("officeAddress")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("taxIDNumber")).isEqualTo(0);
	}
	
	@Test
	public void validateOutOfBoundsTest() {
		final FilmmakerRegisterData mockData = new FilmmakerRegisterData();
		mockData.setName("abc");
		mockData.setEmail("filmmaker@gmail.com");
		mockData.setPassword("password");
		mockData.setConfirmPassword("password");
		mockData.setCity("Seville");
		mockData.setCountry("Spain");
		mockData.setFullname("Filmmaker Díaz García");
		mockData.setPhone("675987432");
		
		BindException errors = new BindException(mockData, "FilmmakerRegisterData");
		validator.validate(mockData, errors);
		
		assertTrue(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertTrue(errors.hasFieldErrors());
		
		assertThat(errors.getFieldErrorCount("name")).isEqualTo(1);
		assertThat(errors.getFieldErrorCount("email")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("password")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("confirmPassword")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("businessPhone")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("companyName")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("officeAddress")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("taxIDNumber")).isEqualTo(0);
	}
}
