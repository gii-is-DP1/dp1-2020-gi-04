package io.github.fourfantastic.standby.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.PrivacyRequest;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.UserRepository;
import io.github.fourfantastics.standby.service.AccountService;
import io.github.fourfantastics.standby.service.PrivacyRequestService;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class AccountServiceTest {
	AccountService accountService;

	@Mock
	UserRepository userRepository;

	@BeforeEach
	public void setup() {
		accountService = new AccountService();
	}
	/*
	@Test
	void loadUserByUsernameTest() {
		final Filmmaker filmmaker = new Filmmaker();
		filmmaker.setId(1L);
		filmmaker.setName("filmmaker4");
		filmmaker.setEmail("filmmaker4@gmail.com");
		filmmaker.setPassword("password");
		filmmaker.setFullname("Filmmaker4");
		filmmaker.setCountry("Spain");
		filmmaker.setCity("Seville");
		filmmaker.setPhone("678765167");

		when(userRepository.findByName(filmmaker.getName())).thenReturn(Optional.of(filmmaker));

		assertDoesNotThrow(() -> {
			accountService.loadUserByUsername(filmmaker.getName());
		});

		verify(userRepository, only()).findByName(filmmaker.getName());
	}

	@Test
	void loadUserByUsernameNotFoundTest() {
		final String username = "username";

		when(userRepository.findByName(username)).thenReturn(Optional.empty());

		assertThrows(UsernameNotFoundException.class, () -> accountService.loadUserByUsername(username));

		verify(userRepository, only()).findByName(username);

	}
	*/
}
