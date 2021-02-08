package io.github.fourfantastic.standby.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.UserRepository;
import io.github.fourfantastics.standby.service.AccountService;
import io.github.fourfantastics.standby.service.exception.NotUniqueException;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class AccountServiceTest {
	AccountService accountService;

	@Mock
	UserRepository userRepository;   


	@BeforeEach
	public void setup() throws NotUniqueException {
		accountService = new AccountService(userRepository);
	}
	
	@Test
	void loadUserByUsernameTest() {
		final User mockUser = new User();
		mockUser.setName("filmmaker4");
		mockUser.setEmail("filmmaker4@gmail.com");
		mockUser.setCreationDate(8L);
		mockUser.setPassword("password");
		
		when(userRepository.findByName(mockUser.getName())).thenReturn(Optional.of(mockUser));
		
		assertDoesNotThrow(() -> {
			accountService.loadUserByUsername(mockUser.getName());
		});

		verify(userRepository, only()).findByName(mockUser.getName());
	}

	@Test
	void loadUserByUsernameNotFoundTest() {
		final String username = "username";
		
		when(userRepository.findByName(username)).thenReturn(Optional.empty());
		
		assertThrows(UsernameNotFoundException.class, () -> accountService.loadUserByUsername(username));

		verify(userRepository, only()).findByName(username);

	}
	
}
