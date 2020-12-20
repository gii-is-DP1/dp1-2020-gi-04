package io.github.fourfantastic.standby.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.configuration.SecurityConfiguration;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.form.FilmmakerRegisterData;
import io.github.fourfantastics.standby.model.validator.FilmmakerRegisterDataValidator;
import io.github.fourfantastics.standby.service.FilmmakerService;
import io.github.fourfantastics.standby.service.NotificationConfigurationService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exceptions.DataMismatchException;
import io.github.fourfantastics.standby.service.exceptions.NotUniqueException;
import io.github.fourfantastics.standby.web.FilmmakerController;


@ContextConfiguration(classes=StandbyApplication.class)
@WebMvcTest(controllers=FilmmakerController.class,
excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class),
excludeAutoConfiguration= SecurityConfiguration.class)
public class FilmmakerControllerTest {
	
	@SuppressWarnings("unused")
	@Autowired
	private FilmmakerController filmmakerController;
	
	@MockBean
	private FilmmakerService filmmakerService;
	
	@MockBean
	private UserService userService;
	
	@MockBean
	private NotificationConfigurationService notificationConfigurationService;
	
	
	@MockBean
	private FilmmakerRegisterDataValidator filmmakerRegisterDataValidator;
	
	
	@Autowired
	private MockMvc mockMvc;
	
	@BeforeEach
	void setup() throws DataMismatchException, NotUniqueException {
		
		FilmmakerRegisterData filmmakerRegisterData = new FilmmakerRegisterData();
		filmmakerRegisterData.setName("Filmmaker1");
		filmmakerRegisterData.setFullname("Filmmaker1 Surname");
		filmmakerRegisterData.setCity("Seville");
		filmmakerRegisterData.setCountry("Spain");
		filmmakerRegisterData.setEmail("filmmaker1@gmail.com");
		filmmakerRegisterData.setPhone("678543167");
		filmmakerRegisterData.setPassword("patata");
		filmmakerRegisterData.setConfirmPassword("patata");
		
		Filmmaker filmmaker = this.filmmakerService.registerFilmmaker(filmmakerRegisterData );
		
		given(this.filmmakerService.registerFilmmaker(filmmakerRegisterData)).willReturn(filmmaker);

	}
	
	
	
	
	
	@WithMockUser(value = "spring")
	@Test
	void testRegisterFilmmaker() throws Exception {
		mockMvc.perform(post("/register/filmmaker")
		.param("type", "Filmmaker")
		.param("name", "Filmmaker4")
		.param("email", "filmmaker4@gmail.com")
		.param("password", "patataa")
		.param("creationDate", "12/12/2020")
		.param("photoUrl", "url photo")
		.with(csrf())
		.param("fullname", "Filmmaker4 Surname")
		.param("country", "Spain")
		.param("city", "Seville")
		.param("phone", "678908432"))
		.andExpect(status().is3xxRedirection());
	}
	
	@WithMockUser(value = "spring")
    @Test
    void testGetRegisterView() throws Exception {
		mockMvc.perform(get("/register/filmmaker")).andExpect(status().isOk())
		.andExpect(model().attributeExists("filmmakerRegisterData")).andExpect(view().name("registerFilmmaker"));
	}
	
	
	
	@WithMockUser(value = "spring")
	@Test
	void testRegisterFilmmakerHasErrors() throws Exception,DataMismatchException,NotUniqueException {
		mockMvc.perform(post("/register/filmmaker")
		.with(csrf())
		.param("type", "Filmmaker")
		.param("name", "Filmmaker4")
		.param("password", "patataa")
		.param("creationDate", "12/12/2020")
		.param("photoUrl", "url photo")
		.param("fullname", "Filmmaker4 Surname")
		.param("country", "Spain")
		.param("city", "Seville")
		.param("phone", "678908432"))
		.andExpect(status().isFound())
		.andExpect(model().attributeDoesNotExist("email"));
		
		
		Optional<Filmmaker> filmmaker= filmmakerService.getFilmmmakerByName("Filmmaker4");
		assertThat(filmmaker.isPresent()).isEqualTo(true);
		
		
	}
	
	
	

}
