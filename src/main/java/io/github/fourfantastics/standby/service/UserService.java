package io.github.fourfantastics.standby.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	UserRepository userRepository;

	public Optional<User> getUser(Long id) {
		return userRepository.findById(id);
	}

	public void encryptPassword(User user) {
		String password = user.getPassword();
		String encryptedPassword = getEncoder().encode(password);
		user.setPassword(encryptedPassword);

	}
	
	public PasswordEncoder getEncoder() {
		return new BCryptPasswordEncoder();
	}
}
