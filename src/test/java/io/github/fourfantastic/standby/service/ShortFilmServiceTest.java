package io.github.fourfantastic.standby.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.form.ShortFilmUploadData;
import io.github.fourfantastics.standby.repository.FileRepository;
import io.github.fourfantastics.standby.repository.ShortFilmRepository;
import io.github.fourfantastics.standby.service.FilmmakerService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.exception.InvalidExtensionException;
import io.github.fourfantastics.standby.service.exception.TooBigException;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class ShortFilmServiceTest {
	ShortFilmService shortFilmService;

	@Mock
	ShortFilmRepository shortFilmRepository;

	@Mock
	FileRepository fileRepository;

	@Mock
	FilmmakerService filmmakerService;

	@BeforeEach
	public void setup() throws InvalidExtensionException, RuntimeException {
		shortFilmService = new ShortFilmService(shortFilmRepository, fileRepository);

		final Filmmaker filmmaker1 = new Filmmaker();
		filmmaker1.setId(1L);
		filmmaker1.setName("filmmaker1");
		filmmaker1.setPassword("password");
		filmmaker1.setEmail("filmmaker@gmail.com");
		filmmaker1.setPhotoUrl("url photo");
		filmmaker1.setCity("Seville");
		filmmaker1.setCountry("Spain");
		filmmaker1.setFullname("Filmmaker Díaz García");
		filmmaker1.setPhone("675987432");
		
		when(filmmakerService.getFilmmmakerByName("filmmaker1")).thenReturn(Optional.of(filmmaker1));
		when(shortFilmRepository.save(any(ShortFilm.class))).then(AdditionalAnswers.returnsFirstArg());
	}

	@Test
	void uploadTest() {
		final String extension = ".mp4";
		final long size = 1000L;
		final MultipartFile mockFile = mock(MultipartFile.class);
		
		when(mockFile.getSize()).thenReturn(size);
		when(fileRepository.getFileExtension(mockFile)).thenReturn(extension);
		when(fileRepository.saveFile(eq(mockFile), any(Path.class))).thenReturn(true);

		ShortFilmUploadData uploadData = new ShortFilmUploadData();
		uploadData.setTitle("Title");
		uploadData.setDescription("Description");
		uploadData.setFile(mockFile);

		assertDoesNotThrow(() -> {
			ShortFilm shortFilm = shortFilmService.upload(uploadData,
					filmmakerService.getFilmmmakerByName("filmmaker1").get());

			assertThat(shortFilm.getTitle()).isEqualTo(uploadData.getTitle());
			assertThat(shortFilm.getDescription()).isEqualTo(uploadData.getDescription());
			assertThat(shortFilm.getFileUrl()).isNotEmpty();
			assertNotNull(shortFilm.getUploadDate());

			verify(fileRepository, times(1)).getFileExtension(mockFile);
			verify(mockFile, only()).getSize();
			verify(fileRepository, times(1)).createDirectory(any(Path.class));
			verify(fileRepository, times(1)).saveFile(eq(mockFile), any(Path.class));
			verifyNoMoreInteractions(fileRepository);
			verify(shortFilmRepository, only()).save(any(ShortFilm.class));
		});
	}

	@Test
	void uploadInvalidExtensionTest() {
		final String extension = ".txt";
		final long size = 1000L;
		final MultipartFile mockFile = mock(MultipartFile.class);
		
		when(mockFile.getSize()).thenReturn(size);
		when(fileRepository.getFileExtension(mockFile)).thenReturn(extension);
		when(fileRepository.saveFile(eq(mockFile), any(Path.class))).thenReturn(true);

		ShortFilmUploadData uploadData = new ShortFilmUploadData();
		uploadData.setTitle("Title");
		uploadData.setDescription("Description");
		uploadData.setFile(mockFile);

		assertThrows(InvalidExtensionException.class, () -> {
			shortFilmService.upload(uploadData, filmmakerService.getFilmmmakerByName("filmmaker1").get());
		});
	}
	
	@Test
	void uploadTooBigTest() {
		final String extension = ".mp4";
		final long size = 1000L * 1000L * 1000L + 1L;
		final MultipartFile mockFile = mock(MultipartFile.class);
		
		when(mockFile.getSize()).thenReturn(size);
		when(fileRepository.getFileExtension(mockFile)).thenReturn(extension);
		when(fileRepository.saveFile(eq(mockFile), any(Path.class))).thenReturn(true);

		ShortFilmUploadData uploadData = new ShortFilmUploadData();
		uploadData.setTitle("Title");
		uploadData.setDescription("Description");
		uploadData.setFile(mockFile);

		assertThrows(TooBigException.class, () -> {
			shortFilmService.upload(uploadData, filmmakerService.getFilmmmakerByName("filmmaker1").get());
		});
	}
	
	@Test
	void uploadRuntimeExceptionTest() {
		final String extension = ".mp4";
		final long size = 1000L;
		final MultipartFile mockFile = mock(MultipartFile.class);
		
		when(mockFile.getSize()).thenReturn(size);
		when(fileRepository.getFileExtension(mockFile)).thenReturn(extension);
		when(fileRepository.saveFile(eq(mockFile), any(Path.class))).thenReturn(false);
		
		ShortFilmUploadData uploadData = new ShortFilmUploadData();
		uploadData.setTitle("Title");
		uploadData.setDescription("Description");
		uploadData.setFile(mockFile);

		assertThrows(RuntimeException.class, () -> {
			shortFilmService.upload(uploadData, filmmakerService.getFilmmmakerByName("filmmaker1").get());
		});
	}
}
