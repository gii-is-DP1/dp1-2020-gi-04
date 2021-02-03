package io.github.fourfantastics.standby.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.model.form.Pagination;
import io.github.fourfantastics.standby.model.form.ShortFilmViewData;
import io.github.fourfantastics.standby.model.form.UserFavouriteShortFilmsData;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.UserService;

@Controller
public class FavouriteShortfilmController {

	@Autowired
	ShortFilmService shortFilmService;

	@Autowired
	UserService userService;

	@PostMapping(path = "/shortfilm/{shortFilmId}", params = { "favouriteShortfilm" })
	public String favouriteShortFilm(@RequestParam Integer favouriteShortfilm, @PathVariable Long shortFilmId,
			@ModelAttribute ShortFilmViewData shortFilmViewData, Map<String, Object> model,
			RedirectAttributes redirections) {
		ShortFilm shortFilm = shortFilmService.getShortFilmById(shortFilmId).orElse(null);
		if (shortFilm == null) {
			return "redirect:/";
		}

		User loggedUser = userService.getLoggedUser().orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}

		redirections.addFlashAttribute(shortFilmViewData);

		userService.favouriteShortFilm(shortFilm, loggedUser);
		return String.format("redirect:/shortfilm/%d", shortFilmId);
	}

	@PostMapping(path = "/shortfilm/{shortFilmId}", params = { "deleteFavourite" })
	public String removeFavouriteShortFilm(@RequestParam Integer deleteFavourite, @PathVariable Long shortFilmId,
			@ModelAttribute ShortFilmViewData shortFilmViewData, Map<String, Object> model,
			RedirectAttributes redirections) {
		ShortFilm shortFilm = shortFilmService.getShortFilmById(shortFilmId).orElse(null);
		if (shortFilm == null) {
			return "redirect:/";
		}

		User loggedUser = userService.getLoggedUser().orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}

		redirections.addFlashAttribute(shortFilmViewData);

		userService.removeFavouriteShortFilm(shortFilm, loggedUser);
		return String.format("redirect:/shortfilm/%d", shortFilmId);
	}
	
	@RequestMapping("/favourites")
	public String getFavouritesView(Map<String, Object> model,@ModelAttribute UserFavouriteShortFilmsData userFavouriteShortFilmsData) {
		User user = userService.getLoggedUser().orElse(null);
		if (user == null) {
			return "redirect:/login";
		}
		
		userFavouriteShortFilmsData.setFavouriteShortFilmPagination(Pagination.empty());
		
		userFavouriteShortFilmsData.getFavouriteShortFilmPagination().setPageElements(2);
		userFavouriteShortFilmsData.getFavouriteShortFilmPagination().setTotalElements(shortFilmService.getCountFavouriteShortFilmsByUser(user));
		userFavouriteShortFilmsData.setFavouriteShortFilms(shortFilmService
				.getFavouriteShortFilmsByUser(user, userFavouriteShortFilmsData.getFavouriteShortFilmPagination().getPageRequest())
				.getContent());
		System.out.println(user.getFavouriteShortFilms());
		model.put("userFavouriteShortFilmsData", userFavouriteShortFilmsData);
		System.out.println(userFavouriteShortFilmsData.getFavouriteShortFilms());
		return "favouriteShortFilmsFilmmaker";
	}

}
