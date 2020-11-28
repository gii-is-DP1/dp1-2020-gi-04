package io.github.fourfantastics.standby.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.github.fourfantastics.standby.service.UserService;

@SpringBootTest
public class PasswordUnitTest {

	@Autowired
	UserService userService;

	@Test
	void encryptionPasswordEquality() {
		PasswordEncoder encoder = userService.getEncoder();
		String password = "Prueba1";
		String encodedPassword = encoder.encode(password);
		Boolean isPasswordEqual = encoder.matches(password, encodedPassword);
		assert(isPasswordEqual);
	}
	
	@Test
	void encryptionPasswordEqualityWithDifferentEncoder() {
		PasswordEncoder encoder = userService.getEncoder();
		String password = "Prueba1";
		String encodedPassword = encoder.encode(password);
		PasswordEncoder differentEncoder = userService.getEncoder();
		Boolean isPasswordEqual = differentEncoder.matches(password, encodedPassword);
		assert(isPasswordEqual);
	}
	
}
