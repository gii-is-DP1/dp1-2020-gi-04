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
import io.github.fourfantastics.standby.model.form.ShortFilmViewData;
import io.github.fourfantastics.standby.model.validator.ShortFilmViewDataValidator;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class ShortFilmViewDataValidatorTest {
	@Autowired
	ShortFilmViewDataValidator validator;
	
	@Test
	public void supportTest() {
		assertTrue(validator.supports(ShortFilmViewData.class));
		assertFalse(validator.supports(Object.class));
	}
	
	@Test
	public void validateTest() {
		final ShortFilmViewData mockData = new ShortFilmViewData();
		mockData.setNewCommentText("Example new comment text");
		
		BindException errors = new BindException(mockData, "ShortFilmEditData");
		validator.validate(mockData, errors);
		
		assertFalse(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertFalse(errors.hasFieldErrors());
	}
	
	@Test
	public void validateMissingDataTest() {
		final ShortFilmViewData mockData = new ShortFilmViewData();
		mockData.setNewCommentText("           ");
		
		BindException errors = new BindException(mockData, "ShortFilmEditData");
		validator.validate(mockData, errors);
		
		assertTrue(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertTrue(errors.hasFieldErrors());
		
		assertThat(errors.getFieldErrorCount("newCommentText")).isEqualTo(1);
	}
}
