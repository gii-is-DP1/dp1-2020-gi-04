package io.github.fourfantastics.standby.web;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.form.ShortFilmViewData;
import io.github.fourfantastics.standby.service.RatingService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.utils.Utils;

@Controller
public class RatingController {
	@Autowired
	RatingService ratingService;
	
	@Autowired
	ShortFilmService shortFilmService;

	@Autowired
	UserService userService;

	@PostMapping(path = "/shortfilm/{shortFilmId}", params = { "rate" })
	public String rateShortFilm(HttpSession session, @RequestParam Integer rate, @PathVariable Long shortFilmId,
			@ModelAttribute ShortFilmViewData shortFilmViewData, Map<String, Object> model,
			RedirectAttributes redirections) {
		ShortFilm shortFilm = shortFilmService.getShortFilmById(shortFilmId).orElse(null);
		if (shortFilm == null) {
			return "redirect:/";
		}

		User loggedUser = userService.getLoggedUser(session).orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}

		redirections.addFlashAttribute(shortFilmViewData);

		ratingService.rateShortFilm(shortFilm, loggedUser, Utils.ensureRange(rate, 10, 1));
		return String.format("redirect:/shortfilm/%d", shortFilmId);
	}

	@PostMapping(path = "/shortfilm/{shortFilmId}", params = { "deleteRating" })
	public String removeRating(HttpSession session, @RequestParam Integer deleteRating, @PathVariable Long shortFilmId,
			@ModelAttribute ShortFilmViewData shortFilmViewData, Map<String, Object> model,
			RedirectAttributes redirections) {
		ShortFilm shortFilm = shortFilmService.getShortFilmById(shortFilmId).orElse(null);
		if (shortFilm == null) {
			return "redirect:/";
		}

		User loggedUser = userService.getLoggedUser(session).orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}

		redirections.addFlashAttribute(shortFilmViewData);
		
		ratingService.removeRating(loggedUser, shortFilm);
		return String.format("redirect:/shortfilm/%d", shortFilmId);
	}
}
