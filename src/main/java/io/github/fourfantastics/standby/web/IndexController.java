package io.github.fourfantastics.standby.web;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.service.UserService;

@Controller
public class IndexController {
	@Autowired
	UserService userService;

	@GetMapping("/")
	public String getIndex(HttpSession session, @ModelAttribute User user, Map<String, Object> model) {
		user = userService.getLoggedUser(session).orElse(null);
		if (user == null) {
			return "redirect:/login";
		}

		model.put("user", user);
		return "index";
	}
}
