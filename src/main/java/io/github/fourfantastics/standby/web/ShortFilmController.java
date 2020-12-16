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
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.model.form.ShortFilmUploadData;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.UserService;

@Controller
public class ShortFilmController {

	@Autowired
	UserService userService;
	
	@Autowired
	ShortFilmService shortFilmService;

	@GetMapping("/upload")
	public String getUploadView(HttpSession session, Map<String, Object> model) {

		User loggedUser = userService.getLoggedUser(session).orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}
		if (loggedUser.getType() != UserType.Filmmaker) {
			return "redirect:/";
		}

		model.put("shortFilmUploadData", new ShortFilmUploadData());

		return "uploadShortFilm";
	}

	@PostMapping("/upload")
	public String uploadShortFilm(HttpSession session,
			@ModelAttribute("shortFilmUploadData") ShortFilmUploadData shortFilmUploadData, BindingResult result,
			Map<String, Object> model) {

		User loggedUser = userService.getLoggedUser(session).orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}
		if (loggedUser.getType() != UserType.Filmmaker) {
			return "redirect:/";
		}

		if (result.hasErrors()) {
			return "uploadShortFilm";
		}
		try {
			shortFilmService.upload(shortFilmUploadData,(Filmmaker) loggedUser);
		}
		catch(Exception e) {
			result.reject("", e.getMessage());
			return "uploadShortFilm";
		}
		return "redirect:/";//temporal,provisional
	}
}
