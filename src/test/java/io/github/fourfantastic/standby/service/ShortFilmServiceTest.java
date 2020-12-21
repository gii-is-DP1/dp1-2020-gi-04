package io.github.fourfantastic.standby.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.InputStream;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.multipart.MultipartFile;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.form.ShortFilmUploadData;
import io.github.fourfantastics.standby.service.FileService;
import io.github.fourfantastics.standby.service.FilmmakerService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.exceptions.InvalidExtensionException;
import io.github.fourfantastics.standby.service.exceptions.NotUniqueException;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = StandbyApplication.class)
public class ShortFilmServiceTest {
	@Autowired
	ShortFilmService shortFilmService;

	@Autowired
	FilmmakerService filmmakerService;
	
	@Autowired
	FileService fileService;

	@Test
	void uploadTest() throws NotUniqueException {
		final String title = "Title";
		final byte[] content = "This is an example".getBytes();

		ShortFilmUploadData uploadData = new ShortFilmUploadData();
		MultipartFile exampleFile = new MockMultipartFile("example", "example.mp4", "video/mp4", content);
		uploadData.setTitle(title);
		uploadData.setDescription("Description");
		uploadData.setFile(exampleFile);

		assertDoesNotThrow(() -> {
			shortFilmService.upload(uploadData, filmmakerService.getFilmmmakerByName("filmmaker1").get());
		});
		
		Optional<ShortFilm> optionalShortFilm = shortFilmService.getShortFilmByTitle(title);
		assertTrue(optionalShortFilm.isPresent());
		ShortFilm shortFilm = optionalShortFilm.get();
		
		assertThat(shortFilm.getTitle()).isEqualTo(uploadData.getTitle());
		assertThat(shortFilm.getDescription()).isEqualTo(uploadData.getDescription());
		
		File uploadedFile = new File(shortFilm.getFileUrl());
		assertThat(uploadedFile).isNotEqualTo(null);
		
		Resource diskFile = null;
		try {
			diskFile = fileService.load(shortFilm.getFileUrl());
		} catch (Exception e) {
			fail("Couldn't load uploaded file!");
		}
		
		try {
			InputStream inputStream = diskFile.getInputStream();
			assertThat(inputStream.readAllBytes()).isEqualTo(content);
			inputStream.close();
		} catch (Exception e) {
			fail("Couldn't read the uploaded file!");
		}
	}
	
	@Test
	public void uploadInvalidExtensionTest() {
		final String title = "Title";
		final byte[] content = "This is an example".getBytes();

		ShortFilmUploadData uploadData = new ShortFilmUploadData();
		MultipartFile exampleFile = new MockMultipartFile("example", "example.txt", "text/plain", content);
		uploadData.setTitle(title);
		uploadData.setDescription("Description");
		uploadData.setFile(exampleFile);

		assertThrows(InvalidExtensionException.class, () -> {
			shortFilmService.upload(uploadData, filmmakerService.getFilmmmakerByName("filmmaker1").get());
		});
	}
}
