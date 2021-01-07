package io.github.fourfantastics.standby.web;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.model.form.FilmmakerConfigurationData;
import io.github.fourfantastics.standby.model.form.FilmmakerProfileData;
import io.github.fourfantastics.standby.model.form.FilmmakerRegisterData;
import io.github.fourfantastics.standby.model.validator.FilmmakerConfigurationDataValidator;
import io.github.fourfantastics.standby.model.validator.FilmmakerRegisterDataValidator;
import io.github.fourfantastics.standby.service.FilmmakerService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.NotUniqueException;

@Controller
public class FilmmakerController {
	@Autowired
	UserService userService;

	@Autowired
	FilmmakerService filmmakerService;

	@Autowired
	FilmmakerRegisterDataValidator filmmakerRegisterDataValidator;

	@Autowired
	FilmmakerConfigurationDataValidator filmmakerConfigurationDataValidator;

	@Autowired
	ShortFilmService shortFilmService;

	@GetMapping("/register/filmmaker")
	public String getRegisterView(HttpSession session, Map<String, Object> model) {
		if (userService.getLoggedUser(session).isPresent()) {
			return "redirect:/";
		}
		model.put("filmmakerRegisterData", new FilmmakerRegisterData());
		return "registerFilmmaker";
	}

	@PostMapping("/register/filmmaker")
	public String registerFilmmaker(HttpSession session,
			@ModelAttribute("filmmakerRegisterData") FilmmakerRegisterData filmmakerRegisterData, BindingResult result,
			Map<String, Object> model) {
		if (userService.getLoggedUser(session).isPresent()) {
			return "redirect:/";
		}

		filmmakerRegisterDataValidator.validate(filmmakerRegisterData, result);
		if (result.hasErrors()) {
			return "registerFilmmaker";
		}

		try {
			Filmmaker filmmaker = filmmakerService.registerFilmmaker(filmmakerRegisterData);
			userService.logIn(session, filmmaker);
		} catch (NotUniqueException e) {
			result.rejectValue("name", "", e.getMessage());
			return "registerFilmmaker";
		}
		return "redirect:/";
	}

	@GetMapping("/profile/filmmaker/{filmmmakerID}")
	public String getProfileView(@PathVariable("filmmmakerID") Long filmmmakerID, Map<String, Object> model) {
		User user = userService.getUserById(filmmmakerID).orElse(null);
		if (user == null) {
			return "redirect:/";
		}

		if (user.getType() != UserType.Filmmaker) {
			return "redirect:/profile/{filmmakerID}";
		}

		Filmmaker filmmaker = (Filmmaker) user;
		FilmmakerProfileData filmmakerProfileData = FilmmakerProfileData.fromFilmmaker(filmmaker);
		filmmakerProfileData.setAttachedShortFilms(shortFilmService.getShortFilmByFilmmaker(filmmaker));

		model.put("filmmakerProfileData", filmmakerProfileData);

		return "filmmakerProfile";
	}
	
	@PostMapping("/subcribesTo/{userID}")
	public String sucribesToFilmmaker(HttpSession session, @PathVariable("userID") Long userID) {
		User follower = userService.getLoggedUser(session).orElse(null);
		if (follower == null) {
			return "redirect:/login";
		}

		Optional<User> optionalUser = userService.getUserById(userID);
		if (!optionalUser.isPresent()) {
			System.out.println("A quien quieres seguir que no existe por favoh");
			return "redirect:/";
		}
		User user = optionalUser.get();
		if (user.getType() != UserType.Filmmaker) {
			System.out.println("No puedes seguir a alguien que no sea un filmmmaker");
			return String.format("redirect:/profile/%d", userID);
		}

		Filmmaker followed = (Filmmaker) user;

		if (followed.getName().equals(follower.getName())) {
			System.out.println("No puedes seguirte a ti mismo egocéntrico");
			return "filmmakerProfile";
		}

		userService.subcribesTo(follower, followed);
		return "filmmakerProfile";

	}

	@GetMapping("/account/filmmaker")
	public String getManageAccount(HttpSession session, Map<String, Object> model) {
		User user = userService.getLoggedUser(session).orElse(null);
		if (user == null) {
			return "redirect:/login";
		}

		if (user.getType() != UserType.Filmmaker) {
			return "redirect:/account";
		}

		Filmmaker filmmaker = (Filmmaker) user;
		model.put("filmmakerConfigurationData", FilmmakerConfigurationData.fromFilmmaker(filmmaker));
		model.put("photoUrl", user.getPhotoUrl());
		return "manageFilmmakerAccount";
	}

	@PostMapping("/account/filmmaker")
	public String doManageAccount(HttpSession session,
			@ModelAttribute("filmmakerConfigurationData") FilmmakerConfigurationData filmmakerConfigurationData,
			BindingResult result, Map<String, Object> model) {
		User user = userService.getLoggedUser(session).orElse(null);
		if (user == null) {
			return "redirect:/login";
		}

		if (user.getType() != UserType.Filmmaker) {
			return "redirect:/account";
		}

		filmmakerConfigurationDataValidator.validate(filmmakerConfigurationData, result);
		if (result.hasErrors()) {
			model.put("photoUrl", user.getPhotoUrl());
			return "manageFilmmakerAccount";
		}

		Filmmaker userFilmmaker = (Filmmaker) user;
		filmmakerConfigurationData.copyToFilmmaker(userFilmmaker);
		if (filmmakerConfigurationData.getNewPhoto() != null && !filmmakerConfigurationData.getNewPhoto().isEmpty()) {
			try {
				userService.setProfilePicture(userFilmmaker, filmmakerConfigurationData.getNewPhoto());
			} catch (Exception e) {
				result.reject("", e.getMessage());
				model.put("photoUrl", user.getPhotoUrl());
				return "manageFilmmakerAccount";
			}
		}
		userFilmmaker = (Filmmaker) userService.saveUser(userFilmmaker);

		model.put("filmmakerData", filmmakerConfigurationData);
		model.put("photoUrl", user.getPhotoUrl());
		return "manageFilmmakerAccount";
	}
}
