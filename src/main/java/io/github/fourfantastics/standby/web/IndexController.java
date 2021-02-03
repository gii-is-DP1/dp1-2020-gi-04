package io.github.fourfantastics.standby.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.service.UserService;

@Controller
public class IndexController {
	@Autowired
	UserService userService;

	@RequestMapping
	public String getIndex(@ModelAttribute User user, Map<String, Object> model) {
		User loggedUser = userService.getLoggedUser().orElse(null);
		if (loggedUser == null) {
			return "redirect:/login";
		}
		model.put("user", loggedUser);
		return "index";
	}

}
