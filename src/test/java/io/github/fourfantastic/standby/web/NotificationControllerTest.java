package io.github.fourfantastic.standby.web;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.NotificationType;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.form.NotificationData;
import io.github.fourfantastics.standby.model.form.NotificationWrapper;
import io.github.fourfantastics.standby.model.form.Pagination;
import io.github.fourfantastics.standby.service.NotificationService;
import io.github.fourfantastics.standby.service.UserService;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
@AutoConfigureMockMvc
public class NotificationControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	UserService userService;

	@MockBean
	NotificationService notificationService;

	@MockBean
	Page<Notification> mockPage;
	
	@Test
	public void getNotificationCountTest() {
		final User mockUser = new User();
		final Integer unreadNotifications = 5;

		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(mockUser));
		when(notificationService.getUnreadNotifications(mockUser)).thenReturn(unreadNotifications);

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/notifications/count"))
			.andExpect(status().isOk())
			.andExpect(content().json(String.format("{status: %d, count: %d}", HttpStatus.OK.value(), unreadNotifications)));
		});

		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verify(notificationService, only()).getUnreadNotifications(mockUser);
	}
	
	@Test
	public void getNotificationCountNotLoggedTest() {
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/notifications/count"))
			.andExpect(status().isOk())
			.andExpect(content().json(String.format("{status: %d, url: %s}", HttpStatus.FOUND.value(), "'/login'")));
		});

		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(notificationService);
	}

	@Test
	public void getNotificationViewTest() {
		final User mockUser = new User();
		final List<Notification> mockNotifications = new ArrayList<Notification>();
		mockNotifications.add(new Notification(1L, "Notification example 1", 3L, null, NotificationType.COMMENT, mockUser));
		mockNotifications.add(new Notification(2L, "Notification example 2", 2L, null, NotificationType.PRIVACY_REQUEST, mockUser));
		mockNotifications.add(new Notification(3L, "Notification example 3", 1L, null, NotificationType.RATING, mockUser));
		final Pagination mockPagination = Pagination.of(mockNotifications.size());
		final PageRequest mockPageRequest = mockPagination.getPageRequest(Sort.by("emissionDate").descending());
		final NotificationData mockNotificationData = new NotificationData();
		mockNotificationData.setNotifications(mockNotifications.stream()
									.sorted((x, y) -> (y.getEmissionDate().intValue() - x.getEmissionDate().intValue()))
									.map(NotificationWrapper::of).collect(Collectors.toList()));
		mockNotificationData.setPagination(mockPagination);
		
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.of(mockUser));
		when(notificationService.countNotifications(mockUser)).thenReturn(mockNotifications.size());
		when(notificationService.getPaginatedNotifications(mockUser, mockPageRequest)).thenReturn(mockPage);
		when(mockPage.getContent()).thenReturn(mockNotifications);
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/notifications")).andExpect(status().isOk())
			.andExpect(view().name("userNotifications"))
			.andExpect(model().attribute("notificationData", mockNotificationData));
		});

		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verify(notificationService, times(1)).countNotifications(mockUser);
		verify(notificationService, times(1)).getPaginatedNotifications(mockUser, mockPageRequest);
		verify(notificationService, times(1)).readNotifications(mockNotifications);
		verifyNoMoreInteractions(notificationService);
	}
	
	@Test
	public void getNotificationViewNotLoggedTest() {
		when(userService.getLoggedUser(any(HttpSession.class))).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			mockMvc.perform(get("/notifications"))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("/login"));
		});

		verify(userService, only()).getLoggedUser(any(HttpSession.class));
		verifyNoInteractions(notificationService);
	}
}
