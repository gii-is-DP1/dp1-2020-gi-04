package io.github.fourfantastic.standby.service;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exceptions.DataMismatchException;
import io.github.fourfantastics.standby.service.exceptions.NotFoundException;
import io.github.fourfantastics.standby.service.exceptions.NotUniqueException;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = StandbyApplication.class)
public class UserServiceTest {

	@Autowired
	UserService userService;

	@Test
	void findUserbyIdTest() {
		Optional<User> user = userService.getUserById(1L);
		assert (user != null);
		Optional<User> user1 = userService.getUserById(777L);
		assert (user1.isPresent() == false);
	}

	@Test
	void findUserByNameTest() {
		Optional<User> user = userService.findByName("filmmaker1");
		assert (user.isPresent() == true);
		Optional<User> user1 = userService.findByName("InventedName");
		assert (user1.isPresent() == false);
	}

	@Test
	void registerUserTest() throws NotUniqueException {
		User prueba = new User();
		Boolean exception = false;
		prueba.setName("T�ctico");
		prueba.setCreationDate(2L);
		prueba.setEmail("Davinci@gmail.com");
		prueba.setPassword("weak password");
		prueba.setType(UserType.Filmmaker);
		userService.register(prueba);
		assert (userService.findByName("T�ctico").isPresent());
		try {
			userService.register(prueba);
		} catch (NotUniqueException e) {
			exception = true;
		}

		assert (exception);
	}

	@Test
	void authenticateTest() {
		Boolean exception = false;
		Optional<User> user = userService.findByName("filmmaker1");
		try {
			userService.authenticate("filmmaker1", "password");
		} catch (NotFoundException e) {
			exception = true;
		} catch (DataMismatchException e) {
			exception = true;
		}
		assert (!exception);
		// DataMismatchException
		try {
			userService.authenticate("filmmaker1", "InventedPassword");
		} catch (NotFoundException e) {
			exception = true;
		} catch (DataMismatchException e) {
			exception = true;
		}
		assert (exception);
		exception = false;
		// NotFoundException
		try {
			userService.authenticate("InvenetedUserName", "password");
		} catch (NotFoundException e) {
			exception = true;
		} catch (DataMismatchException e) {
			exception = true;
		}
		assert (exception);
	}

}
