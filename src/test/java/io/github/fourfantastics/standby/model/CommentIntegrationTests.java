package io.github.fourfantastics.standby.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import io.github.fourfantastics.standby.repository.CommentRepository;
import io.github.fourfantastics.standby.repository.CompanyRepository;
import io.github.fourfantastics.standby.repository.FilmmakerRepository;
import io.github.fourfantastics.standby.repository.ShortFilmRepository;
import io.github.fourfantastics.standby.repository.UserRepository;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class CommentIntegrationTests {
	
	@Autowired
	FilmmakerRepository filmmakerRepository;
	
	@Autowired
	CompanyRepository companyRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CommentRepository commentRepository;
	
	@Autowired
	ShortFilmRepository shortFilmRepository;
	
	@Test
	void contextLoads() {
		/*
		Filmmaker filmmaker=new Filmmaker();
		filmmaker.setCity("Seville");
		filmmaker.setCountry("Spain");
		filmmaker.setCreationDate(1L);
		filmmaker.setEmail("filmmaker@gmail.com");
		filmmaker.setFullname("Javier Guti�rrez");
		filmmaker.setName("javig");
		filmmaker.setPhone("675987432");
		filmmaker.setPhotoUrl("url photo");
		filmmakerRepository.save(filmmaker);
		
		Filmmaker savedFilmmaker = filmmakerRepository.findById(1L).orElse(null);
		assertNotNull(savedFilmmaker);
		*/
		
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
		companyRepository.save(company);

		Company savedCompany = companyRepository.findById(1L).orElse(null);
		assertNotNull(savedCompany);
		
		User savedUser = userRepository.findById(1L).orElse(null);
		assertNotNull(savedUser);
		
		ShortFilm shortFilm = new ShortFilm();
		shortFilm.setName("A new awesome video");
		shortFilm.setFileUrl("file://video.mp4");
		shortFilm.setUploadDate(1L);
		shortFilm.setDescription("Description");
		
		ShortFilm savedFilm = shortFilmRepository.save(shortFilm);

		ShortFilm savedShortFilm = shortFilmRepository.findById(savedFilm.getId()).orElse(null);
		assertNotNull(savedShortFilm);
		
		Comment comment = new Comment();
		
		comment.setUser(company);
		comment.setShortFilm(shortFilm);
		comment.setDate(1L);
		comment.setComment("Breathtaking");
		
		Comment savedComment = commentRepository.save(comment);
		
		Comment returnedComment=  commentRepository.findById(savedComment.getId()).orElse(null);
		assertNotNull(returnedComment);
		
		Comment commonComment =  commentRepository.findByUserAndShortFilm(company, shortFilm).orElse(null);
		assertNotNull(commonComment);
	}
}
