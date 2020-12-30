package io.github.fourfantastics.standby.service;

import java.time.Instant;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.UserRepository;
import io.github.fourfantastics.standby.service.exception.DataMismatchException;
import io.github.fourfantastics.standby.service.exception.NotFoundException;
import io.github.fourfantastics.standby.service.exception.NotUniqueException;
import io.github.fourfantastics.standby.utils.Utils;

@Service
public class UserService {
	UserRepository userRepository;
	NotificationService notificationService;

	@Autowired
	public UserService(UserRepository userRepository, NotificationService notificationService) {
		this.userRepository = userRepository;
		this.notificationService = notificationService;
	}

	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}

	public Optional<User> getUserByName(String name) {
		return userRepository.findByName(name);
	}

	public User saveUser(User user) {
		return userRepository.save(user);
	}

	public User register(User user) throws NotUniqueException {
		Optional<User> foundUser = getUserByName(user.getName());
		if (foundUser.isPresent()) {
			throw new NotUniqueException("Username already registered!", Utils.hashSet("name"));
		}
		encryptPassword(user);
		user.setCreationDate(Instant.now().getEpochSecond());
		return userRepository.save(user);
	}

	private void encryptPassword(User user) {
		user.setPassword(getEncoder().encode(user.getPassword()));
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

	public void subcribesTo(User follower, Filmmaker followed) {
		follower.getFilmmakersSubscribedTo().add(followed);
		followed.getFilmmakerSubscribers().add(follower);
		if (followed.getConfiguration().getBySubscriptions()) {
			Notification newFollowerNotification = new Notification();
			newFollowerNotification.setEmisionDate(Instant.now().getEpochSecond());
			newFollowerNotification.setText(follower.getName() + " has subscribed to your profile.");
			newFollowerNotification.setUser(followed);
			followed.getNotifications().add(newFollowerNotification);

			notificationService.saveNotification(newFollowerNotification);
		}
		userRepository.save(follower);
		userRepository.save(followed);
	}

}
