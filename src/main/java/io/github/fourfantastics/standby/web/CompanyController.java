package io.github.fourfantastics.standby.web;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.service.CompanyService;
import io.github.fourfantastics.standby.service.UserService;

@Controller
public class CompanyController {
	@Autowired
	UserService userService;
	@Autowired
	CompanyService companyService;
	
	@GetMapping("/registerCompany")
	public String registerC(HttpSession session, Map<String,Object> model){
		if (userService.isLogged(session)) {
			return "redirect:/";
		}
		Company company = new Company();
		model.put("company", company);
		return "registerCompany";
	}
	
	@PostMapping("/resgisterCompany")
	public String doRegisterC(HttpSession session, Bindi	ngResult result, Map<String, Object> model) {
		if (userService.isLogged(session)) {
			return "redirect:/";
		}
		if(result.hasErrors()) {
			return "registerCompany";
		}
		Company company;
		
		
		
		
		
}
}