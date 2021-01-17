package io.github.fourfantastics.standby.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.NotificationType;
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
	final Set<String> allowedImageFileExtensions = Utils.hashSet(".bmp", ".png", ".jpg", ".jpeg", ".webp");

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

	public User register(User user) throws NotUniqueException {
		Optional<User> foundUser = getUserByName(user.getName());
		if (foundUser.isPresent()) {
			throw new NotUniqueException("Username already registered!", Utils.hashSet("name"));
		}
		encryptPassword(user);
		user.setCreationDate(new Date().getTime());
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
		session.setAttribute("userType", user.getType());
	}

	public void logOut(HttpSession session) {
		session.removeAttribute("userId");
		session.removeAttribute("userType");
	}

	public PasswordEncoder getEncoder() {
		return new BCryptPasswordEncoder();
	}

	public void subscribesTo(User follower, Filmmaker followed) {
		follower.getFilmmakersSubscribedTo().add(followed);
		if (followed.getConfiguration().getBySubscriptions()) {
			notificationService.sendNotification(followed, NotificationType.SUBSCRIPTION,
					String.format("%s has subscribed to your profile.", follower.getName()));
			
		}
		userRepository.save(follower);
	}

	public void unsubscribesTo(User follower, Filmmaker followed) {
		followed.getFilmmakerSubscribers().remove(follower);
		List<Notification> notification = followed.getNotifications().stream()
				.filter(x -> x.getText().equals(follower.getName() + " has subscribed to your profile."))
				.collect(Collectors.toList());
		if (!notification.isEmpty()) {
			followed.getNotifications().remove(notification.get(0));
			notificationService.deleteNotification(notification.get(0));
		}
		userRepository.save(follower);
	}

	public void setProfilePicture(User user, MultipartFile imageFile)
			throws TooBigException, InvalidExtensionException, RuntimeException {
		String extension = fileRepository.getFileExtension(imageFile);
		if (!allowedImageFileExtensions.contains(extension)) {
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
		userRepository.save(user);
	}
}
