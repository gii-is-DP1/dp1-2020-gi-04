package io.github.fourfantastics.standby.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.model.form.Credentials;
import io.github.fourfantastics.standby.model.validator.CompanyConfigurationDataValidator;
import io.github.fourfantastics.standby.model.validator.CredentialsValidator;
import io.github.fourfantastics.standby.service.AccountService;
import io.github.fourfantastics.standby.service.UserService;

@Controller
public class UserController {
	@Autowired
	UserService userService;

	@Autowired
	CredentialsValidator credentialsValidator;

	@Autowired
	CompanyConfigurationDataValidator companyConfigurationDataValidator;

	@Autowired
	AccountService accountService;

	@GetMapping("/login")
	public String getLogin(@ModelAttribute Credentials credentials, Map<String, Object> model,
			BindingResult bindingResult) {
		if (userService.getLoggedUser().isPresent()) {
			return "redirect:/";
		}
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("Is authenticated: " + authentication.isAuthenticated());
		System.out.println("Is authenticated: " + authentication.getAuthorities());
		model.put("credentials", new Credentials());
		System.out.println(bindingResult.getAllErrors());
		return "login";
	}

	@GetMapping("/account")
	public String getManageAccount() {
		User user = userService.getLoggedUser().orElse(null);
		if (user == null) {
			return "redirect:/login";
		}

		if (user.getType() == UserType.Filmmaker) {
			return "redirect:/account/filmmaker";
		} else {
			return "redirect:/account/company";
		}
	}

	@GetMapping("/profile")
	public String getProfileView() {
		User user = userService.getLoggedUser().orElse(null);
		if (user == null) {
			return "redirect:/login";
		}

		if (user.getType().equals(UserType.Filmmaker)) {
			return String.format("redirect:/profile/%d", user.getId());
		} else {
			return "redirect:/account";
		}
	}
}
