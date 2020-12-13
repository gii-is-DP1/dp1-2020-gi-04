package io.github.fourfantastics.standby.web;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.service.UserService;

@Controller
public class IndexController {
	@Autowired
	UserService userService;
	
	@RequestMapping("/")
	public String getIndex(HttpSession session, Map<String, Object> model) {
		Optional<User> optionalUser = userService.getLoggedUser(session);

		if (!optionalUser.isPresent()) {
			return "redirect:/login";
		}
		
		User user = optionalUser.get();
		model.put("user", user);
		return "index";
	}
}
