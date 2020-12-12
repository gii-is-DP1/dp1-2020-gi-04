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
	
	public User register(User user) {
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
			throw new NotFoundException("Username not found!", Utils.hashSet("username"));
		}
		
		User user = foundUser.get();
		if (!getEncoder().matches(password, user.getPassword())) {
			throw new DataMismatchException("The entered password doesn't match", Utils.hashSet("password"));
		}
		
		return user;
	}
	
	public boolean isLogged(HttpSession session) {
		return session.getAttribute("userId") != null;
	}
	
	public Optional<User> getLoggedUser(HttpSession session) {
		if (!isLogged(session)) {
			return Optional.empty();
		}
		
		return userRepository.findById((Long) session.getAttribute("userId"));
	}
	
	public void logIn(HttpSession session, User user) {
		session.setAttribute("userId", user.getId());
		session.setAttribute("userType", user.getType().toString());
	}
	
	public void logOut(HttpSession session) {
		session.removeAttribute("userId");
		session.removeAttribute("userType");
	}
	
	public PasswordEncoder getEncoder() {
		return new BCryptPasswordEncoder();
	}
}
