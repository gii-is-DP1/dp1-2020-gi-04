package io.github.fourfantastics.standby.web;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.service.NotificationConfigurationService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exceptions.DataMismatchException;
import io.github.fourfantastics.standby.service.exceptions.NotFoundException;

@Controller
public class UserController {
	@Autowired
	UserService userService;
	
	@Autowired
	NotificationConfigurationService notificationConfigurationService;
	
	@InitBinder("credentials")
	public void initBinderCredentials(WebDataBinder dataBinder) {
		dataBinder.setAllowedFields("name", "password");
	}
	
	@GetMapping("/login")
	public String getLogin(HttpSession session, Map<String, Object> model) {
		if (userService.isLogged(session)) {
			return "redirect:/";
		}
		
		model.put("credentials", new User());
		return "login.html";
	}
	
	@PostMapping("/login")
	public String doLogin(HttpSession session, @ModelAttribute("credentials") User credentials,
			BindingResult result, Map<String, Object> model) {
		if (userService.isLogged(session)) {
			return "redirect:/";
		}
		
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
		} catch (Exception e) {
			result.reject("", "We weren't able to log you in, something went wrong!");
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
	
	@GetMapping("/manageAccount")
	public String getManageAccount(HttpSession session, Map<String, Object> model) {
		Optional<User> optionalUser = userService.getLoggedUser(session);
		if (!optionalUser.isPresent()) {
			return "redirect:/login";
		}
		
		User user = optionalUser.get();
		if (user.getType() == UserType.Filmmaker) {
			Filmmaker filmmaker = (Filmmaker) user;
			model.put("filmmaker", filmmaker);
			model.put("notificationConfiguration", filmmaker.getConfiguration());
			return "manageFilmmakerAccount";
		} else {
			Company company = (Company) user;
			model.put("company", company);
			model.put("notificationConfiguration", company.getConfiguration());
			return "manageCompanyAccount";
		}
	}
	
	@PostMapping("/manageFilmmakerAccount")
	public String doManageAccount(HttpSession session, @ModelAttribute("filmmaker") Filmmaker filmmaker,
			@ModelAttribute("notificationConfiguration") NotificationConfiguration notificationConfiguration,
			BindingResult result, Map<String, Object> model) {
		Optional<User> optionalUser = userService.getLoggedUser(session);
		if (!optionalUser.isPresent()) {
			return "redirect:/login";
		}
		
		User user = optionalUser.get();
		if (user.getType() != UserType.Filmmaker) {
			return "redirect:/manageAccount";
		}
		
		if (result.hasErrors()) {
			return "redirect:/manageAccount";
		}
		
		Filmmaker userFilmmaker = (Filmmaker) user;
		userFilmmaker.setCity(filmmaker.getCity());
		userFilmmaker.setCountry(filmmaker.getCountry());
		userFilmmaker.setFullname(filmmaker.getFullname());
		userFilmmaker.setPhone(filmmaker.getPhone());
		userFilmmaker = (Filmmaker) userService.saveUser(userFilmmaker);
			
		model.put("filmmaker", userFilmmaker);
		model.put("notificationConfiguration", userFilmmaker.getConfiguration());
		return "redirect:/manageAccount";
	}
}
