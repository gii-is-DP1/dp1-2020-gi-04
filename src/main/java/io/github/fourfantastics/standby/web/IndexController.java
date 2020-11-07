package io.github.fourfantastics.standby.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	@RequestMapping("/")
	public String getIndex() {
		return "index.html";
	}
}
