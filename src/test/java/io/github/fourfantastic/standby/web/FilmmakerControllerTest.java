package io.github.fourfantastic.standby.web;

import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.configuration.SecurityConfiguration;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.validator.FilmmakerRegisterDataValidator;
import io.github.fourfantastics.standby.service.FilmmakerService;
import io.github.fourfantastics.standby.service.NotificationConfigurationService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.web.FilmmakerController;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = StandbyApplication.class)
@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = FilmmakerController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE), excludeAutoConfiguration = SecurityConfiguration.class)
public class FilmmakerControllerTest {

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

	@WithMockUser(value = "spring")
	@Test
	void testGetRegisterView() throws Exception {
		mockMvc.perform(get("/register/filmmaker")).andExpect(status().isOk())
				.andExpect(model().attributeExists("filmmakerRegisterData"))
				.andExpect(view().name("registerFilmmaker"));
	}
	
	@WithMockUser(value = "spring")
	@Test
	void testRegisterFilmmaker() throws Exception {
		mockMvc.perform(post("/register/filmmaker"));
		when(filmmakerService.getFilmmmakerById(3L).get().getName()).thenReturn("Poppy");
		HttpServletRequest request = null;
		Optional<User> value = null;
		when(userService.getLoggedUser(request.getSession(true))).thenReturn(value);
		
	}
	
	

}
