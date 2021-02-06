package io.github.fourfantastics.standby.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.service.PrivacyRequestService;
import io.github.fourfantastics.standby.service.UserService;

@Controller
public class PrivacyRequestController {
	@Autowired
	UserService userService;

	@Autowired
	PrivacyRequestService privacyRequestService;

	@GetMapping("/requests")
	public String getPrivacyRequestView() {
		return "requests";
	}
	
	@PostMapping("/profile/{filmmakerId}/request")
	public String sendPrivacyRequest(@PathVariable Long filmmakerId) {
		User sender = userService.getLoggedUser().orElse(null);
		if (sender == null) {
			return "redirect:/login";
		}

		User receiver = userService.getUserById(filmmakerId).orElse(null);

		if (receiver == null) {
			return "redirect:/";
		}

		try {
			privacyRequestService.sendPrivacyRequest(sender, receiver);
		} catch (Exception e) {
			return String.format("redirect:/profile/%d", filmmakerId);
		}

		return String.format("redirect:/profile/%d", filmmakerId);
	}

}
