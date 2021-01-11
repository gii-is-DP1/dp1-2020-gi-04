package io.github.fourfantastics.standby.web;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.model.form.CompanyConfigurationData;
import io.github.fourfantastics.standby.model.form.CompanyProfileData;
import io.github.fourfantastics.standby.model.form.CompanyRegisterData;
import io.github.fourfantastics.standby.model.validator.CompanyConfigurationDataValidator;
import io.github.fourfantastics.standby.model.validator.CompanyRegisterDataValidator;
import io.github.fourfantastics.standby.service.CompanyService;
import io.github.fourfantastics.standby.service.UserService;
import io.github.fourfantastics.standby.service.exception.NotUniqueException;

@Controller
public class CompanyController {
	@Autowired
	CompanyService companyService;
	
	@Autowired
	UserService userService;

	@Autowired
	CompanyRegisterDataValidator companyRegisterDataValidator;

	@Autowired
	CompanyConfigurationDataValidator companyConfigurationDataValidator;

	@GetMapping("/register/company")
	public String registerCompany(HttpSession session, Map<String, Object> model) {
		if (userService.getLoggedUser(session).isPresent()) {
			return "redirect:/";
		}
		
		model.put("companyRegisterData", new CompanyRegisterData());
		return "registerCompany";
	}

	@PostMapping("/register/company")
	public String doRegisterCompany(HttpSession session, @ModelAttribute CompanyRegisterData companyRegisterData,
			BindingResult result) {
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

	@GetMapping("/profile/company/{companyId}")
	public String getProfileView(@PathVariable Long companyId, Map<String, Object> model) {
		User user = userService.getUserById(companyId).orElse(null);
		if (user == null) {
			return "redirect:/";
		}

		if (user.getType() != UserType.Company) {
			return "redirect:/profile/{companyID}";
		}

		Company company = (Company) user;
		CompanyProfileData companyProfileData = CompanyProfileData.fromCompany(company);

		model.put("companyProfileData", companyProfileData);

		return "companyProfile";
	}

	@GetMapping("/account/company")
	public String getManageAccount(HttpSession session, Map<String, Object> model) {
		User user = userService.getLoggedUser(session).orElse(null);
		if (user == null) {
			return "redirect:/login";
		}

		if (user.getType() != UserType.Company) {
			return "redirect:/account";
		}

		Company company = (Company) user;
		model.put("companyConfigurationData", CompanyConfigurationData.fromCompany(company));
		model.put("photoUrl", user.getPhotoUrl());
		return "manageCompanyAccount";
	}

	@PostMapping("/account/company")
	public String doManageAccount(HttpSession session,
			@ModelAttribute("companyConfigurationData") CompanyConfigurationData companyConfigurationData,
			BindingResult result, Map<String, Object> model) {
		User user = userService.getLoggedUser(session).orElse(null);
		if (user == null) {
			return "redirect:/login";
		}

		if (user.getType() != UserType.Company) {
			return "redirect:/manageAccount";
		}

		companyConfigurationDataValidator.validate(companyConfigurationData, result);
		if (result.hasErrors()) {
			return "manageCompanyAccount";
		}

		Company userCompany = (Company) user;
		companyConfigurationData.copyToCompany(userCompany);
		if (companyConfigurationData.getNewPhoto() != null && !companyConfigurationData.getNewPhoto().isEmpty()) {
			try {
				userService.setProfilePicture(userCompany, companyConfigurationData.getNewPhoto());
			} catch (Exception e) {
				result.reject("", e.getMessage());
				model.put("photoUrl", user.getPhotoUrl());
				return "manageCompanyAccount";
			}
		}
		userCompany = (Company) userService.saveUser(userCompany);

		model.put("companyData", companyConfigurationData);
		model.put("photoUrl", user.getPhotoUrl());
		model.put("success", "Configuration has been saved successfully!");
		return "manageCompanyAccount";
	}
}
