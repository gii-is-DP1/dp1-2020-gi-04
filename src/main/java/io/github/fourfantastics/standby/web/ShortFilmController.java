package io.github.fourfantastics.standby.web;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.ShortFilm;
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

	@RequestMapping(value = "upload", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	public @ResponseBody Object uploadShortFilm(HttpSession session,
			@ModelAttribute("shortFilmUploadData") ShortFilmUploadData shortFilmUploadData, BindingResult result,
			Map<String, Object> model) {

		User loggedUser = userService.getLoggedUser(session).orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}
		if (loggedUser.getType() != UserType.Filmmaker) {
			return "redirect:/";
		}

		Map<String, Object> res = new HashMap<String, Object>();

		long gigabyte = 1000L * 1000L * 1000L;
		if (shortFilmUploadData.getFile().getSize() > gigabyte) {
			res.put("status", 400);
			res.put("message", "File limit exceeded, file too large");
			return res;
		}

		ShortFilm shortFilm;

		try {
			shortFilm = shortFilmService.upload(shortFilmUploadData, (Filmmaker) loggedUser);

		} catch (Exception e) {
			res.put("status", 500);
			res.put("message", e.getMessage());
			return res;
		}
		res.put("status", 302);
		res.put("url", "/" + shortFilm.getFileUrl());

		return res;// temporal,provisional
	}
}
