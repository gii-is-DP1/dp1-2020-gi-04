package io.github.fourfantastics.standby.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.Role;
import io.github.fourfantastics.standby.model.RoleType;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.Tag;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.model.form.ShortFilmEditData;
import io.github.fourfantastics.standby.model.form.ShortFilmUploadData;
import io.github.fourfantastics.standby.model.validator.ShortFilmEditDataValidator;
import io.github.fourfantastics.standby.model.validator.ShortFilmUploadDataValidator;
import io.github.fourfantastics.standby.service.RoleService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.TagService;
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
	TagService tagService;

	@Autowired
	RoleService roleService;

	@Autowired
	ShortFilmUploadDataValidator shortFilmUploadDataValidator;

	@Autowired
	ShortFilmEditDataValidator shortFilmEditDataValidator;

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
	public String getEditView(HttpSession session, @PathVariable("shortFilmId") Long shortFilmId,
			Map<String, Object> model) {
		User loggedUser = userService.getLoggedUser(session).orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}

		ShortFilm shortFilm = shortFilmService.getShortFilmById(shortFilmId).orElse(null);
		if (shortFilm == null || !shortFilm.getUploader().equals((Filmmaker) loggedUser)) {
			return "redirect:/";
		}

		model.put("shortFilmId", shortFilmId);
		model.put("shortFilmEditData", ShortFilmEditData.fromShortFilm(shortFilm));
		return "editShortFilm";
	}

	@PostMapping(path = "/shortfilm/{shortFilmId}/edit", params = { "addTag" })
	public String addTagToFilm(HttpSession session, @PathVariable("shortFilmId") Long shortFilmId,
			@ModelAttribute("shortFilmEditData") ShortFilmEditData shortFilmEditData, BindingResult result,
			Map<String, Object> model, HttpServletRequest req) {
		User loggedUser = userService.getLoggedUser(session).orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}

		ShortFilm shortFilm = shortFilmService.getShortFilmById(shortFilmId).orElse(null);
		if (shortFilm == null || !shortFilm.getUploader().equals((Filmmaker) loggedUser)) {
			return "redirect:/";
		}

		if (shortFilmEditData.getTags() == null) {
			shortFilmEditData.setTags(new ArrayList<String>());
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
	public String removeTagFromFilm(HttpSession session, @PathVariable("shortFilmId") Long shortFilmId,
			@ModelAttribute("shortFilmEditData") ShortFilmEditData shortFilmEditData, BindingResult result,
			Map<String, Object> model, HttpServletRequest req) {
		User loggedUser = userService.getLoggedUser(session).orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}

		ShortFilm shortFilm = shortFilmService.getShortFilmById(shortFilmId).orElse(null);
		if (shortFilm == null || !shortFilm.getUploader().equals((Filmmaker) loggedUser)) {
			return "redirect:/";
		}

		if (shortFilmEditData.getTags() != null) {
			shortFilmEditData.getTags().remove(req.getParameter("removeTag"));
		}

		return "editShortFilm";
	}

	@PostMapping(path = "/shortfilm/{shortFilmId}/edit", params = { "addRole" })
	public String addRoleToFilm(HttpSession session, @PathVariable("shortFilmId") Long shortFilmId,
			@ModelAttribute("shortFilmEditData") ShortFilmEditData shortFilmEditData, BindingResult result,
			Map<String, Object> model, HttpServletRequest req) {
		User loggedUser = userService.getLoggedUser(session).orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}

		ShortFilm shortFilm = shortFilmService.getShortFilmById(shortFilmId).orElse(null);
		if (shortFilm == null || !shortFilm.getUploader().equals((Filmmaker) loggedUser)) {
			return "redirect:/";
		}

		if (shortFilmEditData.getRoles() == null) {
			shortFilmEditData.setRoles(new HashMap<String, RoleType>());
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
			result.rejectValue("newRoleFilmmaker",  "", "That user is not a filmmaker!");
			return "editShortFilm";
		}

		shortFilmEditData.getRoles().put(shortFilmEditData.getNewRoleFilmmaker(), shortFilmEditData.getNewRoleType());
		shortFilmEditData.setNewRoleFilmmaker("");

		return "editShortFilm";
	}

	@PostMapping(path = "/shortfilm/{shortFilmId}/edit", params = { "removeRole" })
	public String removeRoleFromFilm(HttpSession session, @PathVariable("shortFilmId") Long shortFilmId,
			@ModelAttribute("shortFilmEditData") ShortFilmEditData shortFilmEditData, BindingResult result,
			Map<String, Object> model, HttpServletRequest req) {
		User loggedUser = userService.getLoggedUser(session).orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}

		ShortFilm shortFilm = shortFilmService.getShortFilmById(shortFilmId).orElse(null);
		if (shortFilm == null || !shortFilm.getUploader().equals((Filmmaker) loggedUser)) {
			return "redirect:/";
		}

		if (shortFilmEditData.getRoles() != null) {
			shortFilmEditData.getRoles().remove(req.getParameter("removeRole"));
		}

		return "editShortFilm";
	}

	@PostMapping("/shortfilm/{shortFilmId}/edit")
	public String applyChangesToFilm(HttpSession session, @PathVariable("shortFilmId") Long shortFilmId,
			@ModelAttribute("shortFilmEditData") ShortFilmEditData shortFilmEditData, BindingResult result,
			Map<String, Object> model) {
		User loggedUser = userService.getLoggedUser(session).orElse(null);
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

		shortFilmEditData.copyToShortFilm(shortFilm);
		
		shortFilm.getTags().clear();
		if (shortFilmEditData.getTags() != null) {
			for (String tagName : shortFilmEditData.getTags()) {
				Tag newTag = tagService.getTagByName(tagName).orElse(null);
				if (newTag == null) {
					newTag = new Tag();
					newTag.setName(tagName);
					tagService.saveTag(newTag);
				}
				shortFilm.getTags().add(newTag);
			}
		}

		for (Role role : shortFilm.getRoles()) {
			roleService.deleteRole(role);
		}
		shortFilm.getRoles().clear();
		if (shortFilmEditData.getRoles() != null) {
			for (Entry<String, RoleType> entry : shortFilmEditData.getRoles().entrySet()) {
				User roleUser = userService.getUserByName(entry.getKey()).orElse(null);
				if (roleUser == null || !roleUser.getType().equals(UserType.Filmmaker)) {
					continue;
				}
				Role newRole = new Role();
				newRole.setFilmmaker((Filmmaker) roleUser);
				newRole.setRole(entry.getValue());
				newRole.setShortfilm(shortFilm);
				roleService.saveRole(newRole);
			}
		}

		shortFilmService.save(shortFilm);
		return "editShortFilm";
	}
}
