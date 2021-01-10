package io.github.fourfantastic.standby.model.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindException;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.RoleType;
import io.github.fourfantastics.standby.model.form.ShortFilmEditData;
import io.github.fourfantastics.standby.model.validator.ShortFilmEditDataValidator;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class ShortFilmEditDataValidatorTest {
	@Autowired
	ShortFilmEditDataValidator validator;
	
	@Test
	public void supportTest() {
		assertTrue(validator.supports(ShortFilmEditData.class));
		assertFalse(validator.supports(Object.class));
	}
	
	@Test
	public void validateTest() {
		final ShortFilmEditData mockData = new ShortFilmEditData();
		mockData.setTitle("Example title for a ShortFilm");
		mockData.setDescription("Example description for a ShortFilm");
		mockData.setNewTagName("Action");
		mockData.setNewRoleFilmmaker("filmmaker1");
		mockData.setNewRoleType(RoleType.ACTOR);
		
		BindException errors = new BindException(mockData, "ShortFilmEditData");
		validator.setValidationTargets(true, true, true);
		validator.validate(mockData, errors);
		
		assertFalse(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertFalse(errors.hasFieldErrors());
	}
	
	@Test
	public void validateMissingDataTest() {
		final ShortFilmEditData mockData = new ShortFilmEditData();
		mockData.setDescription("Example description for a ShortFilm");
		mockData.setNewTagName("");
		mockData.setNewRoleFilmmaker("filmmaker1");
		
		BindException errors = new BindException(mockData, "ShortFilmEditData");
		validator.setValidationTargets(true, true, true);
		validator.validate(mockData, errors);
		
		assertTrue(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertTrue(errors.hasFieldErrors());
		
		assertThat(errors.getFieldErrorCount("title")).isEqualTo(1);
		assertThat(errors.getFieldErrorCount("description")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("newTagName")).isEqualTo(1);
		assertThat(errors.getFieldErrorCount("newRoleFilmmaker")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("newRoleType")).isEqualTo(1);
	}
	
	@Test
	public void validateTooLongDataTest() {
		final ShortFilmEditData mockData = new ShortFilmEditData();
		mockData.setTitle(Stream.generate(() -> "a").limit(129).collect(Collectors.joining()));
		mockData.setDescription(Stream.generate(() -> "a").limit(10001).collect(Collectors.joining()));
		
		BindException errors = new BindException(mockData, "ShortFilmEditData");
		validator.setValidationTargets(true, false, false);
		validator.validate(mockData, errors);
		
		assertTrue(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertTrue(errors.hasFieldErrors());
		
		assertThat(errors.getFieldErrorCount("title")).isEqualTo(1);
		assertThat(errors.getFieldErrorCount("description")).isEqualTo(1);
		assertThat(errors.getFieldErrorCount("newTagName")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("newRoleFilmmaker")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("newRoleType")).isEqualTo(0);
	}
}
