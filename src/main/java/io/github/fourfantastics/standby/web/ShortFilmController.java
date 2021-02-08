package io.github.fourfantastics.standby.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.fourfantastics.standby.model.Comment;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.model.form.RoleData;
import io.github.fourfantastics.standby.model.form.ShortFilmEditData;
import io.github.fourfantastics.standby.model.form.ShortFilmUploadData;
import io.github.fourfantastics.standby.model.form.ShortFilmViewData;
import io.github.fourfantastics.standby.model.validator.ShortFilmEditDataValidator;
import io.github.fourfantastics.standby.model.validator.ShortFilmUploadDataValidator;
import io.github.fourfantastics.standby.model.validator.ShortFilmViewDataValidator;
import io.github.fourfantastics.standby.service.CommentService;
import io.github.fourfantastics.standby.service.FavouriteService;
import io.github.fourfantastics.standby.service.RatingService;
import io.github.fourfantastics.standby.service.RoleService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.SubscriptionService;
import io.github.fourfantastics.standby.service.TagService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.InvalidExtensionException;
import io.github.fourfantastics.standby.service.exception.TooBigException;

@Controller
public class ShortFilmController {
	@Autowired
	ShortFilmService shortFilmService;

	@Autowired
	UserService userService;

	@Autowired
	TagService tagService;

	@Autowired
	RoleService roleService;

	@Autowired
	RatingService ratingService;

	@Autowired
	CommentService commentService;
	
	@Autowired
	SubscriptionService subscriptionService;
	
	@Autowired
	FavouriteService favouriteService;

	@Autowired
	ShortFilmUploadDataValidator shortFilmUploadDataValidator;

	@Autowired
	ShortFilmEditDataValidator shortFilmEditDataValidator;

	@Autowired
	ShortFilmViewDataValidator shortFilmViewDataValidator;

	@GetMapping("/upload")
	public String getUploadView(@ModelAttribute ShortFilmUploadData shortFilmUploadData, Map<String, Object> model) {
		User loggedUser = userService.getLoggedUser().orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}
		if (loggedUser.getType() != UserType.Filmmaker) {
			return "redirect:/";
		}

		model.put("shortFilmUploadData", new ShortFilmUploadData());
		return "uploadShortFilm";
	}

	@PostMapping(value = "upload", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Object uploadShortFilm(@ModelAttribute ShortFilmUploadData shortFilmUploadData,
			BindingResult result) {
		Map<String, Object> res = new HashMap<String, Object>();

		User loggedUser = userService.getLoggedUser().orElse(null);
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
			fieldErrors.put("file", e.getMessage());
			res.put("status", 400);
			res.put("message", "");
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

	@RequestMapping("/shortfilm/{shortFilmId}")
	public String getShortfilmView(@PathVariable Long shortFilmId, @ModelAttribute ShortFilmViewData shortFilmViewData,
			BindingResult result, Map<String, Object> model) {
		ShortFilm shortFilm = shortFilmService.getShortFilmById(shortFilmId).orElse(null);
		if (shortFilm == null) {
			return "redirect:/";
		}

		if (shortFilmViewData.getLoaded() == null) {
			shortFilmService.updateViewCount(shortFilm, 1);
		}
		
		shortFilmViewData.setShortFilm(shortFilm);
		
		Page<Comment> page = commentService
				.getCommentsByShortFilm(shortFilm,
						shortFilmViewData.getCommentPagination().getPageRequest(Sort.by("date").descending()));
		shortFilmViewData.getCommentPagination().setTotalElements((int) page.getTotalElements());
		shortFilmViewData.setComments(page.getContent());

		User loggedUser = userService.getLoggedUser().orElse(null);
		if (loggedUser != null) {
			shortFilmViewData.setHasFavourite(favouriteService.hasFavouriteShortFilm(shortFilm, loggedUser));
			shortFilmViewData.setWatcherId(loggedUser.getId());
			shortFilmViewData.setWatcherName(loggedUser.getName());
			shortFilmViewData.setWatcherPhotoUrl(loggedUser.getPhotoUrl());
		} else {
			shortFilmViewData.setHasFavourite(false);
		}

		Double meanRating = shortFilm.getRatingAverage();
		shortFilmViewData.setMeanRating(meanRating);
		shortFilmViewData.setUserRating(ratingService.getRatingByUserAndShortFilm(loggedUser, shortFilm));
		
		shortFilmViewData.setFollowerCount(subscriptionService.getFollowerCount(shortFilm.getUploader()));

		model.put("shortFilmViewData", shortFilmViewData);
		if (model.containsKey("errors")) {
			result.addAllErrors((BindingResult) model.get("errors"));
		}

		return "viewShortFilm";
	}

	@RequestMapping("/shortfilm/{shortFilmId}/edit")
	public String getEditView(@PathVariable Long shortFilmId, @ModelAttribute ShortFilmEditData shortFilmEditData,
			BindingResult bindingResult, Map<String, Object> model) {
		User loggedUser = userService.getLoggedUser().orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}

		ShortFilm shortFilm = shortFilmService.getShortFilmById(shortFilmId).orElse(null);
		if (shortFilm == null || !shortFilm.getUploader().equals(loggedUser)) {
			return "redirect:/";
		}

		if (shortFilmEditData.getTitle() == null) {
			model.put("shortFilmEditData", ShortFilmEditData.fromShortFilm(shortFilm));
			shortFilmEditData = ShortFilmEditData.fromShortFilm(shortFilm);
		}
		return "editShortFilm";
	}

	@PostMapping(path = "/shortfilm/{shortFilmId}/edit", params = { "addTag" })
	public String addTagToFilm(@PathVariable Long shortFilmId, @ModelAttribute ShortFilmEditData shortFilmEditData,
			BindingResult result) {
		User loggedUser = userService.getLoggedUser().orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}

		ShortFilm shortFilm = shortFilmService.getShortFilmById(shortFilmId).orElse(null);
		if (shortFilm == null || !shortFilm.getUploader().equals((Filmmaker) loggedUser)) {
			return "redirect:/";
		}

		shortFilmEditDataValidator.setValidationTargets(false, true, false);
		shortFilmEditDataValidator.validate(shortFilmEditData, result);
		if (result.hasErrors()) {
			return "editShortFilm";
		}

		if (shortFilmEditData.getTags().contains(shortFilmEditData.getNewTagName())) {
			result.rejectValue("newTagName", "", "Tag is already added!");
		} else {
			shortFilmEditData.getTags().add(shortFilmEditData.getNewTagName());
			shortFilmEditData.setNewTagName("");
		}

		return "editShortFilm";
	}

	@PostMapping(path = "/shortfilm/{shortFilmId}/edit", params = { "removeTag" })
	public String removeTagFromFilm(@RequestParam String removeTag, @PathVariable Long shortFilmId,
			@ModelAttribute ShortFilmEditData shortFilmEditData, BindingResult result) {
		User loggedUser = userService.getLoggedUser().orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}

		ShortFilm shortFilm = shortFilmService.getShortFilmById(shortFilmId).orElse(null);
		if (shortFilm == null || !shortFilm.getUploader().equals((Filmmaker) loggedUser)) {
			return "redirect:/";
		}

		shortFilmEditData.getTags().remove(removeTag);

		return "editShortFilm";
	}

	@PostMapping(path = "/shortfilm/{shortFilmId}/edit", params = { "addRole" })
	public String addRoleToFilm(@PathVariable Long shortFilmId, @ModelAttribute ShortFilmEditData shortFilmEditData,
			BindingResult result) {
		User loggedUser = userService.getLoggedUser().orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}

		ShortFilm shortFilm = shortFilmService.getShortFilmById(shortFilmId).orElse(null);
		if (shortFilm == null || !shortFilm.getUploader().equals((Filmmaker) loggedUser)) {
			return "redirect:/";
		}

		shortFilmEditDataValidator.setValidationTargets(false, false, true);
		shortFilmEditDataValidator.validate(shortFilmEditData, result);
		if (result.hasErrors()) {
			return "editShortFilm";
		}

		User roleUser = userService.getUserByName(shortFilmEditData.getNewRoleFilmmaker()).orElse(null);
		if (roleUser == null) {
			result.rejectValue("newRoleFilmmaker", "", "Filmmaker's username doesn't exist!");
			return "editShortFilm";
		} else if (!roleUser.getType().equals(UserType.Filmmaker)) {
			result.rejectValue("newRoleFilmmaker", "", "That user is not a filmmaker!");
			return "editShortFilm";
		}

		RoleData newPair = RoleData.of(shortFilmEditData.getNewRoleFilmmaker(), shortFilmEditData.getNewRoleType());
		for (RoleData pair : shortFilmEditData.getRoles()) {
			if (newPair.equals(pair)) {
				result.rejectValue("newRoleFilmmaker", "", "This association is already added!");
				return "editShortFilm";
			}
		}

		shortFilmEditData.getRoles().add(newPair);
		shortFilmEditData.getRolePagination().setTotalElements(shortFilmEditData.getRoles().size());

		return "editShortFilm";
	}

	@PostMapping(path = "/shortfilm/{shortFilmId}/edit", params = { "removeRole" })
	public String removeRoleFromFilm(@RequestParam Integer removeRole, @PathVariable Long shortFilmId,
			@ModelAttribute ShortFilmEditData shortFilmEditData, BindingResult result) {
		User loggedUser = userService.getLoggedUser().orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}

		ShortFilm shortFilm = shortFilmService.getShortFilmById(shortFilmId).orElse(null);
		if (shortFilm == null || !shortFilm.getUploader().equals((Filmmaker) loggedUser)) {
			return "redirect:/";
		}

		shortFilmEditData.getRoles().remove(removeRole.intValue());
		shortFilmEditData.getRolePagination().setTotalElements(shortFilmEditData.getRoles().size());

		return "editShortFilm";
	}

	@PostMapping(path = "/shortfilm/{shortFilmId}/edit", params = { "applyChanges" })
	public String applyChangesToFilm(@PathVariable Long shortFilmId,
			@ModelAttribute ShortFilmEditData shortFilmEditData, BindingResult result, Map<String, Object> model) {
		User loggedUser = userService.getLoggedUser().orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}

		ShortFilm shortFilm = shortFilmService.getShortFilmById(shortFilmId).orElse(null);
		if (shortFilm == null || !shortFilm.getUploader().equals((Filmmaker) loggedUser)) {
			return "redirect:/";
		}

		shortFilmEditDataValidator.setValidationTargets(true, false, false);
		shortFilmEditDataValidator.validate(shortFilmEditData, result);
		if (result.hasErrors()) {
			return "editShortFilm";
		}

		if (shortFilmEditData.getNewThumbnailFile() != null && !shortFilmEditData.getNewThumbnailFile().isEmpty()) {
			try {
				shortFilmService.uploadThumbnail(shortFilm, shortFilmEditData.getNewThumbnailFile());
				shortFilmEditData.setThumbnailUrl(shortFilm.getThumbnailUrl());
			} catch (Exception e) {
				result.rejectValue("newThumbnailFile", "", e.getMessage());
				return "editShortFilm";
			}
		}

		shortFilmService.updateShortFilmMetadata(shortFilm, shortFilmEditData.getTitle(),
				shortFilmEditData.getDescription());
		tagService.tagShortFilm(shortFilmEditData.getTags(), shortFilm);
		roleService.setRolesOfShortFilm(shortFilmEditData.getRoles(), shortFilm);

		model.put("success", "Short film information updated successfully!");
		return "editShortFilm";
	}

}
