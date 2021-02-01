package io.github.fourfantastics.standby.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.model.form.CompanyConfigurationData;
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
	public String registerCompany(Map<String, Object> model) {
		if (userService.getLoggedUser().isPresent()) {
			return "redirect:/";
		}

		model.put("companyRegisterData", new CompanyRegisterData());
		return "registerCompany";
	}

	@PostMapping("/register/company")
	public String doRegisterCompany(@ModelAttribute CompanyRegisterData companyRegisterData, BindingResult result) {
		if (userService.getLoggedUser().isPresent()) {
			return "redirect:/";
		}

		companyRegisterDataValidator.validate(companyRegisterData, result);
		if (result.hasErrors()) {
			return "registerCompany";
		}

		try {
			companyService.registerCompany(companyRegisterData);
		} catch (NotUniqueException e) {
			result.rejectValue("name", "", e.getMessage());
			return "registerCompany";
		}
		return "redirect:/";
	}

	@GetMapping("/account/company")
	public String getManageAccount(Map<String, Object> model) {
		User user = userService.getLoggedUser().orElse(null);
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
	public String doManageAccount(
			@ModelAttribute("companyConfigurationData") CompanyConfigurationData companyConfigurationData,
			BindingResult result, Map<String, Object> model) {
		User user = userService.getLoggedUser().orElse(null);
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
		companyService.updateCompanyData(userCompany, companyConfigurationData);
		if (companyConfigurationData.getNewPhoto() != null && !companyConfigurationData.getNewPhoto().isEmpty()) {
			try {
				userService.setProfilePicture(userCompany, companyConfigurationData.getNewPhoto());
			} catch (Exception e) {
				result.reject("", e.getMessage());
				model.put("photoUrl", user.getPhotoUrl());
				return "manageCompanyAccount";
			}
		}

		model.put("companyData", companyConfigurationData);
		model.put("photoUrl", user.getPhotoUrl());
		model.put("success", "Configuration has been saved successfully!");
		return "manageCompanyAccount";
	}
}
