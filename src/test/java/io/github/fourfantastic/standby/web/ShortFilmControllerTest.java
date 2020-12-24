package io.github.fourfantastic.standby.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.form.ShortFilmUploadData;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.InvalidExtensionException;
import io.github.fourfantastics.standby.service.exception.TooBigException;
import io.github.fourfantastics.standby.utils.Utils;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
@AutoConfigureMockMvc
public class ShortFilmControllerTest {
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	UserService userService;

	@MockBean
	ShortFilmService shortFilmService;
	
	@Test
	public void uploadViewTest() {
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(new Filmmaker()));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/upload"))
			.andExpect(status().isOk())
			.andExpect(model().attribute("shortFilmUploadData", new ShortFilmUploadData()))
			.andExpect(view().name("uploadShortFilm"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(shortFilmService);
	}
	
	@Test
	public void uploadViewNotLoggedTest() {
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/upload"))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("/login"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(shortFilmService);
	}
	
	@Test
	public void uploadViewLoggedAsCompanyTest() {
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(new Company()));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/upload"))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("/"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(shortFilmService);
	}
	
	@Test
	public void uploadShortFilmTest() throws InvalidExtensionException, TooBigException, RuntimeException {
		final Filmmaker mockFilmmaker = new Filmmaker();
		final ShortFilmUploadData mockShortFilmUploadData = new ShortFilmUploadData();
		mockShortFilmUploadData.setTitle("Title example");
		mockShortFilmUploadData.setDescription("Description example");
		mockShortFilmUploadData.setFile(new MockMultipartFile("file", "mockFile.mp4", "video/mp4", "This is an example".getBytes()));
		final ShortFilm mockShortFilm = mockShortFilmUploadData.toShortFilm();
		mockShortFilm.setId(123456L);
		mockShortFilm.setFileUrl(UUID.randomUUID().toString());
		mockShortFilm.setUploadDate(Instant.now().getEpochSecond());
		mockShortFilm.setUploader(mockFilmmaker);
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.upload(mockShortFilmUploadData, mockFilmmaker)).thenReturn(mockShortFilm);
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(multipart("/upload")
					.file((MockMultipartFile) mockShortFilmUploadData.getFile())
					.with(csrf())
					.param("title", mockShortFilmUploadData.getTitle())
					.param("description", mockShortFilmUploadData.getDescription()))
			.andExpect(status().isOk())
			.andExpect(content().json(
				String.format(
					"{'url': '/shortfilm/%d/edit', 'status': 302}",
					mockShortFilm.getId()
				)
			));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verify(shortFilmService, only()).upload(mockShortFilmUploadData, mockFilmmaker);
	}
	
	@Test
	public void uploadShortFilmNotLoggedTest()  {
		final ShortFilmUploadData mockShortFilmUploadData = new ShortFilmUploadData();
		mockShortFilmUploadData.setTitle("Title example");
		mockShortFilmUploadData.setDescription("Description example");
		mockShortFilmUploadData.setFile(new MockMultipartFile("file", "mockFile.mp4", "video/mp4", "This is an example".getBytes()));
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(multipart("/upload")
					.file((MockMultipartFile) mockShortFilmUploadData.getFile())
					.with(csrf())
					.param("title", mockShortFilmUploadData.getTitle())
					.param("description", mockShortFilmUploadData.getDescription()))
			.andExpect(status().isOk())
			.andExpect(content().json("{'url': '/login', 'status': 302}"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(shortFilmService);
	}
	
	@Test
	public void uploadShortFilmLoggedAsCompanyTest()  {
		final ShortFilmUploadData mockShortFilmUploadData = new ShortFilmUploadData();
		mockShortFilmUploadData.setTitle("Title example");
		mockShortFilmUploadData.setDescription("Description example");
		mockShortFilmUploadData.setFile(new MockMultipartFile("file", "mockFile.mp4", "video/mp4", "This is an example".getBytes()));
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(new Company()));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(multipart("/upload")
					.file((MockMultipartFile) mockShortFilmUploadData.getFile())
					.with(csrf())
					.param("title", mockShortFilmUploadData.getTitle())
					.param("description", mockShortFilmUploadData.getDescription()))
			.andExpect(status().isOk())
			.andExpect(content().json("{'url': '/', 'status': 302}"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(shortFilmService);
	}
	
	@Test
	public void uploadShortFilmMissingDataTest()  {
		final ShortFilmUploadData mockShortFilmUploadData = new ShortFilmUploadData();
		mockShortFilmUploadData.setTitle("");
		mockShortFilmUploadData.setDescription("Description example");
		mockShortFilmUploadData.setFile(new MockMultipartFile("file", "mockFile.mp4", "video/mp4", "This is an example".getBytes()));

		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(new Filmmaker()));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(multipart("/upload")
					.file((MockMultipartFile) mockShortFilmUploadData.getFile())
					.with(csrf())
					.param("title", mockShortFilmUploadData.getTitle())
					.param("description", mockShortFilmUploadData.getDescription()))
			.andExpect(status().isOk())
			.andExpect(content().json("{'message': '', 'status': 400, 'fieldErrors': {}}"));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(shortFilmService);
	}
	
	@Test
	public void uploadShortFilmInvalidExtensionTest() throws InvalidExtensionException, TooBigException, RuntimeException  {
		final ShortFilmUploadData mockShortFilmUploadData = new ShortFilmUploadData();
		mockShortFilmUploadData.setTitle("Title example");
		mockShortFilmUploadData.setDescription("Description example");
		mockShortFilmUploadData.setFile(new MockMultipartFile("file", "mockFile.txt", "text/plain", "This is an example".getBytes()));
		final Filmmaker mockFilmmaker = new Filmmaker();
		final String exceptionMessage = "This file has an invalid extension";
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.upload(mockShortFilmUploadData, mockFilmmaker))
			.thenThrow(new InvalidExtensionException(exceptionMessage, Utils.hashSet("file")));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(multipart("/upload")
					.file((MockMultipartFile) mockShortFilmUploadData.getFile())
					.with(csrf())
					.param("title", mockShortFilmUploadData.getTitle())
					.param("description", mockShortFilmUploadData.getDescription()))
			.andExpect(status().isOk())
			.andExpect(content().json(String.format("{'message': '%s', 'status': 400, 'fieldErrors': {}}", exceptionMessage)));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verify(shortFilmService, only()).upload(mockShortFilmUploadData, mockFilmmaker);
	}
	
	@Test
	public void uploadShortFilmTooBigTest() throws InvalidExtensionException, TooBigException, RuntimeException  {
		final ShortFilmUploadData mockShortFilmUploadData = new ShortFilmUploadData();
		mockShortFilmUploadData.setTitle("Title example");
		mockShortFilmUploadData.setDescription("Description example");
		mockShortFilmUploadData.setFile(new MockMultipartFile("file", "mockFile.txt", "text/plain", "This is an example".getBytes()));
		final Filmmaker mockFilmmaker = new Filmmaker();
		final String exceptionMessage = "This file is too big";
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.upload(mockShortFilmUploadData, mockFilmmaker))
			.thenThrow(new TooBigException(exceptionMessage, Utils.hashSet("file")));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(multipart("/upload")
					.file((MockMultipartFile) mockShortFilmUploadData.getFile())
					.with(csrf())
					.param("title", mockShortFilmUploadData.getTitle())
					.param("description", mockShortFilmUploadData.getDescription()))
			.andExpect(status().isOk())
			.andExpect(content().json(String.format("{'message': '%s', 'status': 400, 'fieldErrors': {}}", exceptionMessage)));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verify(shortFilmService, only()).upload(mockShortFilmUploadData, mockFilmmaker);
	}
	
	@Test
	public void uploadShortFilmRuntimeExceptionTest() throws InvalidExtensionException, TooBigException, RuntimeException  {
		final ShortFilmUploadData mockShortFilmUploadData = new ShortFilmUploadData();
		mockShortFilmUploadData.setTitle("Title example");
		mockShortFilmUploadData.setDescription("Description example");
		mockShortFilmUploadData.setFile(new MockMultipartFile("file", "mockFile.txt", "text/plain", "This is an example".getBytes()));
		final Filmmaker mockFilmmaker = new Filmmaker();
		final String exceptionMessage = "An unexpected exception ocurred!";
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.upload(mockShortFilmUploadData, mockFilmmaker))
			.thenThrow(new RuntimeException(exceptionMessage));
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(multipart("/upload")
					.file((MockMultipartFile) mockShortFilmUploadData.getFile())
					.with(csrf())
					.param("title", mockShortFilmUploadData.getTitle())
					.param("description", mockShortFilmUploadData.getDescription()))
			.andExpect(status().isOk())
			.andExpect(content().json(String.format("{'message': '%s', 'status': 500, 'fieldErrors': {}}", exceptionMessage)));
		});
		
		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verify(shortFilmService, only()).upload(mockShortFilmUploadData, mockFilmmaker);
	}
}
