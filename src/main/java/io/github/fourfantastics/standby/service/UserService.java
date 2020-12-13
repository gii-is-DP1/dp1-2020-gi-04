package io.github.fourfantastics.standby.service;

import java.time.Instant;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.UserRepository;
import io.github.fourfantastics.standby.service.exceptions.DataMismatchException;
import io.github.fourfantastics.standby.service.exceptions.NotFoundException;
import io.github.fourfantastics.standby.service.exceptions.NotUniqueException;
import io.github.fourfantastics.standby.utils.Utils;

@Service
public class UserService {
	@Autowired
	UserRepository userRepository;

	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}

	public User saveUser(User user) {
		return userRepository.save(user);
	}
	
	public Optional<User> findByName(String name) {
		 return userRepository.findByName(name);
	}
	
	public User register(User user) throws NotUniqueException {
		Optional<User> foundUser = findByName(user.getName());
		if (foundUser.isPresent()) {
			throw new NotUniqueException("Username already registered!", Utils.hashSet("name"));
		}
		encryptPassword(user);
		user.setCreationDate(Instant.now().getEpochSecond());
		return userRepository.save(user);
	}

	private void encryptPassword(User user) {
		String password = user.getPassword();
		String encryptedPassword = getEncoder().encode(password);
		user.setPassword(encryptedPassword);
	}

	public User authenticate(String name, String password) throws NotFoundException, DataMismatchException {
		Optional<User> foundUser = userRepository.findByName(name);
		if (!foundUser.isPresent()) {
			throw new NotFoundException("Username not found!", Utils.hashSet("name"));
		}

		User user = foundUser.get();
		if (!getEncoder().matches(password, user.getPassword())) {
			throw new DataMismatchException("The entered password doesn't match", Utils.hashSet("password"));
		}

		return user;
	}

	public Optional<User> getLoggedUser(HttpSession session) {
		if (session.getAttribute("userId") == null) {
			return Optional.empty();
		}

		Optional<User> user = userRepository.findById((Long) session.getAttribute("userId"));
		if (!user.isPresent()) {
			logOut(session);
		}
		return user;
	}

	public void logIn(HttpSession session, User user) {
		session.setAttribute("userId", user.getId());
	}

	public void logOut(HttpSession session) {
		session.removeAttribute("userId");
	}

	public PasswordEncoder getEncoder() {
		return new BCryptPasswordEncoder();
	}
}
