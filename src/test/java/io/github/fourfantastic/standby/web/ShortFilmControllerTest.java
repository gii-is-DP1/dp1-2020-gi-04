package io.github.fourfantastic.standby.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.Rating;
import io.github.fourfantastics.standby.model.RoleType;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.form.Pagination;
import io.github.fourfantastics.standby.model.form.RoleData;
import io.github.fourfantastics.standby.model.form.ShortFilmEditData;
import io.github.fourfantastics.standby.model.form.ShortFilmUploadData;
import io.github.fourfantastics.standby.model.form.ShortFilmViewData;
import io.github.fourfantastics.standby.service.CommentService;
import io.github.fourfantastics.standby.service.FavouriteService;
import io.github.fourfantastics.standby.service.RatingService;
import io.github.fourfantastics.standby.service.RoleService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.SubscriptionService;
import io.github.fourfantastics.standby.service.TagService;
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

	@MockBean
	TagService tagService;

	@MockBean
	RoleService roleService;

	@MockBean
	CommentService commentService;

	@MockBean
	RatingService ratingService;

	@MockBean
	SubscriptionService subscriptionService;
	
	@MockBean
	FavouriteService favouriteService;

	@Test
	public void uploadViewTest() {
		when(userService.getLoggedUser()).thenReturn(Optional.of(new Filmmaker()));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/upload")).andExpect(status().isOk())
					.andExpect(model().attribute("shortFilmUploadData", new ShortFilmUploadData()))
					.andExpect(view().name("uploadShortFilm"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(shortFilmService);
	}

	@Test
	public void uploadViewNotLoggedTest() {
		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/upload")).andExpect(status().isFound()).andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(shortFilmService);
	}

	@Test
	public void uploadViewLoggedAsCompanyTest() {
		when(userService.getLoggedUser()).thenReturn(Optional.of(new Company()));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/upload")).andExpect(status().isFound()).andExpect(redirectedUrl("/"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(shortFilmService);
	}

	@Test
	public void uploadShortFilmTest() throws InvalidExtensionException, TooBigException, RuntimeException {
		final Filmmaker mockFilmmaker = new Filmmaker();
		final ShortFilmUploadData mockShortFilmUploadData = new ShortFilmUploadData();
		mockShortFilmUploadData.setTitle("Title example");
		mockShortFilmUploadData.setDescription("Description example");
		mockShortFilmUploadData
				.setFile(new MockMultipartFile("file", "mockFile.mp4", "video/mp4", "This is an example".getBytes()));
		final ShortFilm mockShortFilm = mockShortFilmUploadData.toShortFilm();
		mockShortFilm.setId(123456L);
		mockShortFilm.setVideoUrl(UUID.randomUUID().toString());
		mockShortFilm.setUploadDate(new Date().getTime());
		mockShortFilm.setUploader(mockFilmmaker);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.upload(mockShortFilmUploadData, mockFilmmaker)).thenReturn(mockShortFilm);

		assertDoesNotThrow(() -> {
			mockMvc.perform(multipart("/upload").file((MockMultipartFile) mockShortFilmUploadData.getFile())
					.with(csrf()).param("title", mockShortFilmUploadData.getTitle())
					.param("description", mockShortFilmUploadData.getDescription())).andExpect(status().isOk())
					.andExpect(content().json(
							String.format("{'url': '/shortfilm/%d/edit', 'status': 302}", mockShortFilm.getId())));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, only()).upload(mockShortFilmUploadData, mockFilmmaker);
	}

	@Test
	public void uploadShortFilmNotLoggedTest() {
		final ShortFilmUploadData mockShortFilmUploadData = new ShortFilmUploadData();
		mockShortFilmUploadData.setTitle("Title example");
		mockShortFilmUploadData.setDescription("Description example");
		mockShortFilmUploadData
				.setFile(new MockMultipartFile("file", "mockFile.mp4", "video/mp4", "This is an example".getBytes()));

		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(multipart("/upload").file((MockMultipartFile) mockShortFilmUploadData.getFile())
					.with(csrf()).param("title", mockShortFilmUploadData.getTitle())
					.param("description", mockShortFilmUploadData.getDescription())).andExpect(status().isOk())
					.andExpect(content().json("{'url': '/login', 'status': 302}"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(shortFilmService);
	}

	@Test
	public void uploadShortFilmLoggedAsCompanyTest() {
		final ShortFilmUploadData mockShortFilmUploadData = new ShortFilmUploadData();
		mockShortFilmUploadData.setTitle("Title example");
		mockShortFilmUploadData.setDescription("Description example");
		mockShortFilmUploadData
				.setFile(new MockMultipartFile("file", "mockFile.mp4", "video/mp4", "This is an example".getBytes()));

		when(userService.getLoggedUser()).thenReturn(Optional.of(new Company()));

		assertDoesNotThrow(() -> {
			mockMvc.perform(multipart("/upload").file((MockMultipartFile) mockShortFilmUploadData.getFile())
					.with(csrf()).param("title", mockShortFilmUploadData.getTitle())
					.param("description", mockShortFilmUploadData.getDescription())).andExpect(status().isOk())
					.andExpect(content().json("{'url': '/', 'status': 302}"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(shortFilmService);
	}

	@Test
	public void uploadShortFilmMissingDataTest() {
		final ShortFilmUploadData mockShortFilmUploadData = new ShortFilmUploadData();
		mockShortFilmUploadData.setTitle("");
		mockShortFilmUploadData.setDescription("Description example");
		mockShortFilmUploadData
				.setFile(new MockMultipartFile("file", "mockFile.mp4", "video/mp4", "This is an example".getBytes()));

		when(userService.getLoggedUser()).thenReturn(Optional.of(new Filmmaker()));

		assertDoesNotThrow(() -> {
			mockMvc.perform(multipart("/upload").file((MockMultipartFile) mockShortFilmUploadData.getFile())
					.with(csrf()).param("title", mockShortFilmUploadData.getTitle())
					.param("description", mockShortFilmUploadData.getDescription())).andExpect(status().isOk())
					.andExpect(content().json("{'message': '', 'status': 400, 'fieldErrors': {}}"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(shortFilmService);
	}

	@Test
	public void uploadShortFilmInvalidExtensionTest()
			throws InvalidExtensionException, TooBigException, RuntimeException {
		final ShortFilmUploadData mockShortFilmUploadData = new ShortFilmUploadData();
		mockShortFilmUploadData.setTitle("Title example");
		mockShortFilmUploadData.setDescription("Description example");
		mockShortFilmUploadData
				.setFile(new MockMultipartFile("file", "mockFile.txt", "text/plain", "This is an example".getBytes()));
		final Filmmaker mockFilmmaker = new Filmmaker();
		final String exceptionMessage = "This file has an invalid extension";

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.upload(mockShortFilmUploadData, mockFilmmaker))
				.thenThrow(new InvalidExtensionException(exceptionMessage, Utils.hashSet("file")));

		assertDoesNotThrow(() -> {
			mockMvc.perform(multipart("/upload").file((MockMultipartFile) mockShortFilmUploadData.getFile())
					.with(csrf()).param("title", mockShortFilmUploadData.getTitle())
					.param("description", mockShortFilmUploadData.getDescription())).andExpect(status().isOk())
					.andExpect(content().json(String.format(
							"{'message': '', 'status': 400, 'fieldErrors': {'file': '%s'}}", exceptionMessage)));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, only()).upload(mockShortFilmUploadData, mockFilmmaker);
	}

	@Test
	public void uploadShortFilmTooBigTest() throws InvalidExtensionException, TooBigException, RuntimeException {
		final ShortFilmUploadData mockShortFilmUploadData = new ShortFilmUploadData();
		mockShortFilmUploadData.setTitle("Title example");
		mockShortFilmUploadData.setDescription("Description example");
		mockShortFilmUploadData
				.setFile(new MockMultipartFile("file", "mockFile.txt", "text/plain", "This is an example".getBytes()));
		final Filmmaker mockFilmmaker = new Filmmaker();
		final String exceptionMessage = "This file is too big";

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.upload(mockShortFilmUploadData, mockFilmmaker))
				.thenThrow(new TooBigException(exceptionMessage, Utils.hashSet("file")));

		assertDoesNotThrow(() -> {
			mockMvc.perform(multipart("/upload").file((MockMultipartFile) mockShortFilmUploadData.getFile())
					.with(csrf()).param("title", mockShortFilmUploadData.getTitle())
					.param("description", mockShortFilmUploadData.getDescription())).andExpect(status().isOk())
					.andExpect(content().json(String.format(
							"{'message': '', 'status': 400, 'fieldErrors': {'file': '%s'}}", exceptionMessage)));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, only()).upload(mockShortFilmUploadData, mockFilmmaker);
	}

	@Test
	public void uploadShortFilmRuntimeExceptionTest()
			throws InvalidExtensionException, TooBigException, RuntimeException {
		final ShortFilmUploadData mockShortFilmUploadData = new ShortFilmUploadData();
		mockShortFilmUploadData.setTitle("Title example");
		mockShortFilmUploadData.setDescription("Description example");
		mockShortFilmUploadData
				.setFile(new MockMultipartFile("file", "mockFile.txt", "text/plain", "This is an example".getBytes()));
		final Filmmaker mockFilmmaker = new Filmmaker();
		final String exceptionMessage = "An unexpected exception ocurred!";

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.upload(mockShortFilmUploadData, mockFilmmaker))
				.thenThrow(new RuntimeException(exceptionMessage));

		assertDoesNotThrow(() -> {
			mockMvc.perform(multipart("/upload").file((MockMultipartFile) mockShortFilmUploadData.getFile())
					.with(csrf()).param("title", mockShortFilmUploadData.getTitle())
					.param("description", mockShortFilmUploadData.getDescription())).andExpect(status().isOk())
					.andExpect(content().json(
							String.format("{'message': '%s', 'status': 500, 'fieldErrors': {}}", exceptionMessage)));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, only()).upload(mockShortFilmUploadData, mockFilmmaker);
	}

	@Test
	public void editViewTest() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.setId(999L);
		mockShortFilm.setUploader(mockFilmmaker);
		mockShortFilm.setTitle("Example title");
		mockShortFilm.setDescription("Example description");
		mockShortFilm.setUploadDate(0L);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.getShortFilmById(mockShortFilm.getId())).thenReturn(Optional.of(mockShortFilm));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get(String.format("/shortfilm/%d/edit", mockShortFilm.getId()))).andExpect(status().isOk())
					.andExpect(model().attribute("shortFilmEditData", ShortFilmEditData.fromShortFilm(mockShortFilm)))
					.andExpect(view().name("editShortFilm"));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, only()).getShortFilmById(mockShortFilm.getId());
	}

	@Test
	public void editViewTestNotLogged() {
		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/shortfilm/999/edit")).andExpect(status().isFound())
					.andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(shortFilmService);
	}

	@Test
	public void editViewTestShortFilmNotFound() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		final long shortFilmId = 999;

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.getShortFilmById(shortFilmId)).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(get(String.format("/shortfilm/%d/edit", shortFilmId))).andExpect(status().isFound())
					.andExpect(redirectedUrl("/"));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, only()).getShortFilmById(shortFilmId);
	}

	@Test
	public void editViewTestUploaderMismatch() {
		final Filmmaker loggedMockFilmmaker = new Filmmaker();
		loggedMockFilmmaker.setId(1L);
		final Filmmaker uploaderMockFilmmaker = new Filmmaker();
		uploaderMockFilmmaker.setId(2L);
		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.setId(999L);
		mockShortFilm.setUploader(uploaderMockFilmmaker);

		when(userService.getLoggedUser()).thenReturn(Optional.of(loggedMockFilmmaker));
		when(shortFilmService.getShortFilmById(mockShortFilm.getId())).thenReturn(Optional.of(mockShortFilm));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get(String.format("/shortfilm/%d/edit", mockShortFilm.getId())))
					.andExpect(status().isFound()).andExpect(redirectedUrl("/"));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, only()).getShortFilmById(mockShortFilm.getId());
	}

	@Test
	public void editShortFilmAddTagTest() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.setId(999L);
		mockShortFilm.setUploader(mockFilmmaker);
		mockShortFilm.setTitle("Example title");
		mockShortFilm.setDescription("Example description");
		final String newTagName = "action";
		final ShortFilmEditData mockShortFilmEditData = ShortFilmEditData.fromShortFilm(mockShortFilm);
		mockShortFilmEditData.setNewTagName(newTagName);

		final ShortFilmEditData returnedShortFilmEditData = ShortFilmEditData.fromShortFilm(mockShortFilm);
		returnedShortFilmEditData.getTags().add(newTagName);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.getShortFilmById(mockShortFilm.getId())).thenReturn(Optional.of(mockShortFilm));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/shortfilm/%d/edit", mockShortFilm.getId())).with(csrf())
					.param("title", mockShortFilmEditData.getTitle())
					.param("description", mockShortFilmEditData.getDescription())
					.param("newTagName", mockShortFilmEditData.getNewTagName())
					.param("newRoleFilmmaker", mockShortFilmEditData.getNewRoleFilmmaker())
					.param("newRoleType", mockShortFilmEditData.getNewRoleType().toString()).param("addTag", ""))
					.andExpect(status().isOk())
					.andExpect(model().attribute("shortFilmEditData", returnedShortFilmEditData))
					.andExpect(view().name("editShortFilm"));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, only()).getShortFilmById(mockShortFilm.getId());
	}

	@Test
	public void editShortFilmAddTagNotLoggedTest() {
		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/shortfilm/999/edit").with(csrf())
					.param("addTag", ""))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(shortFilmService);
	}
	
	@Test
	public void editShortFilmAddTagInvalidShortFilmTest() {
		Long shortFilmId = 999L;
		
		when(userService.getLoggedUser()).thenReturn(Optional.of(new Company()));
		when(shortFilmService.getShortFilmById(shortFilmId)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/shortfilm/%d/edit", shortFilmId)).with(csrf())
					.param("addTag", ""))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/"));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, only()).getShortFilmById(shortFilmId);
	}
	
	@Test
	public void editShortFilmAddInvalidTagTest() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.setId(999L);
		mockShortFilm.setUploader(mockFilmmaker);
		mockShortFilm.setTitle("Example title");
		mockShortFilm.setDescription("Example description");
		final String newTagName = "";
		final ShortFilmEditData mockShortFilmEditData = ShortFilmEditData.fromShortFilm(mockShortFilm);
		mockShortFilmEditData.setNewTagName(newTagName);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.getShortFilmById(mockShortFilm.getId())).thenReturn(Optional.of(mockShortFilm));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/shortfilm/%d/edit", mockShortFilm.getId())).with(csrf())
					.param("title", mockShortFilmEditData.getTitle())
					.param("description", mockShortFilmEditData.getDescription())
					.param("newTagName", mockShortFilmEditData.getNewTagName())
					.param("newRoleFilmmaker", mockShortFilmEditData.getNewRoleFilmmaker())
					.param("newRoleType", mockShortFilmEditData.getNewRoleType().toString()).param("addTag", ""))
					.andExpect(status().isOk()).andExpect(model().attribute("shortFilmEditData", mockShortFilmEditData))
					.andExpect(model().attributeHasFieldErrors("shortFilmEditData", "newTagName"))
					.andExpect(view().name("editShortFilm"));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, only()).getShortFilmById(mockShortFilm.getId());
	}

	@Test
	public void editShortFilmAddRepeatedTagTest() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.setId(999L);
		mockShortFilm.setUploader(mockFilmmaker);
		mockShortFilm.setTitle("Example title");
		mockShortFilm.setDescription("Example description");
		final String newTagName = "wakala";
		final ShortFilmEditData mockShortFilmEditData = ShortFilmEditData.fromShortFilm(mockShortFilm);
		mockShortFilmEditData.getTags().add(newTagName);
		mockShortFilmEditData.setNewTagName(newTagName);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.getShortFilmById(mockShortFilm.getId())).thenReturn(Optional.of(mockShortFilm));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/shortfilm/%d/edit", mockShortFilm.getId())).with(csrf())
					.param("title", mockShortFilmEditData.getTitle())
					.param("tags[0]", mockShortFilmEditData.getTags().get(0))
					.param("description", mockShortFilmEditData.getDescription())
					.param("newTagName", mockShortFilmEditData.getNewTagName())
					.param("newRoleFilmmaker", mockShortFilmEditData.getNewRoleFilmmaker())
					.param("newRoleType", mockShortFilmEditData.getNewRoleType().toString()).param("addTag", ""))
					.andExpect(status().isOk()).andExpect(model().attribute("shortFilmEditData", mockShortFilmEditData))
					.andExpect(model().attributeHasFieldErrors("shortFilmEditData", "newTagName"))
					.andExpect(view().name("editShortFilm"));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, only()).getShortFilmById(mockShortFilm.getId());
	}
	
	@Test
	public void editShortFilmRemoveTagTest() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.setId(999L);
		mockShortFilm.setUploader(mockFilmmaker);
		mockShortFilm.setTitle("Example title");
		mockShortFilm.setDescription("Example description");
		final String tagName = "action";
		final ShortFilmEditData mockShortFilmEditData = ShortFilmEditData.fromShortFilm(mockShortFilm);
		mockShortFilmEditData.getTags().add(tagName);

		final ShortFilmEditData returnedShortFilmEditData = ShortFilmEditData.fromShortFilm(mockShortFilm);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.getShortFilmById(mockShortFilm.getId())).thenReturn(Optional.of(mockShortFilm));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/shortfilm/%d/edit", mockShortFilm.getId())).with(csrf())
					.param("title", mockShortFilmEditData.getTitle())
					.param("description", mockShortFilmEditData.getDescription())
					.param("newTagName", mockShortFilmEditData.getNewTagName())
					.param("newRoleFilmmaker", mockShortFilmEditData.getNewRoleFilmmaker())
					.param("newRoleType", mockShortFilmEditData.getNewRoleType().toString())
					.param("removeTag", tagName)).andExpect(status().isOk())
					.andExpect(model().attribute("shortFilmEditData", returnedShortFilmEditData))
					.andExpect(view().name("editShortFilm"));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, only()).getShortFilmById(mockShortFilm.getId());
	}
	
	@Test
	public void editShortFilmRemoveTagNotLoggedTest() {
		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/shortfilm/999/edit").with(csrf())
					.param("removeTag", ""))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(shortFilmService);
	}
	
	@Test
	public void editShortFilmRemoveTagInvalidShortFilmTest() {
		Long shortFilmId = 999L;
		
		when(userService.getLoggedUser()).thenReturn(Optional.of(new Company()));
		when(shortFilmService.getShortFilmById(shortFilmId)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/shortfilm/%d/edit", shortFilmId)).with(csrf())
					.param("removeTag", ""))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/"));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, only()).getShortFilmById(shortFilmId);
	}

	@Test
	public void editShortFilmAddRoleTest() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setName("filmmaker1");
		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.setId(999L);
		mockShortFilm.setUploader(mockFilmmaker);
		mockShortFilm.setTitle("Example title");
		mockShortFilm.setDescription("Example description");
		final ShortFilmEditData mockShortFilmEditData = ShortFilmEditData.fromShortFilm(mockShortFilm);
		mockShortFilmEditData.setNewRoleFilmmaker(mockFilmmaker.getName());
		mockShortFilmEditData.setNewRoleType(RoleType.ANIMATOR);

		final ShortFilmEditData returnedShortFilmEditData = ShortFilmEditData.fromShortFilm(mockShortFilm);
		returnedShortFilmEditData.getRoles()
				.add(RoleData.of(mockFilmmaker.getName(), mockShortFilmEditData.getNewRoleType()));
		returnedShortFilmEditData.setNewRoleFilmmaker(mockShortFilmEditData.getNewRoleFilmmaker());
		returnedShortFilmEditData.setNewRoleType(mockShortFilmEditData.getNewRoleType());
		returnedShortFilmEditData.getRolePagination().setTotalElements(returnedShortFilmEditData.getRoles().size());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.getShortFilmById(mockShortFilm.getId())).thenReturn(Optional.of(mockShortFilm));
		when(userService.getUserByName(mockFilmmaker.getName())).thenReturn(Optional.of(mockFilmmaker));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/shortfilm/%d/edit", mockShortFilm.getId())).with(csrf())
					.param("title", mockShortFilmEditData.getTitle())
					.param("description", mockShortFilmEditData.getDescription())
					.param("newTagName", mockShortFilmEditData.getNewTagName())
					.param("newRoleFilmmaker", mockShortFilmEditData.getNewRoleFilmmaker())
					.param("newRoleType", mockShortFilmEditData.getNewRoleType().toString()).param("addRole", ""))
					.andExpect(status().isOk())
					.andExpect(model().attribute("shortFilmEditData", returnedShortFilmEditData))
					.andExpect(view().name("editShortFilm"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(shortFilmService, only()).getShortFilmById(mockShortFilm.getId());
		verify(userService, times(1)).getUserByName(mockFilmmaker.getName());
		verifyNoMoreInteractions(userService);
	}
	
	@Test
	public void editShortFilmAddRoleNotLoggedTest() {
		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/shortfilm/999/edit").with(csrf())
					.param("addRole", ""))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(shortFilmService);
	}
	
	@Test
	public void editShortFilmAddRoleInvalidShortFilmTest() {
		Long shortFilmId = 999L;
		
		when(userService.getLoggedUser()).thenReturn(Optional.of(new Company()));
		when(shortFilmService.getShortFilmById(shortFilmId)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/shortfilm/%d/edit", shortFilmId)).with(csrf())
					.param("addRole", ""))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/"));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, only()).getShortFilmById(shortFilmId);
	}

	@Test
	public void editShortFilmAddRoleFilmmakerNotFoundTest() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.setId(999L);
		mockShortFilm.setUploader(mockFilmmaker);
		mockShortFilm.setTitle("Example title");
		mockShortFilm.setDescription("Example description");
		final ShortFilmEditData mockShortFilmEditData = ShortFilmEditData.fromShortFilm(mockShortFilm);
		mockShortFilmEditData.setNewRoleFilmmaker("filmmaker2");
		mockShortFilmEditData.setNewRoleType(RoleType.ANIMATOR);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.getShortFilmById(mockShortFilm.getId())).thenReturn(Optional.of(mockShortFilm));
		when(userService.getUserByName(mockShortFilmEditData.getNewRoleFilmmaker())).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/shortfilm/%d/edit", mockShortFilm.getId())).with(csrf())
					.param("title", mockShortFilmEditData.getTitle())
					.param("description", mockShortFilmEditData.getDescription())
					.param("newTagName", mockShortFilmEditData.getNewTagName())
					.param("newRoleFilmmaker", mockShortFilmEditData.getNewRoleFilmmaker())
					.param("newRoleType", mockShortFilmEditData.getNewRoleType().toString()).param("addRole", ""))
					.andExpect(status().isOk()).andExpect(model().attribute("shortFilmEditData", mockShortFilmEditData))
					.andExpect(model().attributeHasFieldErrors("shortFilmEditData", "newRoleFilmmaker"))
					.andExpect(view().name("editShortFilm"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(shortFilmService, only()).getShortFilmById(mockShortFilm.getId());
		verify(userService, times(1)).getUserByName(mockShortFilmEditData.getNewRoleFilmmaker());
		verifyNoMoreInteractions(userService);
	}
	
	@Test
	public void editShortFilmAddInvalidRoleTest() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.setId(999L);
		mockShortFilm.setUploader(mockFilmmaker);
		mockShortFilm.setTitle("Example title");
		mockShortFilm.setDescription("Example description");
		final ShortFilmEditData mockShortFilmEditData = ShortFilmEditData.fromShortFilm(mockShortFilm);
		mockShortFilmEditData.setNewRoleFilmmaker("");
		mockShortFilmEditData.setNewRoleType(RoleType.ANIMATOR);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.getShortFilmById(mockShortFilm.getId())).thenReturn(Optional.of(mockShortFilm));
		when(userService.getUserByName(mockShortFilmEditData.getNewRoleFilmmaker())).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/shortfilm/%d/edit", mockShortFilm.getId())).with(csrf())
					.param("title", mockShortFilmEditData.getTitle())
					.param("description", mockShortFilmEditData.getDescription())
					.param("newTagName", mockShortFilmEditData.getNewTagName())
					.param("newRoleFilmmaker", mockShortFilmEditData.getNewRoleFilmmaker())
					.param("newRoleType", mockShortFilmEditData.getNewRoleType().toString()).param("addRole", ""))
					.andExpect(status().isOk()).andExpect(model().attribute("shortFilmEditData", mockShortFilmEditData))
					.andExpect(model().attributeHasFieldErrors("shortFilmEditData", "newRoleFilmmaker"))
					.andExpect(view().name("editShortFilm"));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, only()).getShortFilmById(mockShortFilm.getId());
	}
	
	@Test
	public void editShortFilmAddRepeatedRoleTest() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.setId(999L);
		mockShortFilm.setUploader(mockFilmmaker);
		mockShortFilm.setTitle("Example title");
		mockShortFilm.setDescription("Example description");
		final ShortFilmEditData mockShortFilmEditData = ShortFilmEditData.fromShortFilm(mockShortFilm);
		mockShortFilmEditData.getRoles().add(RoleData.of("filmmaker", RoleType.ACTOR));
		mockShortFilmEditData.setNewRoleFilmmaker("filmmaker");
		mockShortFilmEditData.setNewRoleType(RoleType.ACTOR);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.getShortFilmById(mockShortFilm.getId())).thenReturn(Optional.of(mockShortFilm));
		when(userService.getUserByName(mockShortFilmEditData.getNewRoleFilmmaker())).thenReturn(Optional.of(mockFilmmaker));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/shortfilm/%d/edit", mockShortFilm.getId())).with(csrf())
					.param("title", mockShortFilmEditData.getTitle())
					.param("description", mockShortFilmEditData.getDescription())
					.param("newTagName", mockShortFilmEditData.getNewTagName())
					.param("newRoleFilmmaker", mockShortFilmEditData.getNewRoleFilmmaker())
					.param("newRoleType", mockShortFilmEditData.getNewRoleType().toString())
					.param("addRole", "")
					.param("roles[0].filmmakerName", mockShortFilmEditData.getRoles().get(0).getFilmmakerName())
					.param("roles[0].roleType", mockShortFilmEditData.getRoles().get(0).getRoleType().toString()))
					.andExpect(status().isOk()).andExpect(model().attribute("shortFilmEditData", mockShortFilmEditData))
					.andExpect(model().attributeHasFieldErrors("shortFilmEditData", "newRoleFilmmaker"))
					.andExpect(view().name("editShortFilm"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(shortFilmService, only()).getShortFilmById(mockShortFilm.getId());
		verify(userService, times(1)).getUserByName(mockShortFilmEditData.getNewRoleFilmmaker());
		verifyNoMoreInteractions(userService);
	}

	@Test
	public void editShortFilmAddRoleCompanyTest() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.setId(999L);
		mockShortFilm.setUploader(mockFilmmaker);
		mockShortFilm.setTitle("Example title");
		mockShortFilm.setDescription("Example description");
		final ShortFilmEditData mockShortFilmEditData = ShortFilmEditData.fromShortFilm(mockShortFilm);
		mockShortFilmEditData.setNewRoleFilmmaker("company1");
		mockShortFilmEditData.setNewRoleType(RoleType.CINEMATOGRAPHER);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.getShortFilmById(mockShortFilm.getId())).thenReturn(Optional.of(mockShortFilm));
		when(userService.getUserByName(mockShortFilmEditData.getNewRoleFilmmaker()))
				.thenReturn(Optional.of(new Company()));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/shortfilm/%d/edit", mockShortFilm.getId())).with(csrf())
					.param("title", mockShortFilmEditData.getTitle())
					.param("description", mockShortFilmEditData.getDescription())
					.param("newTagName", mockShortFilmEditData.getNewTagName())
					.param("newRoleFilmmaker", mockShortFilmEditData.getNewRoleFilmmaker())
					.param("newRoleType", mockShortFilmEditData.getNewRoleType().toString()).param("addRole", ""))
					.andExpect(status().isOk()).andExpect(model().attribute("shortFilmEditData", mockShortFilmEditData))
					.andExpect(model().attributeHasFieldErrors("shortFilmEditData", "newRoleFilmmaker"))
					.andExpect(view().name("editShortFilm"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(shortFilmService, only()).getShortFilmById(mockShortFilm.getId());
		verify(userService, times(1)).getUserByName(mockShortFilmEditData.getNewRoleFilmmaker());
		verifyNoMoreInteractions(userService);
	}

	@Test
	public void editShortFilmRemoveRoleTest() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setName("filmmaker1");
		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.setId(999L);
		mockShortFilm.setUploader(mockFilmmaker);
		mockShortFilm.setTitle("Example title");
		mockShortFilm.setDescription("Example description");
		final ShortFilmEditData mockShortFilmEditData = ShortFilmEditData.fromShortFilm(mockShortFilm);
		mockShortFilmEditData.getRoles().add(RoleData.of(mockFilmmaker.getName(), RoleType.ACTOR));
		mockShortFilmEditData.getRoles().add(RoleData.of(mockFilmmaker.getName(), RoleType.ANIMATOR));
		final Integer roleIndexToRemove = 0;

		final ShortFilmEditData returnedShortFilmEditData = ShortFilmEditData.fromShortFilm(mockShortFilm);
		returnedShortFilmEditData.getRoles().addAll(mockShortFilmEditData.getRoles());
		returnedShortFilmEditData.getRoles().remove(roleIndexToRemove.intValue());
		returnedShortFilmEditData.getRolePagination().setTotalElements(returnedShortFilmEditData.getRoles().size());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.getShortFilmById(mockShortFilm.getId())).thenReturn(Optional.of(mockShortFilm));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/shortfilm/%d/edit", mockShortFilm.getId())).with(csrf())
					.param("title", mockShortFilmEditData.getTitle())
					.param("description", mockShortFilmEditData.getDescription())
					.param("newTagName", mockShortFilmEditData.getNewTagName())
					.param("newRoleFilmmaker", mockShortFilmEditData.getNewRoleFilmmaker())
					.param("newRoleType", mockShortFilmEditData.getNewRoleType().toString())
					.param("roles[0].filmmakerName", mockShortFilmEditData.getRoles().get(0).getFilmmakerName())
					.param("roles[0].roleType", mockShortFilmEditData.getRoles().get(0).getRoleType().toString())
					.param("roles[1].filmmakerName", mockShortFilmEditData.getRoles().get(1).getFilmmakerName())
					.param("roles[1].roleType", mockShortFilmEditData.getRoles().get(1).getRoleType().toString())
					.param("removeRole", String.valueOf(roleIndexToRemove))).andExpect(status().isOk())
					.andExpect(model().attribute("shortFilmEditData", returnedShortFilmEditData))
					.andExpect(view().name("editShortFilm"));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, only()).getShortFilmById(mockShortFilm.getId());
	}
	
	@Test
	public void editShortFilmRemoveRoleNotLoggedTest() {
		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/shortfilm/999/edit").with(csrf())
					.param("removeRole", ""))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(shortFilmService);
	}
	
	@Test
	public void editShortFilmRemoveRoleInvalidShortFilmTest() {
		Long shortFilmId = 999L;
		
		when(userService.getLoggedUser()).thenReturn(Optional.of(new Company()));
		when(shortFilmService.getShortFilmById(shortFilmId)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/shortfilm/%d/edit", shortFilmId)).with(csrf())
					.param("removeRole", ""))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/"));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, only()).getShortFilmById(shortFilmId);
	}

	@Test
	public void editShortFilmTest() throws TooBigException, InvalidExtensionException, RuntimeException {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setName("filmmaker1");
		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.setId(999L);
		mockShortFilm.setUploader(mockFilmmaker);
		mockShortFilm.setTitle("Example title");
		mockShortFilm.setDescription("Example description");
		final ShortFilmEditData mockShortFilmEditData = ShortFilmEditData.fromShortFilm(mockShortFilm);
		mockShortFilmEditData.setDescription("Example description 2");
		mockShortFilmEditData.getTags().add("Scifi");
		mockShortFilmEditData.getRoles().add(RoleData.of(mockFilmmaker.getName(), RoleType.ACTOR));
		mockShortFilmEditData.getRoles().add(RoleData.of(mockFilmmaker.getName(), RoleType.ANIMATOR));
		
		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.getShortFilmById(mockShortFilm.getId())).thenReturn(Optional.of(mockShortFilm));
		when(userService.getUserByName(mockFilmmaker.getName())).thenReturn(Optional.of(mockFilmmaker));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/shortfilm/%d/edit", mockShortFilm.getId()))
					.with(csrf())
					.param("title", mockShortFilmEditData.getTitle())
					.param("description", mockShortFilmEditData.getDescription())
					.param("newTagName", mockShortFilmEditData.getNewTagName())
					.param("newRoleFilmmaker", mockShortFilmEditData.getNewRoleFilmmaker())
					.param("newRoleType", mockShortFilmEditData.getNewRoleType().toString())
					.param("tags[0]", mockShortFilmEditData.getTags().get(0))
					.param("roles[0].filmmakerName", mockShortFilmEditData.getRoles().get(0).getFilmmakerName())
					.param("roles[0].roleType", mockShortFilmEditData.getRoles().get(0).getRoleType().toString())
					.param("roles[1].filmmakerName", mockShortFilmEditData.getRoles().get(1).getFilmmakerName())
					.param("roles[1].roleType", mockShortFilmEditData.getRoles().get(1).getRoleType().toString())
					.param("applyChanges", "")).andExpect(status().isOk())
					.andExpect(model().attribute("shortFilmEditData", mockShortFilmEditData))
					.andExpect(view().name("editShortFilm"));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, times(1)).getShortFilmById(mockShortFilm.getId());
		verify(tagService, only()).tagShortFilm(mockShortFilmEditData.getTags(), mockShortFilm);
		verify(roleService, only()).setRolesOfShortFilm(mockShortFilmEditData.getRoles(), mockShortFilm);
		verify(shortFilmService, times(1)).updateShortFilmMetadata(mockShortFilm, mockShortFilmEditData.getTitle(),
				mockShortFilmEditData.getDescription());
		verifyNoMoreInteractions(shortFilmService);
	}
	
	@Test
	public void editShortFilmUploadThumbnailTest() throws TooBigException, InvalidExtensionException, RuntimeException {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setName("filmmaker1");
		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.setId(999L);
		mockShortFilm.setUploader(mockFilmmaker);
		mockShortFilm.setTitle("Example title");
		mockShortFilm.setDescription("Example description");
		mockShortFilm.setThumbnailUrl("exampleThumbnailUrl");
		final ShortFilmEditData mockShortFilmEditData = ShortFilmEditData.fromShortFilm(mockShortFilm);
		final MockMultipartFile mockThumbnailFile = new MockMultipartFile("newThumbnailFile", "example.png", "image/png", "This is an example".getBytes());
		mockShortFilmEditData.setNewThumbnailFile(mockThumbnailFile);
		
		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.getShortFilmById(mockShortFilm.getId())).thenReturn(Optional.of(mockShortFilm));
		when(userService.getUserByName(mockFilmmaker.getName())).thenReturn(Optional.of(mockFilmmaker));

		assertDoesNotThrow(() -> {
			mockMvc.perform(multipart(String.format("/shortfilm/%d/edit", mockShortFilm.getId()))
					.file(mockThumbnailFile).with(csrf())
					.param("title", mockShortFilmEditData.getTitle())
					.param("description", mockShortFilmEditData.getDescription())
					.param("newTagName", mockShortFilmEditData.getNewTagName())
					.param("newRoleFilmmaker", mockShortFilmEditData.getNewRoleFilmmaker())
					.param("newRoleType", mockShortFilmEditData.getNewRoleType().toString())
					.param("applyChanges", "")).andExpect(status().isOk())
					.andExpect(model().attribute("shortFilmEditData", mockShortFilmEditData))
					.andExpect(view().name("editShortFilm"));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, times(1)).getShortFilmById(mockShortFilm.getId());
		verify(shortFilmService, times(1)).uploadThumbnail(mockShortFilm, mockThumbnailFile);
		verify(tagService, only()).tagShortFilm(mockShortFilmEditData.getTags(), mockShortFilm);
		verify(roleService, only()).setRolesOfShortFilm(mockShortFilmEditData.getRoles(), mockShortFilm);
		verify(shortFilmService, times(1)).updateShortFilmMetadata(mockShortFilm, mockShortFilmEditData.getTitle(),
				mockShortFilmEditData.getDescription());
		verifyNoMoreInteractions(shortFilmService);
	}
	
	@Test
	public void editShortFilmUploadInvalidThumbnailTest() throws TooBigException, InvalidExtensionException, RuntimeException {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setName("filmmaker1");
		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.setId(999L);
		mockShortFilm.setUploader(mockFilmmaker);
		mockShortFilm.setTitle("Example title");
		mockShortFilm.setDescription("Example description");
		final ShortFilmEditData mockShortFilmEditData = ShortFilmEditData.fromShortFilm(mockShortFilm);
		final MockMultipartFile mockThumbnailFile = new MockMultipartFile("newThumbnailFile", "example.mp4", "video/mp4", "This is an example".getBytes());
		mockShortFilmEditData.setNewThumbnailFile(mockThumbnailFile);
		
		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.getShortFilmById(mockShortFilm.getId())).thenReturn(Optional.of(mockShortFilm));
		doThrow(new InvalidExtensionException("Example exception")).when(shortFilmService).uploadThumbnail(mockShortFilm, mockThumbnailFile);
		when(userService.getUserByName(mockFilmmaker.getName())).thenReturn(Optional.of(mockFilmmaker));

		assertDoesNotThrow(() -> {
			mockMvc.perform(multipart(String.format("/shortfilm/%d/edit", mockShortFilm.getId()))
					.file(mockThumbnailFile).with(csrf())
					.param("title", mockShortFilmEditData.getTitle())
					.param("description", mockShortFilmEditData.getDescription())
					.param("newTagName", mockShortFilmEditData.getNewTagName())
					.param("newRoleFilmmaker", mockShortFilmEditData.getNewRoleFilmmaker())
					.param("newRoleType", mockShortFilmEditData.getNewRoleType().toString())
					.param("applyChanges", "")).andExpect(status().isOk())
					.andExpect(model().attribute("shortFilmEditData", mockShortFilmEditData))
					.andExpect(model().attributeHasFieldErrors("shortFilmEditData", "newThumbnailFile"))
					.andExpect(view().name("editShortFilm"));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, times(1)).getShortFilmById(mockShortFilm.getId());
		verify(shortFilmService, times(1)).uploadThumbnail(mockShortFilm, mockThumbnailFile);
		verifyNoMoreInteractions(shortFilmService);
		verifyNoInteractions(tagService);
		verifyNoInteractions(roleService);
	}

	@Test
	public void editShortFilmNotLoggedTest() {
		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/shortfilm/999/edit").with(csrf())
					.param("applyChanges", ""))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(shortFilmService);
	}
	
	@Test
	public void editShortFilmInvalidShortFilmTest() {
		Long shortFilmId = 999L;
		
		when(userService.getLoggedUser()).thenReturn(Optional.of(new Company()));
		when(shortFilmService.getShortFilmById(shortFilmId)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/shortfilm/%d/edit", shortFilmId)).with(csrf())
					.param("applyChanges", ""))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl("/"));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, only()).getShortFilmById(shortFilmId);
	}
	
	@Test
	public void editShortFilmMissingDataTest() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setName("filmmaker1");
		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.setId(999L);
		mockShortFilm.setUploader(mockFilmmaker);
		mockShortFilm.setTitle("Example title");
		mockShortFilm.setDescription("Example description");
		final ShortFilmEditData mockShortFilmEditData = ShortFilmEditData.fromShortFilm(mockShortFilm);
		mockShortFilmEditData.setTitle("                 ");
		mockShortFilmEditData.setDescription("Example description 2");
		mockShortFilmEditData.getTags().add("Scifi");
		mockShortFilmEditData.getRoles().add(RoleData.of(mockFilmmaker.getName(), RoleType.ACTOR));
		mockShortFilmEditData.getRoles().add(RoleData.of(mockFilmmaker.getName(), RoleType.ANIMATOR));

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));
		when(shortFilmService.getShortFilmById(mockShortFilm.getId())).thenReturn(Optional.of(mockShortFilm));
		when(userService.getUserByName(mockFilmmaker.getName())).thenReturn(Optional.of(mockFilmmaker));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post(String.format("/shortfilm/%d/edit", mockShortFilm.getId())).with(csrf())
					.param("title", mockShortFilmEditData.getTitle())
					.param("description", mockShortFilmEditData.getDescription())
					.param("newTagName", mockShortFilmEditData.getNewTagName())
					.param("newRoleFilmmaker", mockShortFilmEditData.getNewRoleFilmmaker())
					.param("newRoleType", mockShortFilmEditData.getNewRoleType().toString())
					.param("tags[0]", mockShortFilmEditData.getTags().get(0))
					.param("roles[0].filmmakerName", mockShortFilmEditData.getRoles().get(0).getFilmmakerName())
					.param("roles[0].roleType", mockShortFilmEditData.getRoles().get(0).getRoleType().toString())
					.param("roles[1].filmmakerName", mockShortFilmEditData.getRoles().get(1).getFilmmakerName())
					.param("roles[1].roleType", mockShortFilmEditData.getRoles().get(1).getRoleType().toString())
					.param("applyChanges", "")).andExpect(status().isOk())
					.andExpect(model().attribute("shortFilmEditData", mockShortFilmEditData))
					.andExpect(model().attributeHasFieldErrors("shortFilmEditData", "title"))
					.andExpect(view().name("editShortFilm"));
		});

		verify(userService, only()).getLoggedUser();
		verify(shortFilmService, only()).getShortFilmById(mockShortFilm.getId());
		verifyNoInteractions(tagService);
		verifyNoInteractions(roleService);
	}

	@Test
	public void getShortfilmViewAsLoggedUser() {
		final User mockUser = new User();
		mockUser.setName("user1");
		mockUser.setId(234L);
		mockUser.setPhotoUrl("profile.jpg");
		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.setId(999L);
		mockShortFilm.setUploadDate(1L);
		mockShortFilm.setUploader(new Filmmaker());
		mockShortFilm.setTitle("Example title");
		mockShortFilm.setDescription("Example description");
		final Rating userRate = new Rating();
		userRate.setGrade(1);
		userRate.setUser(mockUser);
		final ShortFilmViewData mockShortFilmViewData = new ShortFilmViewData();
		mockShortFilmViewData.setCommentPagination(Pagination.empty());

		when(shortFilmService.getShortFilmById(mockShortFilm.getId())).thenReturn(Optional.of(mockShortFilm));
		when(commentService.getCommentCountByShortFilm(mockShortFilm)).thenReturn(1);
		when(commentService.getCommentsByShortFilm(mockShortFilm,
				mockShortFilmViewData.getCommentPagination().getPageRequest(Sort.by("date").descending())))
						.thenReturn(Page.empty());
		when(userService.getLoggedUser()).thenReturn(Optional.of(mockUser));
		when(favouriteService.hasFavouriteShortFilm(mockShortFilm, mockUser)).thenReturn(true);
		when(ratingService.getRatingCount(mockShortFilm)).thenReturn(3L);
		when(ratingService.getRatingByUserAndShortFilm(mockUser, mockShortFilm)).thenReturn(userRate);
		when(subscriptionService.getFollowedCount(any(Filmmaker.class))).thenReturn(1);

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/shortfilm/999"))
					.andExpect(model().attribute("shortFilmViewData",
							Matchers.hasProperty("hasFavourite", Matchers.equalTo(true))))
					.andExpect(model().attribute("shortFilmViewData", Matchers.hasProperty("watcherId")))
					.andExpect(model().attribute("shortFilmViewData", Matchers.hasProperty("watcherName")))
					.andExpect(model().attribute("shortFilmViewData", Matchers.hasProperty("watcherPhotoUrl")))
					.andExpect(view().name("viewShortFilm"));
		});

		verify(shortFilmService, times(1)).getShortFilmById(mockShortFilm.getId());
		verify(shortFilmService, times(1)).updateViewCount(any(ShortFilm.class), any(Integer.class));
		verifyNoMoreInteractions(shortFilmService);
		verify(commentService, times(1)).getCommentCountByShortFilm(mockShortFilm);
		verify(commentService, times(1)).getCommentsByShortFilm(mockShortFilm,
				mockShortFilmViewData.getCommentPagination().getPageRequest(Sort.by("date").descending()));
		verifyNoMoreInteractions(commentService);
		verify(userService, times(1)).getLoggedUser();
		verifyNoMoreInteractions(userService);
		verify(favouriteService, only()).hasFavouriteShortFilm(mockShortFilm, mockUser);
		verify(ratingService, times(1)).getRatingCount(mockShortFilm);
		verify(ratingService, times(1)).getRatingByUserAndShortFilm(mockUser, mockShortFilm);
		verifyNoMoreInteractions(ratingService);
		verify(subscriptionService, only()).getFollowerCount(any(Filmmaker.class));
	}

	@Test
	public void getShortfilmViewAsNotLoggedUser() {
		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.setId(999L);
		mockShortFilm.setUploadDate(1L);
		mockShortFilm.setUploader(new Filmmaker());
		mockShortFilm.setTitle("Example title");
		mockShortFilm.setDescription("Example description");
		final ShortFilmViewData mockShortFilmViewData = new ShortFilmViewData();
		mockShortFilmViewData.setCommentPagination(Pagination.empty());

		when(shortFilmService.getShortFilmById(mockShortFilm.getId())).thenReturn(Optional.of(mockShortFilm));
		when(commentService.getCommentCountByShortFilm(mockShortFilm)).thenReturn(1);
		when(commentService.getCommentsByShortFilm(mockShortFilm,
				mockShortFilmViewData.getCommentPagination().getPageRequest(Sort.by("date").descending())))
						.thenReturn(Page.empty());
		when(userService.getLoggedUser()).thenReturn(Optional.empty());
		when(ratingService.getRatingCount(mockShortFilm)).thenReturn(3L);
		when(ratingService.getRatingByUserAndShortFilm(null, mockShortFilm)).thenReturn(null);
		when(subscriptionService.getFollowedCount(any(Filmmaker.class))).thenReturn(1);

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/shortfilm/999"))
					.andExpect(model().attribute("shortFilmViewData",
							Matchers.hasProperty("hasFavourite", Matchers.equalTo(false))))
					.andExpect(view().name("viewShortFilm"));
		});

		verify(shortFilmService, times(1)).getShortFilmById(mockShortFilm.getId());
		verify(shortFilmService, times(1)).updateViewCount(any(ShortFilm.class), any(Integer.class));
		verifyNoMoreInteractions(shortFilmService);
		verify(commentService, times(1)).getCommentCountByShortFilm(mockShortFilm);
		verify(commentService, times(1)).getCommentsByShortFilm(mockShortFilm,
				mockShortFilmViewData.getCommentPagination().getPageRequest(Sort.by("date").descending()));
		verifyNoMoreInteractions(commentService);
		verify(userService, times(1)).getLoggedUser();
		verifyNoMoreInteractions(userService);
		verify(ratingService, times(1)).getRatingCount(mockShortFilm);
		verify(ratingService, times(1)).getRatingByUserAndShortFilm(null, mockShortFilm);
		verifyNoMoreInteractions(ratingService);
		verify(subscriptionService, only()).getFollowerCount(any(Filmmaker.class));

	}

	@Test
	public void getNonexistanceShortFilmView() {
		when(shortFilmService.getShortFilmById(any(Long.class))).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/shortfilm/999")).andExpect(status().isFound()).andExpect(redirectedUrl("/"));
		});

		verify(shortFilmService, only()).getShortFilmById(any(Long.class));
		verifyNoInteractions(commentService);
		verifyNoInteractions(userService);
		verifyNoInteractions(ratingService);
		verifyNoInteractions(favouriteService);
		verifyNoInteractions(subscriptionService);
	}
}
