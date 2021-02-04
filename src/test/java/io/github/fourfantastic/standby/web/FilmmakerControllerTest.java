package io.github.fourfantastic.standby.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.model.PrivacyRequest;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.form.FilmmakerConfigurationData;
import io.github.fourfantastics.standby.model.form.FilmmakerRegisterData;
import io.github.fourfantastics.standby.service.FilmmakerService;
import io.github.fourfantastics.standby.service.PrivacyRequestService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.InvalidExtensionException;
import io.github.fourfantastics.standby.service.exception.NotUniqueException;
import io.github.fourfantastics.standby.service.exception.TooBigException;
import io.github.fourfantastics.standby.utils.Utils;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
@AutoConfigureMockMvc
public class FilmmakerControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	FilmmakerService filmmakerService;

	@MockBean
	UserService userService;

	@MockBean
	ShortFilmService shortFilmService;

	@MockBean
	PrivacyRequestService privacyRequestService;

	@Test
	void registerViewTest() {
		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/register/filmmaker")).andExpect(status().isOk())
					.andExpect(model().attribute("filmmakerRegisterData", new FilmmakerRegisterData()))
					.andExpect(view().name("registerFilmmaker"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(filmmakerService);
	}

	@Test
	void registerViewUserIsPresentTest() {
		when(userService.getLoggedUser()).thenReturn(Optional.of(new User()));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/register/filmmaker")).andExpect(status().isFound()).andExpect(redirectedUrl("/"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(filmmakerService);
	}

	@Test
	void registerFilmmakerTest() throws Exception {
		final FilmmakerRegisterData mockFilmmakerRegisterData = new FilmmakerRegisterData();
		mockFilmmakerRegisterData.setName("filmmaker1");
		mockFilmmakerRegisterData.setEmail("filmmaker1@gmail.com");
		mockFilmmakerRegisterData.setPassword("password");
		mockFilmmakerRegisterData.setConfirmPassword("password");
		mockFilmmakerRegisterData.setFullname("Filmmaker1");
		mockFilmmakerRegisterData.setCountry("Spain");
		mockFilmmakerRegisterData.setCity("Seville");
		mockFilmmakerRegisterData.setPhone("678543167");

		when(userService.getLoggedUser()).thenReturn(Optional.empty());
		when(filmmakerService.registerFilmmaker(mockFilmmakerRegisterData))
				.then(x -> ((FilmmakerRegisterData) x.getArgument(0)).toFilmmaker());

		mockMvc.perform(post("/register/filmmaker").with(csrf()).param("name", mockFilmmakerRegisterData.getName())
				.param("email", mockFilmmakerRegisterData.getEmail())
				.param("password", mockFilmmakerRegisterData.getPassword())
				.param("confirmPassword", mockFilmmakerRegisterData.getConfirmPassword())
				.param("fullname", mockFilmmakerRegisterData.getFullname())
				.param("country", mockFilmmakerRegisterData.getCountry())
				.param("city", mockFilmmakerRegisterData.getCity())
				.param("phone", mockFilmmakerRegisterData.getPhone())).andExpect(status().isFound())
				.andExpect(redirectedUrl("/"));

		verify(userService, times(1)).getLoggedUser();
		verify(filmmakerService, only()).registerFilmmaker(mockFilmmakerRegisterData);
		verifyNoMoreInteractions(filmmakerService);
		verifyNoMoreInteractions(userService);
	}

	@Test
	void registerFilmmakerUserIsPresentTest() throws NotUniqueException {
		final FilmmakerRegisterData mockFilmmakerRegisterData = new FilmmakerRegisterData();
		mockFilmmakerRegisterData.setName("filmmaker1");
		mockFilmmakerRegisterData.setEmail("filmmaker1@gmail.com");
		mockFilmmakerRegisterData.setPassword("password");
		mockFilmmakerRegisterData.setConfirmPassword("password");
		mockFilmmakerRegisterData.setFullname("Filmmaker1");
		mockFilmmakerRegisterData.setCountry("Spain");
		mockFilmmakerRegisterData.setCity("Seville");
		mockFilmmakerRegisterData.setPhone("678543167");

		when(userService.getLoggedUser()).thenReturn(Optional.of(new User()));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/register/filmmaker").with(csrf()).param("name", mockFilmmakerRegisterData.getName())
					.param("email", mockFilmmakerRegisterData.getEmail())
					.param("password", mockFilmmakerRegisterData.getPassword())
					.param("confirmPassword", mockFilmmakerRegisterData.getConfirmPassword())
					.param("fullname", mockFilmmakerRegisterData.getFullname())
					.param("country", mockFilmmakerRegisterData.getCountry())
					.param("city", mockFilmmakerRegisterData.getCity())
					.param("phone", mockFilmmakerRegisterData.getPhone())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(filmmakerService);
	}

	@Test
	void registerFilmmakerMissingDataTest() throws NotUniqueException {
		final FilmmakerRegisterData mockFilmmakerRegisterData = new FilmmakerRegisterData();
		mockFilmmakerRegisterData.setName("");
		mockFilmmakerRegisterData.setEmail("filmmaker1@gmail.com");
		mockFilmmakerRegisterData.setPassword("password");
		mockFilmmakerRegisterData.setConfirmPassword("");
		mockFilmmakerRegisterData.setFullname("Filmmaker1");
		mockFilmmakerRegisterData.setCountry("Spain");
		mockFilmmakerRegisterData.setCity("Seville");
		mockFilmmakerRegisterData.setPhone("678543167");

		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/register/filmmaker").with(csrf()).param("name", mockFilmmakerRegisterData.getName())
					.param("email", mockFilmmakerRegisterData.getEmail())
					.param("password", mockFilmmakerRegisterData.getPassword())
					.param("confirmPassword", mockFilmmakerRegisterData.getConfirmPassword())
					.param("fullname", mockFilmmakerRegisterData.getFullname())
					.param("country", mockFilmmakerRegisterData.getCountry())
					.param("city", mockFilmmakerRegisterData.getCity())
					.param("phone", mockFilmmakerRegisterData.getPhone())).andExpect(status().isOk())
					.andExpect(model().attributeHasErrors("filmmakerRegisterData"))
					.andExpect(view().name("registerFilmmaker"));
		});

		verify(userService, only()).getLoggedUser();
		verifyNoInteractions(filmmakerService);
	}

	@Test
	void registerFilmmakerAlreadyUsedNameTest() throws NotUniqueException {
		final FilmmakerRegisterData mockFilmmakerRegisterData = new FilmmakerRegisterData();
		mockFilmmakerRegisterData.setName("filmmaker1");
		mockFilmmakerRegisterData.setEmail("filmmaker1@gmail.com");
		mockFilmmakerRegisterData.setPassword("password");
		mockFilmmakerRegisterData.setConfirmPassword("password");
		mockFilmmakerRegisterData.setFullname("Filmmaker1");
		mockFilmmakerRegisterData.setCountry("Spain");
		mockFilmmakerRegisterData.setCity("Seville");
		mockFilmmakerRegisterData.setPhone("678543167");

		when(userService.getLoggedUser()).thenReturn(Optional.empty());
		when(filmmakerService.registerFilmmaker(mockFilmmakerRegisterData))
				.thenThrow(new NotUniqueException("", Utils.hashSet("name")));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/register/filmmaker").with(csrf()).param("name", mockFilmmakerRegisterData.getName())
					.param("email", mockFilmmakerRegisterData.getEmail())
					.param("password", mockFilmmakerRegisterData.getPassword())
					.param("confirmPassword", mockFilmmakerRegisterData.getConfirmPassword())
					.param("fullname", mockFilmmakerRegisterData.getFullname())
					.param("country", mockFilmmakerRegisterData.getCountry())
					.param("city", mockFilmmakerRegisterData.getCity())
					.param("phone", mockFilmmakerRegisterData.getPhone())).andExpect(status().isOk())
					.andExpect(model().attributeHasFieldErrors("filmmakerRegisterData", "name"))
					.andExpect(view().name("registerFilmmaker"));
		});

		verify(userService, only()).getLoggedUser();
		verify(filmmakerService, only()).registerFilmmaker(mockFilmmakerRegisterData);
	}

	@Test
	void manageAccountFilmmakerView() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setFullname("Filmmaker1");
		mockFilmmaker.setCountry("Spain");
		mockFilmmaker.setCity("Seville");
		mockFilmmaker.setPhone("678543167");
		mockFilmmaker.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/account/filmmaker")).andExpect(status().isOk())
					.andExpect(model().attribute("filmmakerConfigurationData",
							FilmmakerConfigurationData.fromFilmmaker(mockFilmmaker)))
					.andExpect(view().name("manageFilmmakerAccount"));
		});

		verify(userService, only()).getLoggedUser();
	}

	@Test
	void manageAccountFilmmaker() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setFullname("Filmmaker1");
		mockFilmmaker.setCountry("Spain");
		mockFilmmaker.setCity("Seville");
		mockFilmmaker.setPhone("678543167");
		mockFilmmaker.setConfiguration(new NotificationConfiguration());

		final FilmmakerConfigurationData mockConfigFilmmaker = new FilmmakerConfigurationData();
		mockConfigFilmmaker.setByComments(false);
		mockConfigFilmmaker.setByRatings(true);
		mockConfigFilmmaker.setBySubscriptions(true);
		mockConfigFilmmaker.setCity("Huelva");
		mockConfigFilmmaker.setCountry("Spain");
		mockConfigFilmmaker.setFullname("Filmmaker1");
		mockConfigFilmmaker.setPhone("616449997");

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/account/filmmaker").with(csrf()).param("fullname", mockConfigFilmmaker.getFullname())
					.param("country", mockConfigFilmmaker.getCountry()).param("city", mockConfigFilmmaker.getCity())
					.param("phone", mockConfigFilmmaker.getPhone())
					.param("byComments", mockConfigFilmmaker.getByComments().toString())
					.param("byRatings", mockConfigFilmmaker.getByRatings().toString())
					.param("bySubscriptions", mockConfigFilmmaker.getBySubscriptions().toString()))
					.andExpect(status().isOk()).andExpect(view().name("manageFilmmakerAccount"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(filmmakerService, only()).updateFilmmakerData(mockFilmmaker, mockConfigFilmmaker);
		verifyNoMoreInteractions(userService);
	}

	@Test
	void manageAccountFilmmakerChangePicture() throws TooBigException, InvalidExtensionException, RuntimeException {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setFullname("Filmmaker1");
		mockFilmmaker.setCountry("Spain");
		mockFilmmaker.setCity("Seville");
		mockFilmmaker.setPhone("678543167");
		mockFilmmaker.setConfiguration(new NotificationConfiguration());

		final FilmmakerConfigurationData mockConfigFilmmaker = new FilmmakerConfigurationData();
		mockConfigFilmmaker.setByComments(false);
		mockConfigFilmmaker.setByRatings(true);
		mockConfigFilmmaker.setBySubscriptions(true);
		mockConfigFilmmaker.setCity("Huelva");
		mockConfigFilmmaker.setCountry("Spain");
		mockConfigFilmmaker.setFullname("Filmmaker1");
		mockConfigFilmmaker.setPhone("616449997");
		mockConfigFilmmaker.setNewPhoto(
				new MockMultipartFile("newPhoto", "mockFile.png", "image/png", "This is an example".getBytes()));

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));

		assertDoesNotThrow(() -> {
			mockMvc.perform(multipart("/account/filmmaker").file((MockMultipartFile) mockConfigFilmmaker.getNewPhoto())
					.with(csrf()).param("fullname", mockConfigFilmmaker.getFullname())
					.param("country", mockConfigFilmmaker.getCountry()).param("city", mockConfigFilmmaker.getCity())
					.param("phone", mockConfigFilmmaker.getPhone())
					.param("byComments", mockConfigFilmmaker.getByComments().toString())
					.param("byRatings", mockConfigFilmmaker.getByRatings().toString())
					.param("bySubscriptions", mockConfigFilmmaker.getBySubscriptions().toString()))
					.andExpect(status().isOk()).andExpect(view().name("manageFilmmakerAccount"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(filmmakerService, only()).updateFilmmakerData(mockFilmmaker, mockConfigFilmmaker);
		verify(userService, times(1)).setProfilePicture(mockFilmmaker, mockConfigFilmmaker.getNewPhoto());
		verifyNoMoreInteractions(userService);
	}

	@Test
	void manageAccountFilmmakerNotLogged() {
		final FilmmakerConfigurationData mockConfigFilmmaker = new FilmmakerConfigurationData();
		mockConfigFilmmaker.setByComments(false);
		mockConfigFilmmaker.setByRatings(true);
		mockConfigFilmmaker.setBySubscriptions(true);
		mockConfigFilmmaker.setCity("Huelva");
		mockConfigFilmmaker.setCountry("Spain");
		mockConfigFilmmaker.setFullname("Filmmaker1");
		mockConfigFilmmaker.setPhone("616449997");

		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/account/filmmaker").with(csrf()).param("fullname", mockConfigFilmmaker.getFullname())
					.param("country", mockConfigFilmmaker.getCountry()).param("city", mockConfigFilmmaker.getCity())
					.param("phone", mockConfigFilmmaker.getPhone())
					.param("byComments", mockConfigFilmmaker.getByComments().toString())
					.param("byRatings", mockConfigFilmmaker.getByRatings().toString())
					.param("bySubscriptions", mockConfigFilmmaker.getBySubscriptions().toString()))
					.andExpect(status().isFound()).andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
	}

	@Test
	void manageAccountFilmmakerMissingData() {
		final Filmmaker mockFilmmaker = new Filmmaker();
		mockFilmmaker.setFullname("Filmmaker1");
		mockFilmmaker.setCountry("Spain");
		mockFilmmaker.setCity("Seville");
		mockFilmmaker.setPhone("678543167");
		mockFilmmaker.setConfiguration(new NotificationConfiguration());

		final FilmmakerConfigurationData mockConfigFilmmaker = new FilmmakerConfigurationData();
		mockConfigFilmmaker.setByComments(false);
		mockConfigFilmmaker.setByRatings(true);
		mockConfigFilmmaker.setBySubscriptions(true);
		mockConfigFilmmaker.setCity("Seville");
		mockConfigFilmmaker.setCountry("Spain");
		mockConfigFilmmaker.setFullname("");
		mockConfigFilmmaker.setPhone("616449997");

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmaker));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/account/filmmaker").with(csrf()).param("fullname", mockConfigFilmmaker.getFullname())
					.param("country", mockConfigFilmmaker.getCountry()).param("city", mockConfigFilmmaker.getCity())
					.param("phone", mockConfigFilmmaker.getPhone())
					.param("byComments", mockConfigFilmmaker.getByComments().toString())
					.param("byRatings", mockConfigFilmmaker.getByRatings().toString())
					.param("bySubscriptions", mockConfigFilmmaker.getBySubscriptions().toString()))
					.andExpect(status().isOk()).andExpect(view().name("manageFilmmakerAccount"));
		});

		verify(userService, only()).getLoggedUser();
	}

	@Test
	void manageAccountFilmmakerAsCompany() {
		final Company mockCompany = new Company();
		mockCompany.setBusinessPhone("675849765");
		mockCompany.setCompanyName("Company1");
		mockCompany.setOfficeAddress("Calle Manzanita 3");
		mockCompany.setTaxIDNumber("123-78-1234567");
		mockCompany.setConfiguration(new NotificationConfiguration());

		final FilmmakerConfigurationData mockConfigFilmmaker = new FilmmakerConfigurationData();
		mockConfigFilmmaker.setByComments(false);
		mockConfigFilmmaker.setByRatings(true);
		mockConfigFilmmaker.setBySubscriptions(true);
		mockConfigFilmmaker.setCity("");
		mockConfigFilmmaker.setCountry("");
		mockConfigFilmmaker.setFullname("Filmmaker1");
		mockConfigFilmmaker.setPhone("616449997");

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockCompany));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/account/filmmaker").with(csrf()).param("fullname", mockConfigFilmmaker.getFullname())
					.param("country", mockConfigFilmmaker.getCountry()).param("city", mockConfigFilmmaker.getCity())
					.param("phone", mockConfigFilmmaker.getPhone())
					.param("byComments", mockConfigFilmmaker.getByComments().toString())
					.param("byRatings", mockConfigFilmmaker.getByRatings().toString())
					.param("bySubscriptions", mockConfigFilmmaker.getBySubscriptions().toString()))
					.andExpect(status().isFound()).andExpect(redirectedUrl("/account"));
		});

		verify(userService, only()).getLoggedUser();
	}

	@Test
	void getProfileViewAsNotSubscribedCompanyTest() throws Exception {
		final Company mockViewerCompany = new Company();
		mockViewerCompany.setName("user1");
		mockViewerCompany.setBusinessPhone("675849765");
		mockViewerCompany.setCompanyName("Company1");
		mockViewerCompany.setOfficeAddress("Calle Manzanita 3");
		mockViewerCompany.setTaxIDNumber("123-78-1234567");
		mockViewerCompany.setConfiguration(new NotificationConfiguration());

		final Filmmaker mockViewed = new Filmmaker();
		mockViewed.setId(1L);
		mockViewed.setName("filmmaker1");
		mockViewed.setFullname("Filmmaker1");
		mockViewed.setCountry("Spain");
		mockViewed.setCity("Seville");
		mockViewed.setPhone("678543167");
		mockViewed.setConfiguration(new NotificationConfiguration());

		final List<ShortFilm> uploadedShortFilms = new ArrayList<ShortFilm>();
		final List<ShortFilm> attachedShortFilms = new ArrayList<ShortFilm>();

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockViewerCompany));
		when(userService.getUserById(mockViewed.getId())).thenReturn(Optional.of(mockViewed));
		when(shortFilmService.getShortFilmsCountByUploader(mockViewed)).thenReturn(uploadedShortFilms.size());
		when(shortFilmService.getShortFilmsByUploader(eq(mockViewed), any(PageRequest.class)))
				.thenReturn(new PageImpl<ShortFilm>(uploadedShortFilms));
		when(shortFilmService.getAttachedShortFilmsCountByFilmmaker(mockViewed.getId()))
				.thenReturn(attachedShortFilms.size());
		when(shortFilmService.getAttachedShortFilmsByFilmmaker(eq(mockViewed.getId()), any(PageRequest.class)))
				.thenReturn(new PageImpl<ShortFilm>(attachedShortFilms));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/profile/1")).andExpect(status().isOk())
					.andExpect(model().attribute("followButton", true))
					.andExpect(model().attribute("privacyRequestButton", true))
					.andExpect(model().attributeDoesNotExist("personalInformation", "disablePrivacyRequestButton",
							"accountButton", "alreadyFollowed"))
					.andExpect(view().name("filmmakerProfile"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1L);
		verifyNoMoreInteractions(userService);
		verify(shortFilmService, times(1)).getShortFilmsCountByUploader(mockViewed);
		verify(shortFilmService, times(1)).getShortFilmsByUploader(eq(mockViewed), any(PageRequest.class));
		verify(shortFilmService, times(1)).getAttachedShortFilmsCountByFilmmaker(mockViewed.getId());
		verify(shortFilmService, times(1)).getAttachedShortFilmsByFilmmaker(eq(mockViewed.getId()),
				any(PageRequest.class));
		verifyNoMoreInteractions(shortFilmService);
	}

	@Test
	void getProfileViewAsSubscribedCompanyTest() throws Exception {
		final User subscriptor = new User();
		subscriptor.setName("user1");

		final Company mockViewerCompany = new Company();
		mockViewerCompany.setId(2L);
		mockViewerCompany.setName("user1");
		mockViewerCompany.setBusinessPhone("675849765");
		mockViewerCompany.setCompanyName("Company1");
		mockViewerCompany.setOfficeAddress("Calle Manzanita 3");
		mockViewerCompany.setTaxIDNumber("123-78-1234567");
		mockViewerCompany.setConfiguration(new NotificationConfiguration());

		final Filmmaker mockViewed = new Filmmaker();
		mockViewed.setId(1L);
		mockViewed.setName("filmmaker1");
		mockViewed.setFullname("Filmmaker1");
		mockViewed.setCountry("Spain");
		mockViewed.setCity("Seville");
		mockViewed.setPhone("678543167");
		mockViewed.setConfiguration(new NotificationConfiguration());
		mockViewed.getFilmmakerSubscribers().add(subscriptor);

		final List<ShortFilm> uploadedShortFilms = new ArrayList<ShortFilm>();
		final List<ShortFilm> attachedShortFilms = new ArrayList<ShortFilm>();

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockViewerCompany));
		when(userService.getUserById(mockViewed.getId())).thenReturn(Optional.of(mockViewed));
		when(shortFilmService.getShortFilmsCountByUploader(mockViewed)).thenReturn(uploadedShortFilms.size());
		when(shortFilmService.getShortFilmsByUploader(eq(mockViewed), any(PageRequest.class)))
				.thenReturn(new PageImpl<ShortFilm>(uploadedShortFilms));
		when(shortFilmService.getAttachedShortFilmsCountByFilmmaker(mockViewed.getId()))
				.thenReturn(attachedShortFilms.size());
		when(shortFilmService.getAttachedShortFilmsByFilmmaker(eq(mockViewed.getId()), any(PageRequest.class)))
				.thenReturn(new PageImpl<ShortFilm>(attachedShortFilms));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/profile/1")).andExpect(status().isOk())
					.andExpect(model().attribute("followButton", true))
					.andExpect(model().attribute("alreadyFollowed", true))
					.andExpect(model().attribute("privacyRequestButton", true))
					.andExpect(model().attributeDoesNotExist("personalInformation", "disablePrivacyRequestButton",
							"accountButton"))
					.andExpect(view().name("filmmakerProfile"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1L);
		verifyNoMoreInteractions(userService);
		verify(shortFilmService, times(1)).getShortFilmsCountByUploader(mockViewed);
		verify(shortFilmService, times(1)).getShortFilmsByUploader(eq(mockViewed), any(PageRequest.class));
		verify(shortFilmService, times(1)).getAttachedShortFilmsCountByFilmmaker(mockViewed.getId());
		verify(shortFilmService, times(1)).getAttachedShortFilmsByFilmmaker(eq(mockViewed.getId()),
				any(PageRequest.class));
		verifyNoMoreInteractions(shortFilmService);
	}

	@Test
	void getProfileViewAsSentPrivacyRequestCompanyTest() throws Exception {
		final Filmmaker mockViewed = new Filmmaker();
		mockViewed.setId(1L);
		mockViewed.setName("filmmaker1");
		mockViewed.setFullname("Filmmaker1");
		mockViewed.setCountry("Spain");
		mockViewed.setCity("Seville");
		mockViewed.setPhone("678543167");
		mockViewed.setConfiguration(new NotificationConfiguration());

		final PrivacyRequest request = new PrivacyRequest();
		request.setFilmmaker(mockViewed);

		final Company mockViewerCompany = new Company();
		mockViewerCompany.setName("user1");
		mockViewerCompany.setBusinessPhone("675849765");
		mockViewerCompany.setCompanyName("Company1");
		mockViewerCompany.setOfficeAddress("Calle Manzanita 3");
		mockViewerCompany.setTaxIDNumber("123-78-1234567");
		mockViewerCompany.setConfiguration(new NotificationConfiguration());
		mockViewerCompany.getSentRequests().add(request);

		final List<ShortFilm> uploadedShortFilms = new ArrayList<ShortFilm>();
		final List<ShortFilm> attachedShortFilms = new ArrayList<ShortFilm>();

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockViewerCompany));
		when(userService.getUserById(mockViewed.getId())).thenReturn(Optional.of(mockViewed));
		when(shortFilmService.getShortFilmsCountByUploader(mockViewed)).thenReturn(uploadedShortFilms.size());
		when(shortFilmService.getShortFilmsByUploader(eq(mockViewed), any(PageRequest.class)))
				.thenReturn(new PageImpl<ShortFilm>(uploadedShortFilms));
		when(shortFilmService.getAttachedShortFilmsCountByFilmmaker(mockViewed.getId()))
				.thenReturn(attachedShortFilms.size());
		when(shortFilmService.getAttachedShortFilmsByFilmmaker(eq(mockViewed.getId()), any(PageRequest.class)))
				.thenReturn(new PageImpl<ShortFilm>(attachedShortFilms));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/profile/1")).andExpect(status().isOk())
					.andExpect(model().attribute("followButton", true))
					.andExpect(model().attribute("privacyRequestButton", true))
					.andExpect(model().attribute("disablePrivacyRequestButton", true))
					.andExpect(
							model().attributeDoesNotExist("accountButton", "alreadyFollowed"))
					.andExpect(view().name("filmmakerProfile"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1L);
		verifyNoMoreInteractions(userService);
		verify(shortFilmService, times(1)).getShortFilmsCountByUploader(mockViewed);
		verify(shortFilmService, times(1)).getShortFilmsByUploader(eq(mockViewed), any(PageRequest.class));
		verify(shortFilmService, times(1)).getAttachedShortFilmsCountByFilmmaker(mockViewed.getId());
		verify(shortFilmService, times(1)).getAttachedShortFilmsByFilmmaker(eq(mockViewed.getId()),
				any(PageRequest.class));
		verifyNoMoreInteractions(shortFilmService);
	}

	@Test
	void getProfileViewAsNotSubscribedFilmmakerTest() throws Exception {
		final Filmmaker mockViewerFilmmaker = new Filmmaker();
		mockViewerFilmmaker.setId(2L);
		mockViewerFilmmaker.setName("user1");
		mockViewerFilmmaker.setFullname("Filmmaker1");
		mockViewerFilmmaker.setCountry("Spain");
		mockViewerFilmmaker.setCity("Seville");
		mockViewerFilmmaker.setPhone("678543167");
		mockViewerFilmmaker.setConfiguration(new NotificationConfiguration());

		final Filmmaker mockViewed = new Filmmaker();
		mockViewed.setId(1L);
		mockViewed.setName("filmmaker1");
		mockViewed.setFullname("Filmmaker1");
		mockViewed.setCountry("Spain");
		mockViewed.setCity("Seville");
		mockViewed.setPhone("678543167");
		mockViewed.setConfiguration(new NotificationConfiguration());

		final List<ShortFilm> uploadedShortFilms = new ArrayList<ShortFilm>();
		final List<ShortFilm> attachedShortFilms = new ArrayList<ShortFilm>();

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockViewerFilmmaker));
		when(userService.getUserById(mockViewed.getId())).thenReturn(Optional.of(mockViewed));
		when(shortFilmService.getShortFilmsCountByUploader(mockViewed)).thenReturn(uploadedShortFilms.size());
		when(shortFilmService.getShortFilmsByUploader(eq(mockViewed), any(PageRequest.class)))
				.thenReturn(new PageImpl<ShortFilm>(uploadedShortFilms));
		when(shortFilmService.getAttachedShortFilmsCountByFilmmaker(mockViewed.getId()))
				.thenReturn(attachedShortFilms.size());
		when(shortFilmService.getAttachedShortFilmsByFilmmaker(eq(mockViewed.getId()), any(PageRequest.class)))
				.thenReturn(new PageImpl<ShortFilm>(attachedShortFilms));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/profile/1")).andExpect(status().isOk())
					.andExpect(model().attribute("followButton", true))
					.andExpect(model().attributeDoesNotExist("personalInformation", "disablePrivacyRequestButton",
							"accountButton", "alreadyFollowed", "privacyRequestButton"))
					.andExpect(view().name("filmmakerProfile"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1L);
		verifyNoMoreInteractions(userService);
		verify(shortFilmService, times(1)).getShortFilmsCountByUploader(mockViewed);
		verify(shortFilmService, times(1)).getShortFilmsByUploader(eq(mockViewed), any(PageRequest.class));
		verify(shortFilmService, times(1)).getAttachedShortFilmsCountByFilmmaker(mockViewed.getId());
		verify(shortFilmService, times(1)).getAttachedShortFilmsByFilmmaker(eq(mockViewed.getId()),
				any(PageRequest.class));
		verifyNoMoreInteractions(shortFilmService);
	}

	@Test
	void getProfileViewAsSubscribedFilmmakerTest() throws Exception {
		final User subscriptor = new User();
		subscriptor.setName("user1");

		final Filmmaker mockFollower = new Filmmaker();
		mockFollower.setId(2L);
		mockFollower.setName("user1");
		mockFollower.setFullname("Filmmaker1");
		mockFollower.setCountry("Spain");
		mockFollower.setCity("Seville");
		mockFollower.setPhone("678543167");
		mockFollower.setConfiguration(new NotificationConfiguration());

		final Filmmaker mockViewed = new Filmmaker();
		mockViewed.setId(1L);
		mockViewed.setName("filmmaker1");
		mockViewed.setFullname("Filmmaker1");
		mockViewed.setCountry("Spain");
		mockViewed.setCity("Seville");
		mockViewed.setPhone("678543167");
		mockViewed.setConfiguration(new NotificationConfiguration());
		mockViewed.getFilmmakerSubscribers().add(subscriptor);

		final List<ShortFilm> uploadedShortFilms = new ArrayList<ShortFilm>();
		final List<ShortFilm> attachedShortFilms = new ArrayList<ShortFilm>();

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFollower));
		when(userService.getUserById(mockViewed.getId())).thenReturn(Optional.of(mockViewed));
		when(shortFilmService.getShortFilmsCountByUploader(mockViewed)).thenReturn(uploadedShortFilms.size());
		when(shortFilmService.getShortFilmsByUploader(eq(mockViewed), any(PageRequest.class)))
				.thenReturn(new PageImpl<ShortFilm>(uploadedShortFilms));
		when(shortFilmService.getAttachedShortFilmsCountByFilmmaker(mockViewed.getId()))
				.thenReturn(attachedShortFilms.size());
		when(shortFilmService.getAttachedShortFilmsByFilmmaker(eq(mockViewed.getId()), any(PageRequest.class)))
				.thenReturn(new PageImpl<ShortFilm>(attachedShortFilms));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/profile/1")).andExpect(status().isOk())
					.andExpect(model().attribute("alreadyFollowed", true))
					.andExpect(model().attribute("followButton", true))
					.andExpect(model().attributeDoesNotExist("personalInformation", "disablePrivacyRequestButton",
							"accountButton", "privacyRequestButton"))
					.andExpect(view().name("filmmakerProfile"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1L);
		verifyNoMoreInteractions(userService);
		verify(shortFilmService, times(1)).getShortFilmsCountByUploader(mockViewed);
		verify(shortFilmService, times(1)).getShortFilmsByUploader(eq(mockViewed), any(PageRequest.class));
		verify(shortFilmService, times(1)).getAttachedShortFilmsCountByFilmmaker(mockViewed.getId());
		verify(shortFilmService, times(1)).getAttachedShortFilmsByFilmmaker(eq(mockViewed.getId()),
				any(PageRequest.class));
		verifyNoMoreInteractions(shortFilmService);
	}

	@Test
	void getYourOwnProfileViewTest() throws Exception {
		final Filmmaker mockViewed = new Filmmaker();
		mockViewed.setId(1L);
		mockViewed.setName("filmmaker1");
		mockViewed.setFullname("Filmmaker1");
		mockViewed.setCountry("Spain");
		mockViewed.setCity("Seville");
		mockViewed.setPhone("678543167");
		mockViewed.setConfiguration(new NotificationConfiguration());

		final List<ShortFilm> uploadedShortFilms = new ArrayList<ShortFilm>();
		final List<ShortFilm> attachedShortFilms = new ArrayList<ShortFilm>();

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockViewed));
		when(userService.getUserById(mockViewed.getId())).thenReturn(Optional.of(mockViewed));
		when(shortFilmService.getShortFilmsCountByUploader(mockViewed)).thenReturn(uploadedShortFilms.size());
		when(shortFilmService.getShortFilmsByUploader(eq(mockViewed), any(PageRequest.class)))
				.thenReturn(new PageImpl<ShortFilm>(uploadedShortFilms));
		when(shortFilmService.getAttachedShortFilmsCountByFilmmaker(mockViewed.getId()))
				.thenReturn(attachedShortFilms.size());
		when(shortFilmService.getAttachedShortFilmsByFilmmaker(eq(mockViewed.getId()), any(PageRequest.class)))
				.thenReturn(new PageImpl<ShortFilm>(attachedShortFilms));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/profile/1")).andExpect(status().isOk())
					.andExpect(model().attribute("accountButton", true))
					.andExpect(model().attribute("personalInformation", true))
					.andExpect(model().attributeDoesNotExist("disablePrivacyRequestButton", "alreadyFollowed",
							"followButton", "privacyRequestButton"))
					.andExpect(view().name("filmmakerProfile"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1L);
		verifyNoMoreInteractions(userService);
		verify(shortFilmService, times(1)).getShortFilmsCountByUploader(mockViewed);
		verify(shortFilmService, times(1)).getShortFilmsByUploader(eq(mockViewed), any(PageRequest.class));
		verify(shortFilmService, times(1)).getAttachedShortFilmsCountByFilmmaker(mockViewed.getId());
		verify(shortFilmService, times(1)).getAttachedShortFilmsByFilmmaker(eq(mockViewed.getId()),
				any(PageRequest.class));
		verifyNoMoreInteractions(shortFilmService);
	}

	@Test
	void getProfileViewAsUnregisteredTest() throws Exception {
		final Filmmaker mockViewed = new Filmmaker();
		mockViewed.setId(1L);
		mockViewed.setName("filmmaker1");
		mockViewed.setFullname("Filmmaker1");
		mockViewed.setCountry("Spain");
		mockViewed.setCity("Seville");
		mockViewed.setPhone("678543167");
		mockViewed.setConfiguration(new NotificationConfiguration());

		final List<ShortFilm> uploadedShortFilms = new ArrayList<ShortFilm>();
		final List<ShortFilm> attachedShortFilms = new ArrayList<ShortFilm>();

		when(userService.getLoggedUser()).thenReturn(Optional.empty());
		when(userService.getUserById(mockViewed.getId())).thenReturn(Optional.of(mockViewed));
		when(shortFilmService.getShortFilmsCountByUploader(mockViewed)).thenReturn(uploadedShortFilms.size());
		when(shortFilmService.getShortFilmsByUploader(eq(mockViewed), any(PageRequest.class)))
				.thenReturn(new PageImpl<ShortFilm>(uploadedShortFilms));
		when(shortFilmService.getAttachedShortFilmsCountByFilmmaker(mockViewed.getId()))
				.thenReturn(attachedShortFilms.size());
		when(shortFilmService.getAttachedShortFilmsByFilmmaker(eq(mockViewed.getId()), any(PageRequest.class)))
				.thenReturn(new PageImpl<ShortFilm>(attachedShortFilms));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/profile/1")).andExpect(status().isOk())
					.andExpect(model().attribute("followButton", true))
					.andExpect(model().attributeDoesNotExist("personalInformation", "disablePrivacyRequestButton",
							"accountButton", "alreadyFollowed", "privacyRequestButton"))
					.andExpect(view().name("filmmakerProfile"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1L);
		verifyNoMoreInteractions(userService);
		verify(shortFilmService, times(1)).getShortFilmsCountByUploader(mockViewed);
		verify(shortFilmService, times(1)).getShortFilmsByUploader(eq(mockViewed), any(PageRequest.class));
		verify(shortFilmService, times(1)).getAttachedShortFilmsCountByFilmmaker(mockViewed.getId());
		verify(shortFilmService, times(1)).getAttachedShortFilmsByFilmmaker(eq(mockViewed.getId()),
				any(PageRequest.class));
		verifyNoMoreInteractions(shortFilmService);
	}

	@Test
	void getNonExistentProfileViewTest() throws Exception {
		final Long filmmakerId = 1L;
		
		when(userService.getUserById(filmmakerId)).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(get(String.format("/profile/%d", filmmakerId))).andExpect(status().isFound()).andExpect(redirectedUrl("/"));
		});

		verify(userService, only()).getUserById(filmmakerId);
	}

	@Test
	void getCompanyProfileViewTest() throws Exception {
		final Company mockViewedCompany = new Company();
		mockViewedCompany.setId(1L);
		mockViewedCompany.setName("user1");
		mockViewedCompany.setBusinessPhone("675849765");
		mockViewedCompany.setCompanyName("Company1");
		mockViewedCompany.setOfficeAddress("Calle Manzanita 3");
		mockViewedCompany.setTaxIDNumber("123-78-1234567");
		mockViewedCompany.setConfiguration(new NotificationConfiguration());
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockViewedCompany));

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/profile/1")).andExpect(status().isFound()).andExpect(redirectedUrl("/"));
		});

		verify(userService, only()).getUserById(1L);
	}

	@Test
	void filmmakerSubscribesToFilmmakerTest() throws Exception {
		final Filmmaker mockFollower = new Filmmaker();
		mockFollower.setName("user1");
		mockFollower.setFullname("Filmmaker1");
		mockFollower.setCountry("Spain");
		mockFollower.setCity("Seville");
		mockFollower.setPhone("678543167");
		mockFollower.setConfiguration(new NotificationConfiguration());

		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		mockFilmmakerFollowed.setId(1L);
		mockFilmmakerFollowed.setName("filmmaker1");
		mockFilmmakerFollowed.setFullname("Filmmaker1");
		mockFilmmakerFollowed.setCountry("Spain");
		mockFilmmakerFollowed.setCity("Seville");
		mockFilmmakerFollowed.setPhone("678543167");
		mockFilmmakerFollowed.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFollower));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerFollowed));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/subscription").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verify(userService, times(1)).subscribesTo(mockFollower, mockFilmmakerFollowed);
		verifyNoMoreInteractions(userService);
	}

	@Test
	void companySubscribesToFilmmakerTest() throws Exception {
		final Company mockFollowerCompany = new Company();
		mockFollowerCompany.setName("user1");
		mockFollowerCompany.setBusinessPhone("675849765");
		mockFollowerCompany.setCompanyName("Company1");
		mockFollowerCompany.setOfficeAddress("Calle Manzanita 3");
		mockFollowerCompany.setTaxIDNumber("123-78-1234567");
		mockFollowerCompany.setConfiguration(new NotificationConfiguration());

		final Filmmaker mockFollowed = new Filmmaker();
		mockFollowed.setId(1L);
		mockFollowed.setName("filmmaker1");
		mockFollowed.setFullname("Filmmaker1");
		mockFollowed.setCountry("Spain");
		mockFollowed.setCity("Seville");
		mockFollowed.setPhone("678543167");
		mockFollowed.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFollowerCompany));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFollowed));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/subscription").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verify(userService, times(1)).subscribesTo(mockFollowerCompany, mockFollowed);
		verifyNoMoreInteractions(userService);
	}

	@Test
	void unregisterUserSubscribesToFilmmakerTest() throws Exception {
		final Filmmaker mockFollowed = new Filmmaker();
		mockFollowed.setId(1L);
		mockFollowed.setName("filmmaker1");
		mockFollowed.setFullname("Filmmaker1");
		mockFollowed.setCountry("Spain");
		mockFollowed.setCity("Seville");
		mockFollowed.setPhone("678543167");
		mockFollowed.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/subscription").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
	}

	@Test
	void userSubscribesToCompanyTest() throws Exception {
		final Filmmaker mockFollower = new Filmmaker();
		mockFollower.setName("user1");
		mockFollower.setFullname("Filmmaker1");
		mockFollower.setCountry("Spain");
		mockFollower.setCity("Seville");
		mockFollower.setPhone("678543167");
		mockFollower.setConfiguration(new NotificationConfiguration());

		final Company mockFollowedCompany = new Company();
		mockFollowedCompany.setId(1l);
		mockFollowedCompany.setName("user1");
		mockFollowedCompany.setBusinessPhone("675849765");
		mockFollowedCompany.setCompanyName("Company1");
		mockFollowedCompany.setOfficeAddress("Calle Manzanita 3");
		mockFollowedCompany.setTaxIDNumber("123-78-1234567");
		mockFollowedCompany.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFollower));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFollowedCompany));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/subscription").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verifyNoMoreInteractions(userService);
	}

	@Test
	void filmmakerSubscribesToItselfTest() throws Exception {
		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		mockFilmmakerFollowed.setId(1L);
		mockFilmmakerFollowed.setName("filmmaker1");
		mockFilmmakerFollowed.setFullname("Filmmaker1");
		mockFilmmakerFollowed.setCountry("Spain");
		mockFilmmakerFollowed.setCity("Seville");
		mockFilmmakerFollowed.setPhone("678543167");
		mockFilmmakerFollowed.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmakerFollowed));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerFollowed));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/subscription").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verifyNoMoreInteractions(userService);
	}

	@Test
	void alreadySubscribedUserSubscribesToFilmmakerTest() throws Exception {
		final Filmmaker mockFollower = new Filmmaker();
		mockFollower.setName("user1");
		mockFollower.setFullname("Filmmaker1");
		mockFollower.setCountry("Spain");
		mockFollower.setCity("Seville");
		mockFollower.setPhone("678543167");
		mockFollower.setConfiguration(new NotificationConfiguration());

		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		mockFilmmakerFollowed.setId(1L);
		mockFilmmakerFollowed.setName("filmmaker1");
		mockFilmmakerFollowed.setFullname("Filmmaker1");
		mockFilmmakerFollowed.setCountry("Spain");
		mockFilmmakerFollowed.setCity("Seville");
		mockFilmmakerFollowed.setPhone("678543167");
		mockFilmmakerFollowed.setConfiguration(new NotificationConfiguration());
		mockFilmmakerFollowed.getFilmmakerSubscribers().add(mockFollower);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFollower));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerFollowed));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/subscription").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verifyNoMoreInteractions(userService);
	}

	@Test
	void filmmakerUnsubscribesToFilmmakerTest() throws Exception {
		final Filmmaker mockFollower = new Filmmaker();
		mockFollower.setName("user1");
		mockFollower.setFullname("Filmmaker1");
		mockFollower.setCountry("Spain");
		mockFollower.setCity("Seville");
		mockFollower.setPhone("678543167");
		mockFollower.setConfiguration(new NotificationConfiguration());

		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		mockFilmmakerFollowed.setId(1L);
		mockFilmmakerFollowed.setName("filmmaker1");
		mockFilmmakerFollowed.setFullname("Filmmaker2");
		mockFilmmakerFollowed.setCountry("Spain");
		mockFilmmakerFollowed.setCity("Seville");
		mockFilmmakerFollowed.setPhone("678543167");
		mockFilmmakerFollowed.setConfiguration(new NotificationConfiguration());
		mockFilmmakerFollowed.getFilmmakerSubscribers().add(mockFollower);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFollower));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerFollowed));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/unsubscription").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verify(userService, times(1)).unsubscribesTo(mockFollower, mockFilmmakerFollowed);
		verifyNoMoreInteractions(userService);
	}

	@Test
	void companyUnsubscribesToFilmmakerTest() throws Exception {
		final Company mockFollowerCompany = new Company();
		mockFollowerCompany.setName("user1");
		mockFollowerCompany.setBusinessPhone("675849765");
		mockFollowerCompany.setCompanyName("Company1");
		mockFollowerCompany.setOfficeAddress("Calle Manzanita 3");
		mockFollowerCompany.setTaxIDNumber("123-78-1234567");
		mockFollowerCompany.setConfiguration(new NotificationConfiguration());

		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		mockFilmmakerFollowed.setId(1L);
		mockFilmmakerFollowed.setName("filmmaker1");
		mockFilmmakerFollowed.setFullname("Filmmaker2");
		mockFilmmakerFollowed.setCountry("Spain");
		mockFilmmakerFollowed.setCity("Seville");
		mockFilmmakerFollowed.setPhone("678543167");
		mockFilmmakerFollowed.setConfiguration(new NotificationConfiguration());
		mockFilmmakerFollowed.getFilmmakerSubscribers().add(mockFollowerCompany);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFollowerCompany));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerFollowed));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/unsubscription").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verify(userService, times(1)).unsubscribesTo(mockFollowerCompany, mockFilmmakerFollowed);
		verifyNoMoreInteractions(userService);
	}

	@Test
	void filmmakerUnsubscribesToItselfTest() throws Exception {
		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		mockFilmmakerFollowed.setId(1L);
		mockFilmmakerFollowed.setName("filmmaker1");
		mockFilmmakerFollowed.setFullname("Filmmaker1");
		mockFilmmakerFollowed.setCountry("Spain");
		mockFilmmakerFollowed.setCity("Seville");
		mockFilmmakerFollowed.setPhone("678543167");
		mockFilmmakerFollowed.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFilmmakerFollowed));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerFollowed));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/unsubscription").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verifyNoMoreInteractions(userService);
	}

	@Test
	void userUnsubscribesToCompanyTest() throws Exception {
		final Filmmaker mockFollower = new Filmmaker();
		mockFollower.setName("user1");
		mockFollower.setFullname("Filmmaker1");
		mockFollower.setCountry("Spain");
		mockFollower.setCity("Seville");
		mockFollower.setPhone("678543167");
		mockFollower.setConfiguration(new NotificationConfiguration());

		final Company mockFollowedCompany = new Company();
		mockFollowedCompany.setId(1l);
		mockFollowedCompany.setName("user1");
		mockFollowedCompany.setBusinessPhone("675849765");
		mockFollowedCompany.setCompanyName("Company1");
		mockFollowedCompany.setOfficeAddress("Calle Manzanita 3");
		mockFollowedCompany.setTaxIDNumber("123-78-1234567");
		mockFollowedCompany.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFollower));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFollowedCompany));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/unsubscription").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verifyNoMoreInteractions(userService);
	}

	@Test
	void notSubscribedUserUnsubscribesToFilmmakerTest() throws Exception {
		final Filmmaker mockFollower = new Filmmaker();
		mockFollower.setName("user1");
		mockFollower.setFullname("Filmmaker1");
		mockFollower.setCountry("Spain");
		mockFollower.setCity("Seville");
		mockFollower.setPhone("678543167");
		mockFollower.setConfiguration(new NotificationConfiguration());

		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		mockFilmmakerFollowed.setId(1L);
		mockFilmmakerFollowed.setName("filmmaker1");
		mockFilmmakerFollowed.setFullname("Filmmaker1");
		mockFilmmakerFollowed.setCountry("Spain");
		mockFilmmakerFollowed.setCity("Seville");
		mockFilmmakerFollowed.setPhone("678543167");
		mockFilmmakerFollowed.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockFollower));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerFollowed));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/unsubscription").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verifyNoMoreInteractions(userService);
	}

	@Test
	void unregisterUserUnsubscribesToFilmmakerTest() throws Exception {
		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		mockFilmmakerFollowed.setId(1L);
		mockFilmmakerFollowed.setName("filmmaker1");
		mockFilmmakerFollowed.setFullname("Filmmaker1");
		mockFilmmakerFollowed.setCountry("Spain");
		mockFilmmakerFollowed.setCity("Seville");
		mockFilmmakerFollowed.setPhone("678543167");
		mockFilmmakerFollowed.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/unsubscription").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
	}

	@Test
	void companySendsPrivacyRequestToFilmmakerTest() throws Exception {
		final Company mockSenderCompany = new Company();
		mockSenderCompany.setName("user1");
		mockSenderCompany.setBusinessPhone("675849765");
		mockSenderCompany.setCompanyName("Company1");
		mockSenderCompany.setOfficeAddress("Calle Manzanita 3");
		mockSenderCompany.setTaxIDNumber("123-78-1234567");
		mockSenderCompany.setConfiguration(new NotificationConfiguration());

		final Filmmaker mockFilmmakerReceiver = new Filmmaker();
		mockFilmmakerReceiver.setId(1L);
		mockFilmmakerReceiver.setName("filmmaker1");
		mockFilmmakerReceiver.setFullname("Filmmaker1");
		mockFilmmakerReceiver.setCountry("Spain");
		mockFilmmakerReceiver.setCity("Seville");
		mockFilmmakerReceiver.setPhone("678543167");
		mockFilmmakerReceiver.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockSenderCompany));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerReceiver));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/privacyrequest").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verify(privacyRequestService, only()).sendPrivacyRequest(mockSenderCompany, mockFilmmakerReceiver);
		verifyNoMoreInteractions(userService);
	}

	@Test
	void companySendsPrivacyRequestToCompanyTest() throws Exception {
		final Company mockSenderCompany = new Company();
		mockSenderCompany.setName("user1");
		mockSenderCompany.setBusinessPhone("675849765");
		mockSenderCompany.setCompanyName("Company1");
		mockSenderCompany.setOfficeAddress("Calle Manzanita 3");
		mockSenderCompany.setTaxIDNumber("123-78-1234567");
		mockSenderCompany.setConfiguration(new NotificationConfiguration());

		final Company mockReceiverCompany = new Company();
		mockReceiverCompany.setId(1l);
		mockReceiverCompany.setName("user1");
		mockReceiverCompany.setBusinessPhone("675849765");
		mockReceiverCompany.setCompanyName("Company1");
		mockReceiverCompany.setOfficeAddress("Calle Manzanita 3");
		mockReceiverCompany.setTaxIDNumber("123-78-1234567");
		mockReceiverCompany.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockSenderCompany));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockReceiverCompany));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/privacyrequest").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verifyNoMoreInteractions(userService);
	}

	@Test
	void companySendsPrivacyRequestMoreThanOnceToFilmmakerTest() throws Exception {
		final Filmmaker mockFilmmakerReceiver = new Filmmaker();
		mockFilmmakerReceiver.setId(1L);
		mockFilmmakerReceiver.setName("filmmaker1");
		mockFilmmakerReceiver.setFullname("Filmmaker1");
		mockFilmmakerReceiver.setCountry("Spain");
		mockFilmmakerReceiver.setCity("Seville");
		mockFilmmakerReceiver.setPhone("678543167");
		mockFilmmakerReceiver.setConfiguration(new NotificationConfiguration());

		final PrivacyRequest request = new PrivacyRequest();
		request.setFilmmaker(mockFilmmakerReceiver);

		final Company mockSenderCompany = new Company();
		mockSenderCompany.setName("user1");
		mockSenderCompany.setBusinessPhone("675849765");
		mockSenderCompany.setCompanyName("Company1");
		mockSenderCompany.setOfficeAddress("Calle Manzanita 3");
		mockSenderCompany.setTaxIDNumber("123-78-1234567");
		mockSenderCompany.setConfiguration(new NotificationConfiguration());
		mockSenderCompany.getSentRequests().add(request);

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockSenderCompany));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerReceiver));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/privacyrequest").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, times(1)).getLoggedUser();
		verify(userService, times(1)).getUserById(1l);
		verifyNoMoreInteractions(userService);
	}

	@Test
	void filmmakerSendsPrivacyRequestTest() throws Exception {
		final Filmmaker mockSenderFilmmaker = new Filmmaker();
		mockSenderFilmmaker.setName("user1");
		mockSenderFilmmaker.setFullname("Filmmaker1");
		mockSenderFilmmaker.setCountry("Spain");
		mockSenderFilmmaker.setCity("Seville");
		mockSenderFilmmaker.setPhone("678543167");
		mockSenderFilmmaker.setConfiguration(new NotificationConfiguration());

		final Filmmaker mockFilmmakerReceiver = new Filmmaker();
		mockFilmmakerReceiver.setId(1L);
		mockFilmmakerReceiver.setName("filmmaker1");
		mockFilmmakerReceiver.setFullname("Filmmaker1");
		mockFilmmakerReceiver.setCountry("Spain");
		mockFilmmakerReceiver.setCity("Seville");
		mockFilmmakerReceiver.setPhone("678543167");
		mockFilmmakerReceiver.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.of(mockSenderFilmmaker));
		when(userService.getUserById(any(Long.class))).thenReturn(Optional.of(mockFilmmakerReceiver));

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/privacyrequest").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/profile/1"));
		});

		verify(userService, only()).getLoggedUser();
	}

	@Test
	void unregisterSendsPrivacyRequestToFilmmakerTest() throws Exception {
		final Filmmaker mockFilmmakerReceiver = new Filmmaker();
		mockFilmmakerReceiver.setId(1L);
		mockFilmmakerReceiver.setName("filmmaker1");
		mockFilmmakerReceiver.setFullname("Filmmaker1");
		mockFilmmakerReceiver.setCountry("Spain");
		mockFilmmakerReceiver.setCity("Seville");
		mockFilmmakerReceiver.setPhone("678543167");
		mockFilmmakerReceiver.setConfiguration(new NotificationConfiguration());

		when(userService.getLoggedUser()).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(post("/profile/1/privacyrequest").with(csrf())).andExpect(status().isFound())
					.andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser();
	}
}