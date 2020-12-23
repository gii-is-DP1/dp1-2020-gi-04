package io.github.fourfantastic.standby.service;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.repository.UserRepository;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.DataMismatchException;
import io.github.fourfantastics.standby.service.exception.NotFoundException;
import io.github.fourfantastics.standby.service.exception.NotUniqueException;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class UserServiceTest {
	UserService userService;

	@Mock
	UserRepository userRepository;
	
	@BeforeEach
	public void setup() {
		userService = new UserService(userRepository);
		
		when(userRepository.save(any(User.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
	}
	
	@Test
	void registerUserTest() {	
		final String name = "Táctico";
		final String rawPassword = "weak password";
		
		when(userRepository.findByName(name)).thenReturn(Optional.empty());
		
		User user = new User();
		user.setName(name);
		user.setEmail("Davinci@gmail.com");
		user.setPassword(rawPassword);
		user.setType(UserType.Filmmaker);
		assertDoesNotThrow(() -> {
			User registeredUser = userService.register(user);
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
		
		when(userRepository.findByName(name)).thenReturn(Optional.of(new User()));
		
		User user = new User();
		user.setName(name);
		user.setEmail("Davinci@gmail.com");
		user.setPassword(rawPassword);
		user.setType(UserType.Filmmaker);
		assertThrows(NotUniqueException.class, () -> {
			userService.register(user);
		});
	}

	@Test
	void authenticateTest() {
		final String name = "filmmaker1";
		final String password = "password";
		
		User mockUser = new User();
		mockUser.setName(name);
		mockUser.setPassword(userService.getEncoder().encode(password));
		when(userRepository.findByName("filmmaker1")).thenReturn(Optional.of(mockUser));
		
		assertDoesNotThrow(() -> {
			User user = userService.authenticate(name, password);
			assertThat(user).isEqualTo(mockUser);
		});
	}
	
	@Test
	void authenticateNotFoundTest() {
		final String name = "filmmaker";
		final String password = "password";
		
		User mockUser = new User();
		mockUser.setName(name);
		mockUser.setPassword(userService.getEncoder().encode(password));
		when(userRepository.findByName("filmmaker1")).thenReturn(Optional.of(mockUser));
		
		assertThrows(NotFoundException.class, () -> {
			userService.authenticate(name, password);
		});
	}
	
	@Test
	void authenticateDataMismatchTest() {
		final String name = "filmmaker1";
		final String password = "password";
		
		User mockUser = new User();
		mockUser.setName(name);
		mockUser.setPassword(password);
		when(userRepository.findByName("filmmaker1")).thenReturn(Optional.of(mockUser));
		
		assertThrows(DataMismatchException.class, () -> {
			userService.authenticate(name, password);
		});
	}
	
	@Test
	void getLoggedUserTest() {
		HttpSession session = mock(HttpSession.class);
		
		final Long id = 1L;
		final String name = "filmmaker1";
		
		User mockUser = new User();
		mockUser.setId(id);
		mockUser.setName(name);
		
		when(session.getAttribute("userId")).thenReturn(id);
		when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));
		
		assertDoesNotThrow(() -> {
			Optional<User> optionalUser = userService.getLoggedUser(session);
			assertTrue(optionalUser.isPresent());
			User user = optionalUser.get();
			assertThat(user).isEqualTo(mockUser);
			
			verify(session, times(2)).getAttribute("userId");
			verifyNoMoreInteractions(session);
			verify(userRepository, only()).findById(id);
		});
	}
	
	@Test
	void getLoggedUserEmptyTest() {
		HttpSession session = mock(HttpSession.class);
		
		when(session.getAttribute("userId")).thenReturn(null);
		
		assertDoesNotThrow(() -> {
			Optional<User> optionalUser = userService.getLoggedUser(session);
			assertFalse(optionalUser.isPresent());
		});
		
		verify(session, only()).getAttribute("userId");
		verifyNoInteractions(userRepository);
	}
	
	@Test
	void getLoggedUserInvalidSessionTest() {
		HttpSession session = mock(HttpSession.class);
		
		final Long id = 1L;
		
		when(session.getAttribute("userId")).thenReturn(id);
		when(userRepository.findById(id)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			Optional<User> optionalUser = userService.getLoggedUser(session);
			assertFalse(optionalUser.isPresent());
		});
		
		verify(session, times(2)).getAttribute("userId");
		verify(userRepository, only()).findById(id);
		verifyNoMoreInteractions(userRepository);
		verify(session, times(1)).removeAttribute("userId");
		verifyNoMoreInteractions(session);
	}
}
