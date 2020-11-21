package io.github.fourfantastics.standby;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.CompanyRepository;
import io.github.fourfantastics.standby.repository.NotificationRepository;
import io.github.fourfantastics.standby.repository.UserRepository;

@SpringBootTest
class StandbyApplicationTests {

	@Autowired
	CompanyRepository companyRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	NotificationRepository notificationRepository;
	
	@Test
	void contextLoads() {
		Company company = new Company();
		company.setEmail("asd@gmail.com");
		company.setName("Eltete");
		company.setPassword("El nano el makina");
		company.setPhotoUrl("aaaaaaa ");
		company.setCreationDate(1L);
		company.setBussinessPhone("6125125125");
		company.setCompanyName("El pepe");
		company.setOfficeAddress("Te gacasas");
		company.setTaxIDNumber(1231521512);
		company.setNotifications(new ArrayList<Notification>());
		companyRepository.save(company);
		
		
		Notification a = new Notification();
			a.setEmisionDate(1L);
			a.setText("Vaya");
			a.setUser(company);
		
		
		notificationRepository.save(a);
		for(Company c : companyRepository.findAll()) {
			System.out.println(c.getEmail());
			System.out.println(notificationRepository.findByUser(c).get(0).getText());
			
		}
		
		for(Notification n : notificationRepository.findAll()) {
			System.out.println(n.getUser().getName());
		}
		
		for(User u : userRepository.findAll()) {
			System.out.println(u.getId());
		}
		
		
		
	}

}
