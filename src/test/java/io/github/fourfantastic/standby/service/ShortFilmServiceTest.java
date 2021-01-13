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

import java.io.IOException;
import java.nio.file.Path;

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
	
	Filmmaker mockUploader;
	
	@BeforeEach
	public void setup() throws InvalidExtensionException, RuntimeException {
		shortFilmService = new ShortFilmService(shortFilmRepository, fileRepository);

		mockUploader = new Filmmaker();
		mockUploader.setId(1L);
		mockUploader.setName("filmmaker1");
		mockUploader.setPassword("password");
		mockUploader.setEmail("filmmaker@gmail.com");
		mockUploader.setPhotoUrl("url photo");
		mockUploader.setCity("Seville");
		mockUploader.setCountry("Spain");
		mockUploader.setFullname("Filmmaker Díaz García");
		mockUploader.setPhone("675987432");
		
		when(shortFilmRepository.save(any(ShortFilm.class))).then(AdditionalAnswers.returnsFirstArg());
	}

	@Test
	void uploadTest() throws Exception, IOException {
		final String extension = ".mp4";
		final long size = 1000L;
		final MultipartFile mockVideo = mock(MultipartFile.class);
		
		when(mockVideo.getSize()).thenReturn(size);
		when(fileRepository.getFileExtension(mockVideo)).thenReturn(extension);
		when(fileRepository.saveFile(eq(mockVideo), any(Path.class))).thenReturn(true);

		ShortFilmUploadData uploadData = new ShortFilmUploadData();
		uploadData.setTitle("Title");
		uploadData.setDescription("Description");
		uploadData.setFile(mockVideo);

		assertDoesNotThrow(() -> {
			ShortFilm shortFilm = shortFilmService.upload(uploadData, mockUploader);

			assertThat(shortFilm.getTitle()).isEqualTo(uploadData.getTitle());
			assertThat(shortFilm.getDescription()).isEqualTo(uploadData.getDescription());
			assertThat(shortFilm.getVideoUrl()).isNotEmpty();
			assertThat(shortFilm.getThumbnailUrl()).isNull();
			assertNotNull(shortFilm.getUploadDate());

			verify(fileRepository, times(1)).getFileExtension(mockVideo);
			verify(fileRepository, times(1)).createDirectory(any(Path.class));
			verify(fileRepository, times(1)).saveFile(eq(mockVideo), any(Path.class));
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
			shortFilmService.upload(uploadData, mockUploader);
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
			shortFilmService.upload(uploadData, mockUploader);
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
			shortFilmService.upload(uploadData, mockUploader);
		});
	}
	
	@Test
	void uploadThumbnailTest() throws Exception, IOException {
		final ShortFilm mockShortFilm = new ShortFilm();
		final String extension = ".png";
		final long size = 50000L;
		final MultipartFile mockImage = mock(MultipartFile.class);
		
		when(mockImage.getSize()).thenReturn(size);
		when(fileRepository.getFileExtension(mockImage)).thenReturn(extension);
		when(fileRepository.saveFile(eq(mockImage), any(Path.class))).thenReturn(true);

		assertDoesNotThrow(() -> {
			shortFilmService.uploadThumbnail(mockShortFilm, mockImage);

			assertThat(mockShortFilm.getThumbnailUrl()).isNotBlank();
			
			verify(fileRepository, times(1)).getFileExtension(mockImage);
			verify(fileRepository, times(1)).createDirectory(any(Path.class));
			verify(fileRepository, times(1)).saveFile(eq(mockImage), any(Path.class));
			verifyNoMoreInteractions(fileRepository);
		});
	}
	
	@Test
	void uploadThumbnailInvalidExtensionTest() throws Exception {
		final ShortFilm mockShortFilm = new ShortFilm();
		final String extension = ".txt";
		final long size = 50000L;
		final MultipartFile mockImage = mock(MultipartFile.class);
		
		when(mockImage.getSize()).thenReturn(size);
		when(fileRepository.getFileExtension(mockImage)).thenReturn(extension);
		when(fileRepository.saveFile(eq(mockImage), any(Path.class))).thenReturn(true);

		assertThrows(InvalidExtensionException.class, () -> {
			shortFilmService.uploadThumbnail(mockShortFilm, mockImage);
		});
	}
	
	@Test
	void uploadThumbnailTooBigTest() throws Exception {
		final ShortFilm mockShortFilm = new ShortFilm();
		final String extension = ".jpg";
		final long size = 7000000L;
		final MultipartFile mockImage = mock(MultipartFile.class);
		
		when(mockImage.getSize()).thenReturn(size);
		when(fileRepository.getFileExtension(mockImage)).thenReturn(extension);
		when(fileRepository.saveFile(eq(mockImage), any(Path.class))).thenReturn(true);
		
		assertThrows(TooBigException.class, () -> {
			shortFilmService.uploadThumbnail(mockShortFilm, mockImage);
		});
	}
	
	@Test
	void uploadThumbnailRuntimeExceptionTest() throws Exception {
		final ShortFilm mockShortFilm = new ShortFilm();
		final String extension = ".jpg";
		final long size = 7000L;
		final MultipartFile mockImage = mock(MultipartFile.class);
		
		when(mockImage.getSize()).thenReturn(size);
		when(fileRepository.getFileExtension(mockImage)).thenReturn(extension);
		when(fileRepository.saveFile(eq(mockImage), any(Path.class))).thenReturn(false);

		assertThrows(RuntimeException.class, () -> {
			shortFilmService.uploadThumbnail(mockShortFilm, mockImage);
		});
	}
}
