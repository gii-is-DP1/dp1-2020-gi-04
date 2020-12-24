package io.github.fourfantastics.standby.web;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.model.form.CompanyConfigurationData;
import io.github.fourfantastics.standby.model.form.Credentials;
import io.github.fourfantastics.standby.model.form.FilmmakerConfigurationData;
import io.github.fourfantastics.standby.model.validator.CompanyConfigurationDataValidator;
import io.github.fourfantastics.standby.model.validator.CredentialsValidator;
import io.github.fourfantastics.standby.model.validator.FilmmakerConfigurationDataValidator;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.DataMismatchException;
import io.github.fourfantastics.standby.service.exception.NotFoundException;

@Controller
public class UserController {
	@Autowired
	UserService userService;
	
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
		return "login.html";
	}

	@PostMapping("/login")
	public String doLogin(HttpSession session, @ModelAttribute("credentials") Credentials credentials,
			BindingResult result, Map<String, Object> model) {
		if (userService.getLoggedUser(session).isPresent()) {
			return "redirect:/";
		}

		credentialsValidator.validate(credentials, result);
		if (result.hasErrors()) {
			return "login.html";
		}

		User loggedUser;
		try {
			loggedUser = userService.authenticate(credentials.getName(), credentials.getPassword());
		} catch (NotFoundException e) {
			result.rejectValue("name", "", e.getMessage());
			return "login.html";
		} catch (DataMismatchException e) {
			result.rejectValue("password", "", e.getMessage());
			return "login.html";
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
		Optional<User> optionalUser = userService.getLoggedUser(session);
		if (!optionalUser.isPresent()) {
			return "redirect:/login";
		}

		User user = optionalUser.get();
		if (user.getType() == UserType.Filmmaker) {
			Filmmaker filmmaker = (Filmmaker) user;
			model.put("filmmakerConfigurationData", FilmmakerConfigurationData.fromFilmmaker(filmmaker));
			return "manageFilmmakerAccount";
		} else {
			Company company = (Company) user;
			model.put("companyConfigurationData", CompanyConfigurationData.fromCompany(company));
			return "manageCompanyAccount";
		}
	}

	@PostMapping("/account/filmmaker")
	public String doManageAccount(HttpSession session,
			@ModelAttribute("filmmakerConfigurationData") FilmmakerConfigurationData filmmakerConfigurationData,
			BindingResult result, Map<String, Object> model) {
		Optional<User> optionalUser = userService.getLoggedUser(session);
		if (!optionalUser.isPresent()) {
			return "redirect:/login";
		}
		User user = optionalUser.get();
		if (user.getType() != UserType.Filmmaker) {
			return "redirect:/manageAccount";
		}

		filmmakerConfigurationDataValidator.validate(filmmakerConfigurationData, result);
		if (result.hasErrors()) {
			return "manageFilmmakerAccount";
		}

		Filmmaker userFilmmaker = (Filmmaker) user;
		filmmakerConfigurationData.copyToFilmmaker(userFilmmaker);
		userFilmmaker = (Filmmaker) userService.saveUser(userFilmmaker);

		model.put("filmmakerData", filmmakerConfigurationData);
		return "manageFilmmakerAccount";
	}

	@PostMapping("/account/company")
	public String doManageAccount(HttpSession session,
			@ModelAttribute("companyConfigurationData") CompanyConfigurationData companyConfigurationData,
			BindingResult result, Map<String, Object> model) {
		Optional<User> optionalUser = userService.getLoggedUser(session);
		if (!optionalUser.isPresent()) {
			return "redirect:/login";
		}

		User user = optionalUser.get();
		if (user.getType() != UserType.Company) {
			return "redirect:/manageAccount";
		}

		companyConfigurationDataValidator.validate(companyConfigurationData, result);
		if (result.hasErrors()) {
			return "manageCompanyAccount";
		}
		Company userCompany = (Company) user;
		companyConfigurationData.copyToCompany(userCompany);
		userCompany = (Company) userService.saveUser(userCompany);

		model.put("companyData", companyConfigurationData);
		return "manageCompanyAccount";
	}
}
