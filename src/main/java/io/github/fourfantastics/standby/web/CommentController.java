package io.github.fourfantastics.standby.web;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.form.ShortFilmViewData;
import io.github.fourfantastics.standby.model.validator.ShortFilmViewDataValidator;
import io.github.fourfantastics.standby.service.CommentService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.NotFoundException;
import io.github.fourfantastics.standby.service.exception.UnauthorizedException;

@Controller
public class CommentController {
	@Autowired
	CommentService commentService;
	
	@Autowired
	UserService userService;

	@Autowired
	ShortFilmService shortFilmService;

	@Autowired
	ShortFilmViewDataValidator shortFilmViewDataValidator;

	@PostMapping(path = "/shortfilm/{shortFilmId}", params = { "postComment" })
	public String postComment(HttpSession session, @PathVariable Long shortFilmId,
			@ModelAttribute ShortFilmViewData shortFilmViewData, BindingResult result,
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
		shortFilmViewDataValidator.validate(shortFilmViewData, result);
		if (result.hasErrors()) {
			redirections.addFlashAttribute("errors", result);
			return String.format("redirect:/shortfilm/%d", shortFilmId);
		}

		commentService.commentShortFilm(shortFilmViewData.getNewCommentText(), shortFilm, loggedUser);
		shortFilmViewData.setNewCommentText("");

		return String.format("redirect:/shortfilm/%d", shortFilmId);
	}

	@PostMapping(path = "/shortfilm/{shortFilmId}", params = { "deleteComment" })
	public String removeComment(HttpSession session, @RequestParam Long deleteComment, @PathVariable Long shortFilmId,
			@ModelAttribute ShortFilmViewData shortFilmViewData, BindingResult result,
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

		Long commentId = deleteComment;
		try {
			commentService.removeUserComment(commentId, loggedUser);
		} catch (NotFoundException | UnauthorizedException e) {
			result.reject("", e.getMessage());
		}

		return String.format("redirect:/shortfilm/%d", shortFilmId);
	}
}
