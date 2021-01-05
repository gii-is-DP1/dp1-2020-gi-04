package io.github.fourfantastics.standby.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.model.form.ShortFilmEditData;
import io.github.fourfantastics.standby.model.form.ShortFilmUploadData;
import io.github.fourfantastics.standby.model.validator.ShortFilmUploadDataValidator;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.InvalidExtensionException;
import io.github.fourfantastics.standby.service.exception.TooBigException;

@Controller
public class ShortFilmController {
	@Autowired
	UserService userService;

	@Autowired
	ShortFilmService shortFilmService;

	@Autowired
	ShortFilmUploadDataValidator shortFilmUploadDataValidator;

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

	@RequestMapping(value = "upload", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	public @ResponseBody Object uploadShortFilm(HttpSession session,
			@ModelAttribute("shortFilmUploadData") ShortFilmUploadData shortFilmUploadData, BindingResult result,
			Map<String, Object> model) {
		Map<String, Object> res = new HashMap<String, Object>();

		User loggedUser = userService.getLoggedUser(session).orElse(null);
		if (loggedUser == null) {
			res.put("status", 302);
			res.put("url", "/login");
			return res;
		}

		if (loggedUser.getType() != UserType.Filmmaker) {
			res.put("status", 302);
			res.put("url", "/");
			return res;
		}

		Map<String, String> fieldErrors = new HashMap<String, String>();
		shortFilmUploadDataValidator.validate(shortFilmUploadData, result);
		if (result.hasErrors()) {
			for (FieldError fieldError : result.getFieldErrors()) {
				fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
			}
			res.put("status", 400);
			res.put("message", "");
			res.put("fieldErrors", fieldErrors);
			return res;
		}

		ShortFilm shortFilm = null;
		try {
			shortFilm = shortFilmService.upload(shortFilmUploadData, (Filmmaker) loggedUser);
		} catch (InvalidExtensionException | TooBigException e) {
			res.put("status", 400);
			res.put("message", e.getMessage());
			res.put("fieldErrors", fieldErrors);
			return res;
		} catch (RuntimeException e) {
			res.put("status", 500);
			res.put("message", e.getMessage());
			res.put("fieldErrors", fieldErrors);
			return res;
		}

		res.put("status", 302);
		res.put("url", String.format("/shortfilm/%d/edit", shortFilm.getId()));
		return res;
	}

	@GetMapping("/shortfilm/{shortFilmId}/edit")
	public String getUploadView(HttpSession session, @PathVariable("shortFilmId") Long shortFilmId,
			Map<String, Object> model) {
		User loggedUser = userService.getLoggedUser(session).orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}

		ShortFilm film = shortFilmService.getShortFilmById(shortFilmId).orElse(null);
		if (film == null || !film.getUploader().equals((Filmmaker) loggedUser)) {
			return "redirect:/";
		}

		model.put("shortFilmEditData", ShortFilmEditData.fromShortFilm(film));
		return "editShortFilm";
	}
}
