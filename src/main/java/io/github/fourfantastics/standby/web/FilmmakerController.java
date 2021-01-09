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

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.PrivacyRequest;
import io.github.fourfantastics.standby.model.RequestStateType;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.model.form.FilmmakerConfigurationData;
import io.github.fourfantastics.standby.model.form.FilmmakerProfileData;
import io.github.fourfantastics.standby.model.form.FilmmakerRegisterData;
import io.github.fourfantastics.standby.model.form.ShortFilmEditData;
import io.github.fourfantastics.standby.model.validator.FilmmakerConfigurationDataValidator;
import io.github.fourfantastics.standby.model.validator.FilmmakerRegisterDataValidator;
import io.github.fourfantastics.standby.service.FilmmakerService;
import io.github.fourfantastics.standby.service.PrivacyRequestService;
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
	PrivacyRequestService privacyRequestService;

	@Autowired
	ShortFilmService shortFilmService;

	@Autowired
	FilmmakerRegisterDataValidator filmmakerRegisterDataValidator;

	@Autowired
	FilmmakerConfigurationDataValidator filmmakerConfigurationDataValidator;

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

	@GetMapping("/profile/{filmmmakerID}")
	public String getProfileView(HttpSession session, @PathVariable("filmmmakerID") Long filmmmakerID,
			Map<String, Object> model) {
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

		User viewer = userService.getLoggedUser(session).orElse(null);
		if (viewer == null) {
			model.put("hidePrivacyRequestButton", true);
			model.put("hidefollowButton", true);
		} else {
			if (viewer.getType().equals(UserType.Company)) {
				Company viewerCompany = (Company) viewer;
				Optional<PrivacyRequest> sentRequest= viewerCompany.getSentRequests().stream()
						.filter((x -> x.getFilmmaker().getName().equals(filmmaker.getName()))).collect();
				if (sentRequest.isPresent()) {
					model.put("disablePrivacyRequestButton", true);
					if(sentRequest.get().getRequestState() == RequestStateType.ACCEPTED){
						model.put("personalInformation", true);
					}
				}
				if(filmmaker.getFilmmakerSubscribers().stream().anyMatch(x-> x.getName()== viewerCompany.getName())) {
					model.put("unfollowButton", true);
					model.put("hideFollowButton", true);
				}
			} else {
				Filmmaker viewerFilmmaker = (Filmmaker) viewer;
				model.put("hidePrivacyRequestButton", true);
				if (viewerFilmmaker.getName().equals(filmmaker.getName())) {
					model.put("hideFollowButton", true);
				}
				if(filmmaker.getFilmmakerSubscribers().stream().anyMatch(x-> x.getName()== viewerFilmmaker.getName())) {
					model.put("unfollowButton", true);
					model.put("hideFollowButton", true);
				}
			}
		}

		return "filmmakerProfile";
	}

	@PostMapping("/profile/{userID}/subscription")
	public String sucribesToFilmmaker(HttpSession session, @PathVariable("userID") Long userID) {
		User follower = userService.getLoggedUser(session).orElse(null);
		if (follower == null) {
			return "redirect:/login";
		}

		User user = userService.getUserById(userID).orElse(null);

		if (user.getType() != UserType.Filmmaker) {
			return String.format("redirect:/profile/%d", userID);
		}

		Filmmaker followed = (Filmmaker) user;
		
		if(followed.getFilmmakerSubscribers().stream().anyMatch(x-> x.getName()== follower.getName())) {
			return String.format("redirect:/profile/%d", userID);
		}
		if (followed.getName().equals(follower.getName())) {
			return String.format("redirect:/profile/%d", userID);
		}
		
		userService.subscribesTo(follower, followed);
		return String.format("redirect:/profile/%d", userID);

	}
	
	@PostMapping("/profile/{userID}/unsubscription")
	public String unsucribesToFilmmaker(HttpSession session, @PathVariable("userID") Long userID) {
		User follower = userService.getLoggedUser(session).orElse(null);
		if (follower == null) {
			return "redirect:/login";
		}

		User user = userService.getUserById(userID).orElse(null);

		if (user.getType() != UserType.Filmmaker) {
			return String.format("redirect:/profile/%d", userID);
		}

		Filmmaker followed = (Filmmaker) user;
		
		if(!followed.getFilmmakerSubscribers().stream().anyMatch(x-> x.getName()== follower.getName())) {
			return String.format("redirect:/profile/%d", userID);
		}
		if (followed.getName().equals(follower.getName())) {
			return String.format("redirect:/profile/%d", userID);
		}

		userService.unsubscribesTo(follower, followed);
		return String.format("redirect:/profile/%d", userID);

	}
	@PostMapping("/profile/{filmmakerID}/privacyrequest")
	public String sendPrivacyRequest(HttpSession session, @PathVariable("filmmakerID") Long userID) {
		User sender = userService.getLoggedUser(session).orElse(null);
		if (sender == null) {
			return "redirect:/login";
		}
		if (sender.getType() != UserType.Company) {
			return String.format("redirect:/profile/%d", userID);
		}

		User receiver = userService.getUserById(userID).orElse(null);

		if (receiver.getType() != UserType.Filmmaker) {
			return String.format("redirect:/profile/%d", userID);
		}
		Company company = (Company) sender;
		if (company.getSentRequests().stream().anyMatch(x -> x.getFilmmaker().getName().equals(receiver.getName()))) {
			return String.format("redirect:/profile/%d", userID);
		}
		Filmmaker filmmaker = (Filmmaker) receiver;
		privacyRequestService.sendPrivacyRequest(company, filmmaker);
		return String.format("redirect:/profile/%d", userID);
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
		model.put("success", "Configuration has been saved successfully!");
		return "manageFilmmakerAccount";
	}
}
