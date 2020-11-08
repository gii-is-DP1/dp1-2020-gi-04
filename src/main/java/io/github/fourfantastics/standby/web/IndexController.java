package io.github.fourfantastics.standby.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

public class IndexController {
	public String getIndex() {
		System.out.println("cargando index");
		return "index.html";
	}
}
