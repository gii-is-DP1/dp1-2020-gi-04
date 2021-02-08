package io.github.fourfantastic.standby.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Company;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.PrivacyRequest;
import io.github.fourfantastics.standby.model.RequestStateType;
import io.github.fourfantastics.standby.model.form.Pagination;
import io.github.fourfantastics.standby.repository.CompanyRepository;
import io.github.fourfantastics.standby.repository.FilmmakerRepository;
import io.github.fourfantastics.standby.repository.PrivacyRequestRepository;
import io.github.fourfantastics.standby.service.PrivacyRequestService;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class PrivacyRequestRepositoryTest {
	@Autowired
	PrivacyRequestService privacyRequestService;

	@Autowired
	FilmmakerRepository filmmakerRepository;

	@Autowired
	CompanyRepository companyRepository;

	@Autowired
	PrivacyRequestRepository privacyRequestRepository;

	@Test
	void getCountPrivacyRequestOfFilmmakerTest() {
		final Filmmaker filmmaker = new Filmmaker();
		filmmaker.setName("filmmaker111");
		filmmaker.setPassword("password11");
		filmmaker.setEmail("filmmaker11@gmail.com");
		filmmaker.setCity("Seville11");
		filmmaker.setCountry("Spain11");
		filmmaker.setFullname("Filmmaker Díaz García11");
		filmmaker.setPhone("675987999");
		filmmaker.setCreationDate(20L);
		this.filmmakerRepository.save(filmmaker);

		final Company company = new Company();
		company.setName("company4");
		company.setPassword("password");
		company.setEmail("business@company4.com");
		company.setBusinessPhone("612347878");
		company.setCompanyName("Company Studiossssssssssssss");
		company.setOfficeAddress("Calle Manzana 456");
		company.setTaxIDNumber("123-00-1234567");
		company.setCreationDate(79L);
		this.companyRepository.save(company);

		final PrivacyRequest request = new PrivacyRequest();
		request.setFilmmaker(filmmaker);
		request.setCompany(company);
		request.setRequestDate(3L);
		request.setRequestState(RequestStateType.PENDING);

		this.privacyRequestRepository.save(request);

		assertEquals(this.privacyRequestService.getCountPrivacyRequestByFilmmaker(filmmaker.getId()), 1);
	}

	@Test
	void getPrivacyRequestOfFilmmakerTest() {
		final Filmmaker filmmaker = new Filmmaker();
		filmmaker.setName("filmmaker111");
		filmmaker.setPassword("password11");
		filmmaker.setEmail("filmmaker11@gmail.com");
		filmmaker.setCity("Seville11");
		filmmaker.setCountry("Spain11");
		filmmaker.setFullname("Filmmaker Díaz García11");
		filmmaker.setPhone("675987999");
		filmmaker.setCreationDate(20L);
		this.filmmakerRepository.save(filmmaker);

		final Company company = new Company();
		company.setName("company4");
		company.setPassword("password");
		company.setEmail("business@company4.com");
		company.setBusinessPhone("612347878");
		company.setCompanyName("Company Studiossssssssssssss");
		company.setOfficeAddress("Calle Manzana 456");
		company.setTaxIDNumber("123-00-1234567");
		company.setCreationDate(79L);
		this.companyRepository.save(company);

		final PrivacyRequest request = new PrivacyRequest();
		request.setFilmmaker(filmmaker);
		request.setCompany(company);
		request.setRequestDate(3L);
		request.setRequestState(RequestStateType.PENDING);

		List<PrivacyRequest> requests = new ArrayList<>();
		requests.add(request);

		this.privacyRequestRepository.save(request);

		assertEquals(this.privacyRequestService
				.getPrivacyRequestByFilmmaker(filmmaker.getId(), Pagination.empty().getPageRequest()).getContent(),
				requests);
	}
}
