package io.github.fourfantastics.standby.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.form.ShortFilmViewData;
import io.github.fourfantastics.standby.service.RatingService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.UserService;

@Controller
public class RatingController {

	@Autowired
	ShortFilmService shortFilmService;

	@Autowired
	UserService userService;

	@Autowired
	RatingService ratingService;

	@PostMapping(path = "/shortfilm/{shortFilmId}", params = { "rate" })
	public String rateShortFilm(HttpSession session, HttpServletRequest req,
			@PathVariable("shortFilmId") Long shortFilmId,
			@ModelAttribute("shortFilmViewData") ShortFilmViewData shortFilmViewData, BindingResult result,
			Map<String, Object> model, RedirectAttributes redirections) {
		ShortFilm shortFilm = shortFilmService.getShortFilmById(shortFilmId).orElse(null);
		if (shortFilm == null) {
			return "redirect:/";
		}

		User loggedUser = userService.getLoggedUser(session).orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}

		redirections.addFlashAttribute(shortFilmViewData);

		if (result.hasErrors()) {
			redirections.addFlashAttribute("errors", result);
			return String.format("redirect:/shortfilm/%d", shortFilmId);
		}

		Integer rate = Integer.parseInt(req.getParameter("rate"));

		ratingService.rateShortFilm(shortFilm, loggedUser, rate);

		return String.format("redirect:/shortfilm/%d", shortFilmId);
	}

	@PostMapping(path = "/shortfilm/{shortFilmId}", params = { "deleteRating" })
	public String removeRating(HttpSession session, HttpServletRequest req,
			@PathVariable("shortFilmId") Long shortFilmId,
			@ModelAttribute("shortFilmViewData") ShortFilmViewData shortFilmViewData, BindingResult result,
			Map<String, Object> model, RedirectAttributes redirections) {
		ShortFilm shortFilm = shortFilmService.getShortFilmById(shortFilmId).orElse(null);
		if (shortFilm == null) {
			return "redirect:/";
		}

		User loggedUser = userService.getLoggedUser(session).orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}

		redirections.addFlashAttribute(shortFilmViewData);

		if (result.hasErrors()) {
			redirections.addFlashAttribute("errors", result);
			return String.format("redirect:/shortfilm/%d", shortFilmId);
		}

		ratingService.removeRating(loggedUser, shortFilm);

		return String.format("redirect:/shortfilm/%d", shortFilmId);

	}
}
