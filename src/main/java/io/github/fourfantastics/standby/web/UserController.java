package io.github.fourfantastics.standby.web;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.model.form.Credentials;
import io.github.fourfantastics.standby.model.validator.CompanyConfigurationDataValidator;
import io.github.fourfantastics.standby.model.validator.CredentialsValidator;
import io.github.fourfantastics.standby.model.validator.FilmmakerConfigurationDataValidator;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.DataMismatchException;
import io.github.fourfantastics.standby.service.exception.NotFoundException;

@Controller
public class UserController {
	@Autowired
	UserService userService;

	@Autowired
	ShortFilmService shortFilmService;

	@Autowired
	CredentialsValidator credentialsValidator;

	@Autowired
	FilmmakerConfigurationDataValidator filmmakerConfigurationDataValidator;

	@Autowired
	CompanyConfigurationDataValidator companyConfigurationDataValidator;

	@GetMapping("/login")
	public String getLogin(HttpSession session, Map<String, Object> model) {
		if (userService.getLoggedUser(session).isPresent()) {
			return "redirect:/";
		}

		model.put("credentials", new Credentials());
		return "login";
	}

	@PostMapping("/login")
	public String doLogin(HttpSession session, @ModelAttribute("credentials") Credentials credentials,
			BindingResult result, Map<String, Object> model) {
		if (userService.getLoggedUser(session).isPresent()) {
			return "redirect:/";
		}

		credentialsValidator.validate(credentials, result);
		if (result.hasErrors()) {
			return "login";
		}

		User loggedUser;
		try {
			loggedUser = userService.authenticate(credentials.getName(), credentials.getPassword());
		} catch (NotFoundException e) {
			result.rejectValue("name", "", e.getMessage());
			return "login";
		} catch (DataMismatchException e) {
			result.rejectValue("password", "", e.getMessage());
			return "login";
		}

		userService.logIn(session, loggedUser);
		return "redirect:/";
	}

	@RequestMapping("/logout")
	public String doLogout(HttpSession session) {
		userService.logOut(session);
		return "redirect:/";
	}

	@GetMapping("/account")
	public String getManageAccount(HttpSession session, Map<String, Object> model) {
		User user = userService.getLoggedUser(session).orElse(null);
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
	public String getProfileView(HttpSession session) {
		User user = userService.getLoggedUser(session).orElse(null);
		if (user == null) {
			return "redirect:/login";
		}
		
		return String.format("redirect:/profile/%d", user.getId());
	}

	@GetMapping("/profile/{userID}")
	public String getProfileView(@PathVariable("userID") Long userID) {
		if (userID == null) {
			System.out.println("sosioooooooo q la id es nula mi vidaaa");
			return "redirect:/";
		}
		
		User user = userService.getUserById(userID).orElse(null);
		if (user == null) {
			return "redirect:/";
		}

		if (user.getType() == UserType.Filmmaker) {
			return String.format("redirect:/profile/filmmaker/%d", userID);
		} else {
			return String.format("redirect:/profile/company/%d", userID);
		}
	}
}
