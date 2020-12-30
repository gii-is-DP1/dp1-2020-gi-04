package io.github.fourfantastics.standby.web;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.model.form.FilmmakerRegisterData;
import io.github.fourfantastics.standby.model.validator.FilmmakerRegisterDataValidator;
import io.github.fourfantastics.standby.service.FilmmakerService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.NotUniqueException;

@Controller
public class FilmmakerController {
	@Autowired
	UserService userService;

	@Autowired
	FilmmakerService filmmakerService;
	
	@Autowired
	FilmmakerRegisterDataValidator filmmakerRegisterDataValidator;

	@GetMapping("/register/filmmaker")
	public String getRegisterView(HttpSession session, Map<String, Object> model) {
		if (userService.getLoggedUser(session).isPresent()) {
			return "redirect:/";
		}
		model.put("filmmakerRegisterData", new FilmmakerRegisterData());
		return "registerFilmmaker";
	}

	@PostMapping("/register/filmmaker")
	public String registerFilmmaker(HttpSession session,
			@ModelAttribute("filmmakerRegisterData") FilmmakerRegisterData filmmakerRegisterData, BindingResult result,
			Map<String, Object> model) {
		if (userService.getLoggedUser(session).isPresent()) {
			return "redirect:/";
		}

		filmmakerRegisterDataValidator.validate(filmmakerRegisterData, result);
		if (result.hasErrors()) {
			return "registerFilmmaker";
		}

		try {
			Filmmaker filmmaker = filmmakerService.registerFilmmaker(filmmakerRegisterData);
			userService.logIn(session, filmmaker);
		} catch (NotUniqueException e) {
			result.rejectValue("name", "", e.getMessage());
			return "registerFilmmaker";
		}
		return "redirect:/";
	}
	

}
