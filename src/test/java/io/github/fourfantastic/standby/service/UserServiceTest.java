package io.github.fourfantastic.standby.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.repository.FileRepository;
import io.github.fourfantastics.standby.repository.UserRepository;
import io.github.fourfantastics.standby.service.NotificationService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.InvalidExtensionException;
import io.github.fourfantastics.standby.service.exception.NotUniqueException;
import io.github.fourfantastics.standby.service.exception.TooBigException;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class UserServiceTest {
	UserService userService;

	@Mock
	UserRepository userRepository;

	@Mock
	NotificationService notificationService;

	@Mock
	ShortFilmService shortFilmService;

	@Mock
	FileRepository fileRepository;

	@BeforeEach
	public void setup() {
		userService = new UserService(userRepository, fileRepository);

		when(userRepository.save(any(User.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
	}

	@Test
	void registerUserTest() {
		final String name = "Táctico";
		final String rawPassword = "weak password";
		final User mockUser = new User();
		mockUser.setName(name);
		mockUser.setEmail("Davinci@gmail.com");
		mockUser.setPassword(rawPassword);
		mockUser.setType(UserType.Filmmaker);

		when(userRepository.findByName(name)).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			User registeredUser = userService.register(mockUser);
			assertTrue(userService.getEncoder().matches(rawPassword, registeredUser.getPassword()));
			assertNotNull(registeredUser.getCreationDate());

			verify(userRepository, times(1)).findByName(name);
			verify(userRepository, times(1)).save(registeredUser);
			verifyNoMoreInteractions(userRepository);
		});
	}

	@Test
	void registerUserDuplicatedTest() {
		final String name = "Táctico";
		final String rawPassword = "weak password";
		final User mockUser = new User();
		mockUser.setName(name);
		mockUser.setEmail("Davinci@gmail.com");
		mockUser.setPassword(rawPassword);
		mockUser.setType(UserType.Filmmaker);

		when(userRepository.findByName(name)).thenReturn(Optional.of(new User()));

		assertThrows(NotUniqueException.class, () -> {
			userService.register(mockUser);
			
			verify(userRepository, only()).findByName(name);
		});
	}
	
	@Test
	void setProfilePictureTest() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setName("filmmaker1");
		mockFilmmaker.setPhotoUrl(null);
		
		final String extension = ".png";
		final long size = 3000L;
		final MultipartFile mockFile = mock(MultipartFile.class);
		
		when(mockFile.getSize()).thenReturn(size);
		when(fileRepository.getFileExtension(mockFile)).thenReturn(extension);
		when(fileRepository.saveFile(eq(mockFile), any(Path.class))).thenReturn(true);
	
		assertDoesNotThrow(() -> {
			userService.setProfilePicture(mockFilmmaker, mockFile);
		});
		
		assertNotNull(mockFilmmaker.getPhotoUrl());
		
		verify(fileRepository, times(1)).getFileExtension(mockFile);
		verify(fileRepository, times(1)).createDirectory(any(Path.class));
		verify(fileRepository, times(1)).saveFile(eq(mockFile), any(Path.class));
		verifyNoMoreInteractions(fileRepository);
		verify(userRepository, only()).save(mockFilmmaker);
	}
	
	@Test
	void setProfilePictureTooBigTest() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setName("filmmaker1");
		mockFilmmaker.setPhotoUrl(null);
		
		final String extension = ".png";
		final long size = 1000 * 1001 * 5L;
		final MultipartFile mockFile = mock(MultipartFile.class);
		
		when(mockFile.getSize()).thenReturn(size);
		when(fileRepository.getFileExtension(mockFile)).thenReturn(extension);
		when(fileRepository.saveFile(eq(mockFile), any(Path.class))).thenReturn(true);
	
		assertThrows(TooBigException.class, () -> {
			userService.setProfilePicture(mockFilmmaker, mockFile);
		});
		
		assertNull(mockFilmmaker.getPhotoUrl());
		
		verify(fileRepository, times(1)).getFileExtension(mockFile);
	}
	
	@Test
	void setProfilePictureInvalidExtensionTest() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setName("filmmaker1");
		mockFilmmaker.setPhotoUrl(null);
		
		final String extension = ".mp3";
		final long size = 1000 * 1000 * 5L;
		final MultipartFile mockFile = mock(MultipartFile.class);
		
		when(mockFile.getSize()).thenReturn(size);
		when(fileRepository.getFileExtension(mockFile)).thenReturn(extension);
		when(fileRepository.saveFile(eq(mockFile), any(Path.class))).thenReturn(true);
	
		assertThrows(InvalidExtensionException.class, () -> {
			userService.setProfilePicture(mockFilmmaker, mockFile);
		});
		
		assertNull(mockFilmmaker.getPhotoUrl());
		
		verify(fileRepository, times(1)).getFileExtension(mockFile);
	}
	
	@Test
	void setProfilePictureRuntimeExceptionTest() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setName("filmmaker1");
		mockFilmmaker.setPhotoUrl(null);
		
		final String extension = ".png";
		final long size = 1000 * 1000 * 5L;
		final MultipartFile mockFile = mock(MultipartFile.class);
		
		when(mockFile.getSize()).thenReturn(size);
		when(fileRepository.getFileExtension(mockFile)).thenReturn(extension);
		when(fileRepository.saveFile(eq(mockFile), any(Path.class))).thenReturn(false);
	
		assertThrows(RuntimeException.class, () -> {
			userService.setProfilePicture(mockFilmmaker, mockFile);
		});
		
		assertNull(mockFilmmaker.getPhotoUrl());
		
		verify(fileRepository, times(1)).getFileExtension(mockFile);
	}
}
