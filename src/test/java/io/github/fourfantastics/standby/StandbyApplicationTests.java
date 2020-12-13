package io.github.fourfantastics.standby;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.CompanyRepository;
import io.github.fourfantastics.standby.repository.NotificationRepository;
import io.github.fourfantastics.standby.repository.UserRepository;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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
		company.setBusinessPhone("6125125125");
		company.setCompanyName("El pepe");
		company.setOfficeAddress("Te gacasas");
		company.setTaxIDNumber("123152112L");
		companyRepository.save(company);
		
		Notification a = new Notification();
			a.setEmisionDate(1L);
			a.setText("Vaya");
			a.setUser(company);
		
		
		notificationRepository.save(a);
		for(Company c : companyRepository.findAll()) {
			System.out.println(c.getEmail());
			
		}
		
		for(Notification n : notificationRepository.findAll()) {
			System.out.println(n.getUser().getName());
		}
		
		for(User u : userRepository.findAll()) {
			System.out.println(u.getId());
		}
		
		
		
	}

}
