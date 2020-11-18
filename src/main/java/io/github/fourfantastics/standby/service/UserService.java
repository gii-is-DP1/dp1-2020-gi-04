package io.github.fourfantastics.standby.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	UserRepository userRepository;
	
	public Optional<User> getAccountById(Long id) {
		return userRepository.findById(id);
	}
	
	public Set<User> getAllShortFilms() {
		Set<User> users = new HashSet<>();
		Iterator<User> iterator = userRepository.findAll().iterator();
		while (iterator.hasNext()) {
			users.add(iterator.next());
		}
		return users;
	}
	
	public void saveAccount(User account) {
		userRepository.save(account);
	}
}
