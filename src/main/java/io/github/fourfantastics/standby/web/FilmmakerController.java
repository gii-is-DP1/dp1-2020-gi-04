package io.github.fourfantastics.standby.web;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.PrivacyRequest;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.model.form.FilmmakerConfigurationData;
import io.github.fourfantastics.standby.model.form.FilmmakerProfileData;
import io.github.fourfantastics.standby.model.form.FilmmakerRegisterData;
import io.github.fourfantastics.standby.model.form.UserFavouriteShortFilmsData;
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
	FilmmakerService filmmakerService;

	@Autowired
	UserService userService;

	@Autowired
	PrivacyRequestService privacyRequestService;

	@Autowired
	ShortFilmService shortFilmService;

	@Autowired
	FilmmakerRegisterDataValidator filmmakerRegisterDataValidator;

	@Autowired
	FilmmakerConfigurationDataValidator filmmakerConfigurationDataValidator;

	@GetMapping("/register/filmmaker")
	public String getRegisterView(@ModelAttribute FilmmakerRegisterData filmmakerRegisterData,
			Map<String, Object> model) {
		if (userService.getLoggedUser().isPresent()) {
			return "redirect:/";
		}
		model.put("filmmakerRegisterData", new FilmmakerRegisterData());

		return "registerFilmmaker";
	}

	@PostMapping("/register/filmmaker")
	public String registerFilmmaker(@ModelAttribute FilmmakerRegisterData filmmakerRegisterData, BindingResult result) {
		if (userService.getLoggedUser().isPresent()) {
			return "redirect:/";
		}

		filmmakerRegisterDataValidator.validate(filmmakerRegisterData, result);
		if (result.hasErrors()) {
			return "registerFilmmaker";
		}

		try {
			filmmakerService.registerFilmmaker(filmmakerRegisterData);
		} catch (NotUniqueException e) {
			result.rejectValue("name", "", e.getMessage());
			return "registerFilmmaker";
		}
		return "redirect:/";
	}

	@RequestMapping("/profile/{filmmmakerId}")
	public String getProfileView(@PathVariable Long filmmmakerId, Map<String, Object> model,@ModelAttribute FilmmakerProfileData filmmakerProfileData) {

		User user = userService.getUserById(filmmmakerId).orElse(null);
		if (user == null || user.getType() != UserType.Filmmaker) {
			return "redirect:/";
		}

		Filmmaker filmmaker = (Filmmaker) user;
		filmmakerProfileData.updateFromFilmmaker(filmmaker);
		filmmakerProfileData.setTotalShortFilms(shortFilmService.getShortFilmsCountByUploader(filmmaker));
		filmmakerProfileData.getUploadedShortFilmPagination().setPageElements(2);
		filmmakerProfileData.getUploadedShortFilmPagination().setTotalElements(shortFilmService.getShortFilmsCountByUploader(filmmaker));
		filmmakerProfileData.setUploadedShortFilms(shortFilmService
				.getShortFilmsByUploader(filmmaker,
						filmmakerProfileData.getUploadedShortFilmPagination().getPageRequest(Sort.by("uploadDate").descending()))
				.getContent());
		
		filmmakerProfileData.getAttachedShortFilmPagination().setPageElements(2);
		filmmakerProfileData.getAttachedShortFilmPagination().setTotalElements(shortFilmService.getShortFilmsCountAttachedShortFilmByFilmmaker(filmmaker));
		filmmakerProfileData.setAttachedShortFilms(shortFilmService
				.getAttachedShortFilmByFilmmaker(filmmaker, 
						filmmakerProfileData.getAttachedShortFilmPagination().getPageRequest(Sort.by("uploadDate").descending()))
				.getContent());
		
		
		model.put("filmmakerProfileData", filmmakerProfileData);

		model.put("followButton", true);

		User viewer = userService.getLoggedUser().orElse(null);
		if (viewer == null) {
			return "filmmakerProfile";
		}

		if (viewer.getType().equals(UserType.Company)) {
			model.put("privacyRequestButton", true);

			Company viewerCompany = (Company) viewer;
			PrivacyRequest sentRequest = viewerCompany.getSentRequests().stream()
					.filter((x -> x.getFilmmaker().getName().equals(filmmaker.getName()))).findFirst().orElse(null);
			if (sentRequest != null) {
				model.put("disablePrivacyRequestButton", true);
				// [TO-DO] Allow to accept privacy requests!!
				// if (sentRequest.getRequestState() == RequestStateType.ACCEPTED) {
				model.put("personalInformation", true);
				// }
			}
		} else {
			Filmmaker viewerFilmmaker = (Filmmaker) viewer;
			if (viewerFilmmaker.getName().equals(filmmaker.getName())) {
				model.remove("followButton");
				model.put("accountButton", true);
				model.put("personalInformation", true);
			}
		}

		if (filmmaker.getFilmmakerSubscribers().stream().anyMatch(x -> x.getName() == viewer.getName())) {
			model.put("alreadyFollowed", true);
		}

		return "filmmakerProfile";
	}

	@PostMapping("/profile/{userId}/subscription")
	public String sucribesToFilmmaker(@PathVariable Long userId) {
		User follower = userService.getLoggedUser().orElse(null);
		if (follower == null) {
			return "redirect:/login";
		}

		User user = userService.getUserById(userId).orElse(null);
		if (user.getType() != UserType.Filmmaker) {
			return String.format("redirect:/profile/%d", userId);
		}

		Filmmaker followed = (Filmmaker) user;
		if (followed.getFilmmakerSubscribers().stream().anyMatch(x -> x.getName() == follower.getName())) {
			return String.format("redirect:/profile/%d", userId);
		}

		if (followed.getName().equals(follower.getName())) {
			return String.format("redirect:/profile/%d", userId);
		}

		userService.subscribesTo(follower, followed);
		return String.format("redirect:/profile/%d", userId);
	}

	@PostMapping("/profile/{userId}/unsubscription")
	public String unsucribesToFilmmaker(@PathVariable Long userId) {
		User follower = userService.getLoggedUser().orElse(null);
		
		if (follower == null) {
			return "redirect:/login";
		}

		User user = userService.getUserById(userId).orElse(null);
		if (user.getType() != UserType.Filmmaker) {
			return String.format("redirect:/profile/%d", userId);
		}

		Filmmaker followed = (Filmmaker) user;
		if (!followed.getFilmmakerSubscribers().stream().anyMatch(x -> x.getName() == follower.getName())) {
			return String.format("redirect:/profile/%d", userId);
		}

		if (followed.getName().equals(follower.getName())) {
			return String.format("redirect:/profile/%d", userId);
		}

		userService.unsubscribesTo(follower, followed);
		return String.format("redirect:/profile/%d", userId);
	}

	@PostMapping("/profile/{filmmakerId}/privacyrequest")
	public String sendPrivacyRequest(@PathVariable Long filmmakerId) {
		User sender = userService.getLoggedUser().orElse(null);
		if (sender == null) {
			return "redirect:/login";
		}
		if (sender.getType() != UserType.Company) {
			return String.format("redirect:/profile/%d", filmmakerId);
		}

		User receiver = userService.getUserById(filmmakerId).orElse(null);
		if (receiver.getType() != UserType.Filmmaker) {
			return String.format("redirect:/profile/%d", filmmakerId);
		}

		Company company = (Company) sender;
		if (company.getSentRequests().stream().anyMatch(x -> x.getFilmmaker().getName().equals(receiver.getName()))) {
			return String.format("redirect:/profile/%d", filmmakerId);
		}

		Filmmaker filmmaker = (Filmmaker) receiver;
		privacyRequestService.sendPrivacyRequest(company, filmmaker);
		return String.format("redirect:/profile/%d", filmmakerId);
	}

	@GetMapping("/account/filmmaker")
	public String getManageAccount(Map<String, Object> model) {
		User user = userService.getLoggedUser().orElse(null);
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
	public String doManageAccount(@ModelAttribute FilmmakerConfigurationData filmmakerConfigurationData,
			BindingResult result, Map<String, Object> model) {
		User user = userService.getLoggedUser().orElse(null);
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
		filmmakerService.updateFilmmakerData(userFilmmaker, filmmakerConfigurationData);
		if (filmmakerConfigurationData.getNewPhoto() != null && !filmmakerConfigurationData.getNewPhoto().isEmpty()) {
			try {
				userService.setProfilePicture(userFilmmaker, filmmakerConfigurationData.getNewPhoto());
			} catch (Exception e) {
				result.reject("", e.getMessage());
				model.put("photoUrl", user.getPhotoUrl());
				return "manageFilmmakerAccount";
			}
		}

		model.put("filmmakerData", filmmakerConfigurationData);
		model.put("photoUrl", user.getPhotoUrl());
		model.put("success", "Configuration has been saved successfully!");
		return "manageFilmmakerAccount";
	}
	
}
