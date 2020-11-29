package io.github.fourfantastics.standby.web;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exceptions.DataMismatchException;
import io.github.fourfantastics.standby.service.exceptions.NotFoundException;

@Controller
public class UserController {
	@Autowired
	UserService userService;
	
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id", "type", "email", "creationDate", "photoUrl",
				"notifications", "ratings", "comments", "favouriteShortFilms");
	}
	
	@GetMapping("/login")
	public String getLogin(HttpSession session, Map<String, Object> model) {
		if (userService.isLogged(session)) {
			return "redirect:/";
		}
		
		model.put("user", new User());
		return "login.html";
	}
	
	@PostMapping("/login")
	public String doLogin(HttpSession session, User user, BindingResult result, Map<String, Object> model) {
		if (userService.isLogged(session)) {
			return "redirect:/";
		}
		
		if (result.hasErrors()) {
			return "login.html";
		}
		
		User loggedUser;
		try {
			loggedUser = userService.authenticate(user.getName(), user.getPassword());
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
}
