package io.github.fourfantastics.standby.web;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.fourfantastics.standby.model.Comment;
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
	UserService userService;

	@Autowired
	ShortFilmService shortFilmService;

	@Autowired
	CommentService commentService;

	@Autowired
	ShortFilmViewDataValidator shortFilmViewDataValidator;

	@PostMapping(path = "/shortfilm/{shortFilmId}", params = { "postComment" })
	public String postComment(HttpSession session, @PathVariable("shortFilmId") Long shortFilmId,
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
		shortFilmViewDataValidator.validate(shortFilmViewData, result);
		if (result.hasErrors()) {
			redirections.addFlashAttribute("errors", result);
			return String.format("redirect:/shortfilm/%d", shortFilmId);
		}

		Comment newComment = new Comment();
		newComment.setText(shortFilmViewData.getNewCommentText());
		newComment.setShortFilm(shortFilm);
		newComment.setUser(loggedUser);
		newComment.setDate(new Date().getTime());
		commentService.saveComment(newComment);

		shortFilm.getComments().add(newComment);
		shortFilmViewData.setNewCommentText("");

		return String.format("redirect:/shortfilm/%d", shortFilmId);
	}

	@PostMapping(path = "/shortfilm/{shortFilmId}", params = { "deleteComment" })
	public String removeComment(HttpSession session, HttpServletRequest req,
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

		Long commentId = Long.parseLong(req.getParameter("deleteComment"));
		if (result.hasErrors()) {
			redirections.addFlashAttribute("errors", result);
			return String.format("redirect:/shortfilm/%d", shortFilmId);
		}

		try {
			commentService.removeUserComment(commentId, loggedUser);
		} catch (NotFoundException e) {
			result.reject("", e.getMessage());
		} catch (UnauthorizedException e) {
			result.reject("", e.getMessage());
		}

		return String.format("redirect:/shortfilm/%d", shortFilmId);

	}
}
