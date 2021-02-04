package io.github.fourfantastics.standby.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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

		model.put("credentials", new Credentials());
		return "login";
	}

	@PostMapping("/login/success")
	public String redirectLoginSuccess() {
		return "redirect:/";
	}

	@GetMapping("/logout")
	public String doLogout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return "redirect:/login";
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

	@GetMapping("/")
	public String getFeedView() {
		User user = userService.getLoggedUser().orElse(null);
		if (user == null) {
			return "redirect:/login";
		}

		return "index";
	}
}
