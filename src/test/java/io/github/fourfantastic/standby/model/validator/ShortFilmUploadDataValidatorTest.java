package io.github.fourfantastic.standby.model.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindException;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.form.ShortFilmUploadData;
import io.github.fourfantastics.standby.model.validator.ShortFilmUploadDataValidator;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class ShortFilmUploadDataValidatorTest {
	@Autowired
	ShortFilmUploadDataValidator validator;
	
	@Test
	public void supportTest() {
		assertTrue(validator.supports(ShortFilmUploadData.class));
		assertFalse(validator.supports(Object.class));
	}
	
	@Test
	public void validateTest() {
		final ShortFilmUploadData mockData = new ShortFilmUploadData();
		mockData.setTitle("Example title for a ShortFilm");
		mockData.setDescription("Example description for a ShortFilm");
		mockData.setFile(new MockMultipartFile("example", "example.mp4", "video/mp4", "example".getBytes()));
		
		BindException errors = new BindException(mockData, "ShortFilmUploadData");
		validator.validate(mockData, errors);
		
		assertFalse(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertFalse(errors.hasFieldErrors());
	}
	
	@Test
	public void validateMissingDataTest() {
		final ShortFilmUploadData mockData = new ShortFilmUploadData();
		mockData.setTitle("       ");
		mockData.setDescription("Example description for a ShortFilm");
		
		BindException errors = new BindException(mockData, "ShortFilmUploadData");
		validator.validate(mockData, errors);
		
		assertTrue(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertTrue(errors.hasFieldErrors());
		
		assertThat(errors.getFieldErrorCount("title")).isEqualTo(1);
		assertThat(errors.getFieldErrorCount("description")).isEqualTo(0);
		assertThat(errors.getFieldErrorCount("file")).isEqualTo(1);
	}
	
	@Test
	public void validateTooLongDataTest() {
		final ShortFilmUploadData mockData = new ShortFilmUploadData();
		mockData.setTitle(Stream.generate(() -> "a").limit(129).collect(Collectors.joining()));
		mockData.setDescription(Stream.generate(() -> "a").limit(10001).collect(Collectors.joining()));
		mockData.setFile(new MockMultipartFile("example", "example.mp4", "video/mp4", "example".getBytes()));
		
		BindException errors = new BindException(mockData, "ShortFilmUploadData");
		validator.validate(mockData, errors);
		
		assertTrue(errors.hasErrors());
		assertFalse(errors.hasGlobalErrors());
		assertTrue(errors.hasFieldErrors());
		
		assertThat(errors.getFieldErrorCount("title")).isEqualTo(1);
		assertThat(errors.getFieldErrorCount("description")).isEqualTo(1);
		assertThat(errors.getFieldErrorCount("file")).isEqualTo(0);
	}
}
