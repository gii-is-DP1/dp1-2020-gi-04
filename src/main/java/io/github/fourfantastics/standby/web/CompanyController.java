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
import io.github.fourfantastics.standby.model.form.CompanyRegisterData;
import io.github.fourfantastics.standby.model.validator.CompanyRegisterDataValidator;
import io.github.fourfantastics.standby.service.CompanyService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.NotUniqueException;

@Controller
public class CompanyController {
	@Autowired
	UserService userService;
	
	@Autowired
	CompanyService companyService;
	
	@Autowired
	CompanyRegisterDataValidator companyRegisterDataValidator;

	@GetMapping("/register/company")
	public String registerCompany(HttpSession session, Map<String, Object> model) {
		if (userService.getLoggedUser(session).isPresent()) {
			return "redirect:/";
		}
		model.put("companyRegisterData", new CompanyRegisterData());
		return "registerCompany";
	}

	@PostMapping("/register/company")
	public String doRegisterCompany(HttpSession session,
			@ModelAttribute("companyRegisterData") CompanyRegisterData companyRegisterData, BindingResult result,
			Map<String, Object> model) {
		if (userService.getLoggedUser(session).isPresent()) {
			return "redirect:/";
		}

		companyRegisterDataValidator.validate(companyRegisterData, result);
		if (result.hasErrors()) {
			return "registerCompany";
		}

		try {
			Company company = companyService.registerCompany(companyRegisterData);
			userService.logIn(session, company);
		} catch (NotUniqueException e) {
			result.rejectValue("name", "", e.getMessage());
			return "registerCompany";
		}
		return "redirect:/";
	}
}
