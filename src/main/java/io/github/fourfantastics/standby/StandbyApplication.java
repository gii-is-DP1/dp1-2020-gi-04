package io.github.fourfantastics.standby;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.service.UserService;

@SpringBootApplication
public class StandbyApplication {
	public static void main(String[] args) {
		SpringApplication.run(StandbyApplication.class, args);
	}
	
	@Component
	public class CommandLineAppStartupRunner implements CommandLineRunner {
		@Autowired
		UserService userService;
		
	    @Override
	    public void run(String... args) throws Exception {
	    	Filmmaker filmmaker = new Filmmaker();
	    	filmmaker.setName("guillex7");
	    	filmmaker.setPassword("prueba1234");
	    	filmmaker.setEmail("guillermox7@gmail.com");
	    	filmmaker.setType(UserType.Filmmaker);
	    	filmmaker.setPhotoUrl("url photo");
			filmmaker.setCity("Seville");
			filmmaker.setCountry("Spain");
			filmmaker.setFullname("Guillermo Diz");
			filmmaker.setPhone("675987432");
			userService.register(filmmaker);
			
			Company company = new Company();
			company.setName("honeymoneystudios");
			company.setPassword("yeahmaincra");
			company.setEmail("business@honeymoney.com");
			company.setPhotoUrl("url photo");
			company.setBusinessPhone("612345678");
			company.setCompanyName("Honey Money Studios");
			company.setOfficeAddress("Calle Manzana 4");
			company.setTaxIDNumber(1231521512);
			userService.register(company);
	    }
	}
}
