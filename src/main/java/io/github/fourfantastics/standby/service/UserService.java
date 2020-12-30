package io.github.fourfantastics.standby.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.FileRepository;
import io.github.fourfantastics.standby.repository.UserRepository;
import io.github.fourfantastics.standby.service.exception.DataMismatchException;
import io.github.fourfantastics.standby.service.exception.InvalidExtensionException;
import io.github.fourfantastics.standby.service.exception.NotFoundException;
import io.github.fourfantastics.standby.service.exception.NotUniqueException;
import io.github.fourfantastics.standby.service.exception.TooBigException;
import io.github.fourfantastics.standby.utils.Utils;

@Service
public class UserService {
	final Path fileRoot = Paths.get("uploads");
	final Set<String> allowedFileExtensions = Utils.hashSet(".bmp", ".png", ".jpg", ".jpeg", ".webp");

	UserRepository userRepository;
	NotificationService notificationService;
	FileRepository fileRepository;

	@Autowired
	public UserService(UserRepository userRepository, NotificationService notificationService,
			FileRepository fileRepository) {
		this.userRepository = userRepository;
		this.notificationService = notificationService;
		this.fileRepository = fileRepository;
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

	public void setProfilePicture(User user, MultipartFile imageFile)
			throws TooBigException, InvalidExtensionException, RuntimeException {
		String extension = fileRepository.getFileExtension(imageFile);
		if (!allowedFileExtensions.contains(extension)) {
			throw new InvalidExtensionException("Invalid extension for the image");
		}

		long gigabyte = 1000L * 1000L * 5L; /* 5MB */
		if (imageFile.getSize() > gigabyte) {
			throw new TooBigException("Uploaded image is too big");
		}

		String filePath = UUID.randomUUID().toString() + extension;
		
		fileRepository.createDirectory(fileRoot);
		if (!fileRepository.saveFile(imageFile, fileRoot.resolve(filePath))) {
			throw new RuntimeException("Couldn't upload image");
		}
		
		user.setPhotoUrl(filePath);
	}
}
