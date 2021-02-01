package io.github.fourfantastic.standby.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.model.NotificationType;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.repository.FileRepository;
import io.github.fourfantastics.standby.repository.UserRepository;
import io.github.fourfantastics.standby.service.NotificationService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.NotUniqueException;

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
		userService = new UserService(userRepository, notificationService, fileRepository, shortFilmService);

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
		});
	}

	/*
	 * @Test void authenticateTest() { final String name = "filmmaker1"; final
	 * String password = "password"; final User mockUser = new User();
	 * mockUser.setName(name);
	 * mockUser.setPassword(userService.getEncoder().encode(password));
	 * 
	 * when(userRepository.findByName("filmmaker1")).thenReturn(Optional.of(mockUser
	 * ));
	 * 
	 * assertDoesNotThrow(() -> { User user = userService.authenticate(name,
	 * password); assertThat(user).isEqualTo(mockUser); }); }
	 * 
	 * @Test void authenticateNotFoundTest() { final String name = "filmmaker";
	 * final String password = "password"; final User mockUser = new User();
	 * mockUser.setName(name);
	 * mockUser.setPassword(userService.getEncoder().encode(password));
	 * 
	 * when(userRepository.findByName("filmmaker1")).thenReturn(Optional.of(mockUser
	 * ));
	 * 
	 * assertThrows(NotFoundException.class, () -> { userService.authenticate(name,
	 * password); }); }
	 * 
	 * @Test void authenticateDataMismatchTest() { final String name = "filmmaker1";
	 * final String password = "password"; final User mockUser = new User();
	 * mockUser.setName(name); mockUser.setPassword(password);
	 * 
	 * when(userRepository.findByName("filmmaker1")).thenReturn(Optional.of(mockUser
	 * ));
	 * 
	 * assertThrows(DataMismatchException.class, () -> {
	 * userService.authenticate(name, password); }); }
	 * 
	 * @Test void getLoggedUserTest() {
	 * 
	 * final Long id = 1L; final String name = "filmmaker1";
	 * 
	 * User mockUser = new User(); mockUser.setId(id); mockUser.setName(name);
	 * 
	 * when(session.getAttribute("userId")).thenReturn(id);
	 * when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));
	 * 
	 * assertDoesNotThrow(() -> { Optional<User> optionalUser =
	 * userService.getLoggedUser(); assertTrue(optionalUser.isPresent()); User user
	 * = optionalUser.get(); assertThat(user).isEqualTo(mockUser);
	 * 
	 * verify(session, times(2)).getAttribute("userId");
	 * verifyNoMoreInteractions(session); verify(userRepository,
	 * only()).findById(id); }); }
	 * 
	 * @Test void getLoggedUserEmptyTest() { final HttpSession session =
	 * mock(HttpSession.class);
	 * 
	 * when(session.getAttribute("userId")).thenReturn(null);
	 * 
	 * assertDoesNotThrow(() -> { Optional<User> optionalUser =
	 * userService.getLoggedUser(session); assertFalse(optionalUser.isPresent());
	 * });
	 * 
	 * verify(session, only()).getAttribute("userId");
	 * verifyNoInteractions(userRepository); }
	 * 
	 * @Test void getLoggedUserInvalidSessionTest() { final HttpSession session =
	 * mock(HttpSession.class); final Long id = 1L;
	 * 
	 * when(session.getAttribute("userId")).thenReturn(id);
	 * when(userRepository.findById(id)).thenReturn(Optional.empty());
	 * 
	 * assertDoesNotThrow(() -> { Optional<User> optionalUser =
	 * userService.getLoggedUser(session); assertFalse(optionalUser.isPresent());
	 * });
	 * 
	 * verify(session, times(2)).getAttribute("userId"); verify(userRepository,
	 * only()).findById(id); verifyNoMoreInteractions(userRepository);
	 * verify(session, times(1)).removeAttribute("userId"); verify(session,
	 * times(1)).removeAttribute("userType"); verifyNoMoreInteractions(session); }
	 */
	@Test
	void subscribesWithNotificationToTest() {
		final User mockUserFollower = new User();
		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		final NotificationConfiguration notificationConfiguration = new NotificationConfiguration();
		notificationConfiguration.setBySubscriptions(true);
		mockFilmmakerFollowed.setConfiguration(notificationConfiguration);

		userService.subscribesTo(mockUserFollower, mockFilmmakerFollowed);

		verify(notificationService, only()).sendNotification(eq(mockFilmmakerFollowed),
				eq(NotificationType.SUBSCRIPTION), any(String.class));
		verify(userRepository, only()).save(mockUserFollower);
	}

	@Test
	void subscribesWithoutNotificationToTest() {
		final User mockUserFollower = new User();
		final Filmmaker mockFilmmakerFollowed = new Filmmaker();
		final NotificationConfiguration notificationConfiguration = new NotificationConfiguration();
		notificationConfiguration.setBySubscriptions(false);
		mockFilmmakerFollowed.setConfiguration(notificationConfiguration);

		userService.subscribesTo(mockUserFollower, mockFilmmakerFollowed);

		verifyNoInteractions(notificationService);
		verify(userRepository, only()).save(mockUserFollower);
	}

	@Test
	void unsubscribesWithNotificationEliminationToTest() {
		final User mockUserFollower = new User();
		mockUserFollower.setName("Filmmaker1");
		final Filmmaker mockFilmmakerFollowed = new Filmmaker();

		mockUserFollower.getFilmmakersSubscribedTo().add(mockFilmmakerFollowed);
		mockFilmmakerFollowed.getFilmmakerSubscribers().add(mockUserFollower);

		Notification notification = new Notification();
		notification.setText("Filmmaker1 has subscribed to your profile.");
		mockFilmmakerFollowed.getNotifications().add(notification);

		userService.unsubscribesTo(mockUserFollower, mockFilmmakerFollowed);

		verify(notificationService, only()).deleteNotification(notification);
		verify(userRepository, only()).save(mockUserFollower);
	}

	@Test
	void unsubscribesWithoutNotificationEliminationToTest() {
		final User mockUserFollower = new User();
		final Filmmaker mockFilmmakerFollowed = new Filmmaker();

		mockUserFollower.getFilmmakersSubscribedTo().add(mockFilmmakerFollowed);
		mockFilmmakerFollowed.getFilmmakerSubscribers().add(mockUserFollower);

		userService.unsubscribesTo(mockUserFollower, mockFilmmakerFollowed);

		verify(userRepository, only()).save(mockUserFollower);
		verifyNoInteractions(notificationService);
	}
}
