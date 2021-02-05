package io.github.fourfantastics.standby.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.service.SubscriptionService;
import io.github.fourfantastics.standby.service.UserService;

@Controller
public class SubscriptionController {
	@Autowired
	SubscriptionService subscriptionService;
	
	@Autowired
	UserService userService;
	
	@PostMapping("/profile/{userId}/subscription")
	public String subscribeToFilmmaker(@PathVariable Long userId) {
		User follower = userService.getLoggedUser().orElse(null);
		if (follower == null) {
			return "redirect:/login";
		}

		User user = userService.getUserById(userId).orElse(null);
		if (user.getType() != UserType.Filmmaker) {
			return String.format("redirect:/profile/%d", userId);
		}

		Filmmaker followed = (Filmmaker) user;
		if (followed.getName().equals(follower.getName())) {
			return String.format("redirect:/profile/%d", userId);
		}
		
		if (subscriptionService.isAlreadySubscribedTo(follower, followed)) {
			return String.format("redirect:/profile/%d", userId);
		}

		subscriptionService.subscribeTo(follower, followed);
		return String.format("redirect:/profile/%d", userId);
	}

	@PostMapping("/profile/{userId}/unsubscription")
	public String unsubscribeToFilmmaker(@PathVariable Long userId) {
		User follower = userService.getLoggedUser().orElse(null);
		if (follower == null) {
			return "redirect:/login";
		}

		User user = userService.getUserById(userId).orElse(null);
		if (user.getType() != UserType.Filmmaker) {
			return String.format("redirect:/profile/%d", userId);
		}

		Filmmaker followed = (Filmmaker) user;
		if (followed.getName().equals(follower.getName())) {
			return String.format("redirect:/profile/%d", userId);
		}
		
		if (!subscriptionService.isAlreadySubscribedTo(follower, followed)) {
			return String.format("redirect:/profile/%d", userId);
		}

		subscriptionService.unsubscribeTo(follower, followed);
		return String.format("redirect:/profile/%d", userId);
	}
}
