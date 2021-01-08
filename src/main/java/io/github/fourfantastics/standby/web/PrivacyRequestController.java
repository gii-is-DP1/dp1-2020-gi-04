package io.github.fourfantastics.standby.web;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.service.CompanyService;
import io.github.fourfantastics.standby.service.PrivacyRequestService;
import io.github.fourfantastics.standby.service.UserService;

@Controller
public class PrivacyRequestController {
	@Autowired
	UserService userService;

	@Autowired
	CompanyService companyService;

	@Autowired
	PrivacyRequestService privacyRequestService;
	
	@PostMapping("/privacyrequest/{filmmakerID}")
	public String sendPrivacyRequest(HttpSession session ,@PathVariable("filmmakerID") Long userID){
		User sender = userService.getLoggedUser(session).orElse(null);
		if (sender == null) {
			return "redirect:/login";
		}
		if (sender.getType() != UserType.Company) {
			System.out.println("Only companies are allowed to send PrivacyRequest");
			return String.format("redirect:/profile/%d", userID );
		}
		
		Optional<User> optional = userService.getUserById(userID);
		if(!optional.isPresent()) {
			System.out.println("A quien le estas mandando la request crack");
			return "redirect:/";
		}
		User receiver = optional.get();
		if(receiver.getType() != UserType.Filmmaker) {
			System.out.println("PrivacyRequest can only be sent to filmmakers");
			return String.format("redirect:/profile/%d", userID );
		}
		Company company = (Company) sender;
		if(!company.getSentRequests().stream().anyMatch(x -> x.getFilmmaker().getName().equals(receiver.getName()))){
			System.out.println("PrivacyRequest can only be sent to filmmakers once");
			return String.format("redirect:/profile/%d", userID );
		}
		Filmmaker filmmaker = (Filmmaker) receiver;
		privacyRequestService.sendPrivacyRequest(company, filmmaker);
		return String.format("redirect:/profile/%d", userID );
	}
}
