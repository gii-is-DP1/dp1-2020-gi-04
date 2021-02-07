package io.github.fourfantastics.standby.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Account;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.UserRepository;

@Service
public class AccountService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> foundUser = userRepository.findByName(username);
		if (!foundUser.isPresent()) {
			throw new UsernameNotFoundException("Username not found!");
		}
		return new Account(foundUser.get());
	}
}
