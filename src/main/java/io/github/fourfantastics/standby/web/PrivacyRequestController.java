package io.github.fourfantastics.standby.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.PrivacyRequest;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.model.form.RequestData;
import io.github.fourfantastics.standby.service.PrivacyRequestService;
import io.github.fourfantastics.standby.service.UserService;

@Controller
public class PrivacyRequestController {
	@Autowired
	UserService userService;

	@Autowired
	PrivacyRequestService privacyRequestService;

	@RequestMapping("/requests")
	public String getPrivacyRequestView(Map<String, Object> model, @ModelAttribute RequestData requestData) {
		User user = userService.getLoggedUser().orElse(null);
		if (user == null) {
			return "redirect:/login";
		}

		if (user.getType() != UserType.Filmmaker) {
			return "redirect:/";
		}

		Filmmaker filmmaker = (Filmmaker) user;
		requestData.setFilmmaker(filmmaker);
		
		Page<PrivacyRequest> page = privacyRequestService.getPrivacyRequestByFilmmaker(filmmaker.getId(), requestData
				.getPrivacyRequestPagination().getPageRequest(Sort.by("privacyRequest.requestDate").descending()));
		requestData.getPrivacyRequestPagination().setTotalElements((int) page.getTotalElements());
		requestData.setRequests(page.getContent());
		model.put("requestData", requestData);
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

	@PostMapping("/requests/{requestId}/accept")
	public String acceptPrivactRequest(@PathVariable Long requestId) {
		User sender = userService.getLoggedUser().orElse(null);
		if (sender == null) {
			return "redirect:/login";
		}

		try {
			privacyRequestService.acceptPrivacyRequest(sender, requestId);
		} catch (Exception e) {
			return String.format("redirect:/requests");
		}

		return "redirect:/requests";
	}

	@PostMapping("/requests/{requestId}/decline")
	public String declinePrivactRequest(@PathVariable Long requestId) {
		User sender = userService.getLoggedUser().orElse(null);
		if (sender == null) {
			return "redirect:/login";
		}

		try {
			privacyRequestService.declinePrivacyRequest(sender, requestId);
		} catch (Exception e) {
			return String.format("redirect:/requests");
		}

		return "redirect:/requests";
	}
}
