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
import io.github.fourfantastics.standby.model.RequestStateType;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.model.form.FilmmakerConfigurationData;
import io.github.fourfantastics.standby.model.form.FilmmakerProfileData;
import io.github.fourfantastics.standby.model.form.FilmmakerRegisterData;
import io.github.fourfantastics.standby.model.validator.FilmmakerConfigurationDataValidator;
import io.github.fourfantastics.standby.model.validator.FilmmakerRegisterDataValidator;
import io.github.fourfantastics.standby.service.FilmmakerService;
import io.github.fourfantastics.standby.service.PrivacyRequestService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.SubscriptionService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.NotUniqueException;

@Controller
public class FilmmakerController {
	@Autowired
	FilmmakerService filmmakerService;

	@Autowired
	UserService userService;
	
	@Autowired
	SubscriptionService subscriptionService;

	@Autowired
	ShortFilmService shortFilmService;
	
	@Autowired
	PrivacyRequestService privacyRequestService;

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
	public String getProfileView(@PathVariable Long filmmmakerId, Map<String, Object> model, @ModelAttribute FilmmakerProfileData filmmakerProfileData) {
		User user = userService.getUserById(filmmmakerId).orElse(null);
		if (user == null || user.getType() != UserType.Filmmaker) {
			return "redirect:/";
		}

		Filmmaker filmmaker = (Filmmaker) user;
		filmmakerProfileData.setFilmmaker(filmmaker);
		
		Integer shortFilmCount = shortFilmService.getShortFilmsCountByUploader(filmmaker);
		filmmakerProfileData.setTotalShortFilms(shortFilmCount);
		
		filmmakerProfileData.getUploadedShortFilmPagination().setTotalElements(shortFilmCount);
		filmmakerProfileData.setUploadedShortFilms(shortFilmService
				.getShortFilmsByUploader(filmmaker,
						filmmakerProfileData.getUploadedShortFilmPagination().getPageRequest(Sort.by("uploadDate").descending()))
				.getContent());
		
		filmmakerProfileData.getAttachedShortFilmPagination().setTotalElements(shortFilmService.getAttachedShortFilmsCountByFilmmaker(filmmaker.getId()));
		filmmakerProfileData.setAttachedShortFilms(shortFilmService
				.getAttachedShortFilmsByFilmmaker(filmmaker.getId(), 
						filmmakerProfileData.getAttachedShortFilmPagination().getPageRequest(Sort.by("uploadDate").descending()))
				.getContent());
		
		filmmakerProfileData.setFollowerCount(subscriptionService.getFollowerCount(filmmaker));
		filmmakerProfileData.setFollowedCount(subscriptionService.getFollowedCount(filmmaker));
		
		model.put("filmmakerProfileData", filmmakerProfileData);
		model.put("followButton", true);

		User viewer = userService.getLoggedUser().orElse(null);
		if (viewer == null) {
			return "filmmakerProfile";
		}

		if (viewer.getType().equals(UserType.Company)) {
			model.put("privacyRequestButton", true);

			Company viewerCompany = (Company) viewer;
			PrivacyRequest sentRequest = privacyRequestService.getPrivacyRequestByFilmmakerAndCompany(filmmaker, viewerCompany).orElse(null);
			if (sentRequest != null) {
				model.put("disablePrivacyRequestButton", true);
				if (sentRequest.getRequestState() == RequestStateType.ACCEPTED) {
					model.put("personalInformation", true);
				}
			}
		} else {
			Filmmaker viewerFilmmaker = (Filmmaker) viewer;
			if (viewerFilmmaker.getName().equals(filmmaker.getName())) {
				model.remove("followButton");
				model.put("accountButton", true);
				model.put("personalInformation", true);
			}
		}

		if (subscriptionService.isAlreadySubscribedTo(viewer, filmmaker)) {
			model.put("alreadyFollowed", true);
		}

		return "filmmakerProfile";
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
