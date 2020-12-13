package io.github.fourfantastics.standby.web;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.form.FilmmakerRegisterData;
import io.github.fourfantastics.standby.service.FilmmakerService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exceptions.DataMismatchException;
import io.github.fourfantastics.standby.service.exceptions.NotUniqueException;

@Controller
public class FilmmakerController {

	@Autowired
	UserService userService;

	@Autowired
	FilmmakerService filmmakerService;

	@GetMapping("/register/filmmaker")
	public String getRegisterView(HttpSession session, Map<String, Object> model) {

		if (userService.getLoggedUser(session).isPresent()) {
			return "redirect:/";
		}
		model.put("filmmakerRegisterData", new FilmmakerRegisterData());
		return "registerFilmmaker.html";
	}

	@PostMapping("/register/filmmaker")
	public String registerFilmmaker(HttpSession session,
			@ModelAttribute("filmmakerRegisterData") FilmmakerRegisterData filmmakerRegisterData, BindingResult result,
			Map<String, Object> model) {
		if (userService.getLoggedUser(session).isPresent()) {
			return "redirect:/";
		}
	
		if (result.hasErrors()) {
			return "registerFilmmaker.html";
		}

		try {
			Filmmaker filmmaker = filmmakerService.registerFilmmaker(filmmakerRegisterData);
			userService.logIn(session, filmmaker);
		} catch (DataMismatchException e) {
			result.rejectValue("confirmPassword", "", e.getMessage());
			return "registerFilmmaker.html";
		} catch (NotUniqueException e) {
			result.rejectValue("name", "", e.getMessage());
			return "registerFilmmaker.html";
		} catch (Exception e) {
			result.reject("", e.getMessage());
		}
		return "redirect:/";
	}
}
