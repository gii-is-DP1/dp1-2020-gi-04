package io.github.fourfantastics.standby.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import io.github.fourfantastics.standby.repository.ShortFilmRepository;
import io.github.fourfantastics.standby.repository.UserRepository;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class FavouriteIntegrationTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	ShortFilmRepository shortFilmRepository;

	@Test
	void createFavouriteShortFilmRelation() {
		ShortFilm shortFilm = new ShortFilm();
		shortFilm.setName("A new awesome video");
		shortFilm.setFileUrl("file://video.mp4");
		shortFilm.setUploadDate(1L);
		shortFilm.setDescription("Description");

		shortFilm = shortFilmRepository.save(shortFilm);

		Company company = new Company();
		company.setEmail("company@gmail.com");
		company.setName("companyusername");
		company.setPassword("Very strong password");
		company.setPhotoUrl("foto url");
		company.setCreationDate(1L);
		company.setBusinessPhone("6125125125");
		company.setCompanyName("The boring company");
		company.setOfficeAddress("Sillicon Valley");
		company.setTaxIDNumber(1231521512);
		company.getFavouriteShortFilms().add(shortFilm);

		company = userRepository.save(company);
		
		shortFilm = shortFilmRepository.findById(shortFilm.getId()).get();
		assertEquals(1, shortFilm.getFavouriteUsers().size());
		System.out.println(shortFilm.getFavouriteUsers());

	}
}
