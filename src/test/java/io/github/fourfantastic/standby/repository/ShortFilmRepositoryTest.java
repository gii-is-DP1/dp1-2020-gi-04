package io.github.fourfantastic.standby.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.filters.ShortFilmSpecifications;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.Tag;
import io.github.fourfantastics.standby.model.form.DateFilter;
import io.github.fourfantastics.standby.model.form.Pagination;
import io.github.fourfantastics.standby.model.form.SearchData;
import io.github.fourfantastics.standby.model.form.SortType;
import io.github.fourfantastics.standby.repository.FilmmakerRepository;
import io.github.fourfantastics.standby.repository.ShortFilmRepository;
import io.github.fourfantastics.standby.repository.TagRepository;
import io.github.fourfantastics.standby.service.RatingService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.exception.BadRequestException;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ShortFilmRepositoryTest {

	@Autowired
	ShortFilmRepository shortFilmRepository;

	@Autowired
	FilmmakerRepository filmmakerRepository;

	@Autowired
	TagRepository tagRepository;

	@Autowired
	RatingService ratingService;
	
	@Autowired
	ShortFilmService shortFilmService;

	@Test
	void getShortFilmsByName() {

		Filmmaker filmmaker = new Filmmaker();
		filmmaker.setName("filmmaker1");
		filmmaker.setPassword("password");
		filmmaker.setEmail("filmmaker@gmail.com");
		filmmaker.setPhotoUrl(null);
		filmmaker.setCity("Seville");
		filmmaker.setCountry("Spain");
		filmmaker.setFullname("Filmmaker Díaz García");
		filmmaker.setPhone("675987432");
		filmmaker.setCreationDate(new Date().getTime());
		filmmakerRepository.save(filmmaker);

		Filmmaker filmmaker2 = new Filmmaker();
		filmmaker2.setName("filmmaker2");
		filmmaker2.setPassword("password");
		filmmaker2.setEmail("filmmaker2@gmail.com");
		filmmaker2.setPhotoUrl(null);
		filmmaker2.setCity("Seville");
		filmmaker2.setCountry("Spain");
		filmmaker2.setFullname("Filmmaker Díaz Garcíaa");
		filmmaker2.setPhone("675987432");
		filmmaker2.setCreationDate(new Date().getTime());
		filmmakerRepository.save(filmmaker2);

		ShortFilm shortFilm = new ShortFilm();
		shortFilm.setTitle("Test film");
		shortFilm.setVideoUrl("example.mp4");
		shortFilm.setUploadDate(8L);
		shortFilm.setDescription("");
		shortFilm.setUploader(filmmaker);
		shortFilm.setViewCount(3L);
		shortFilmRepository.save(shortFilm);

		ShortFilm shortFilm2 = new ShortFilm();
		shortFilm2.setTitle("new film");
		shortFilm2.setVideoUrl("example.mp4");
		shortFilm2.setUploadDate(10L);
		shortFilm2.setDescription("");
		shortFilm2.setUploader(filmmaker2);
		shortFilm2.setViewCount(5L);
		shortFilmRepository.save(shortFilm2);

		ShortFilm shortFilm3 = new ShortFilm();
		shortFilm3.setTitle("test");
		shortFilm3.setVideoUrl("example.mp4");
		shortFilm3.setUploadDate(12L);
		shortFilm3.setDescription("");
		shortFilm3.setUploader(filmmaker2);
		shortFilm3.setViewCount(5L);
		shortFilmRepository.save(shortFilm3);

		Specification<ShortFilm> thatHasTitle = ShortFilmSpecifications.hasTitle("%film%");
		Specification<ShortFilm> thatHasUploader = ShortFilmSpecifications.hasUploader(filmmaker);

		assertEquals(2, shortFilmRepository.findAll(thatHasTitle).size());
		assertEquals(1, shortFilmRepository.findAll(thatHasUploader).size());

		Tag tag = new Tag();
		tag.setName("comedy");
		tag.getMovies().add(shortFilm2);

		tagRepository.save(tag);

		Tag tag2 = new Tag();
		tag2.setName("eed");
		tag2.getMovies().add(shortFilm);
		tag2.getMovies().add(shortFilm2);

		tagRepository.save(tag2);

		Set<String> tags = new HashSet<String>();
		tags.add("comedy");
		tags.add("eed");

		Specification<ShortFilm> thatHasTags = ShortFilmSpecifications.hasTags(tags);
		Specification<ShortFilm> betweenDates = ShortFilmSpecifications.betweenDates(5L, 11L);

		assertEquals(2, shortFilmRepository.findAll(thatHasTags).size());
		assertEquals(2, shortFilmRepository.findAll(betweenDates).size());

		Specification<ShortFilm> sortByViews = ShortFilmSpecifications.sortByViews(false);

		List<ShortFilm> shortFilms = shortFilmRepository
				.findAll(Specification.where(thatHasTags).and(sortByViews).and(betweenDates));

		assertEquals(2, shortFilms.size());
		assertEquals(shortFilm2, shortFilms.get(0));
		assertEquals(shortFilm, shortFilms.get(1));

		ratingService.rateShortFilm(shortFilm, filmmaker, 3);
		ratingService.rateShortFilm(shortFilm, filmmaker2, 5);

		ratingService.rateShortFilm(shortFilm2, filmmaker, 6);
		ratingService.rateShortFilm(shortFilm2, filmmaker2, 8);

		List<ShortFilm> shortFilms2 = shortFilmRepository.findAll(ShortFilmSpecifications.sortByRating(false));

		assertEquals(3, shortFilms2.size());
		assertEquals(shortFilm2, shortFilms2.get(0));
		assertEquals(shortFilm, shortFilms2.get(1));
		assertEquals(shortFilm3, shortFilms2.get(2));

	}

	@Test
	void searchFilters() {

		Long now = Instant.now().toEpochMilli();
		Filmmaker filmmaker = new Filmmaker();
		filmmaker.setName("filmmaker1");
		filmmaker.setPassword("password");
		filmmaker.setEmail("filmmaker@gmail.com");
		filmmaker.setPhotoUrl(null);
		filmmaker.setCity("Seville");
		filmmaker.setCountry("Spain");
		filmmaker.setFullname("Filmmaker Díaz García");
		filmmaker.setPhone("675987432");
		filmmaker.setCreationDate(new Date().getTime());
		filmmakerRepository.save(filmmaker);

		Filmmaker filmmaker2 = new Filmmaker();
		filmmaker2.setName("filmmaker2");
		filmmaker2.setPassword("password");
		filmmaker2.setEmail("filmmaker2@gmail.com");
		filmmaker2.setPhotoUrl(null);
		filmmaker2.setCity("Seville");
		filmmaker2.setCountry("Spain");
		filmmaker2.setFullname("Filmmaker Díaz Garcíaa");
		filmmaker2.setPhone("675987432");
		filmmaker2.setCreationDate(new Date().getTime());
		filmmakerRepository.save(filmmaker2);

		ShortFilm shortFilm = new ShortFilm();
		shortFilm.setTitle("Test film");
		shortFilm.setVideoUrl("example.mp4");
		shortFilm.setUploadDate(now - 2L);
		shortFilm.setDescription("");
		shortFilm.setUploader(filmmaker);
		shortFilm.setViewCount(3L);
		shortFilmRepository.save(shortFilm);

		ShortFilm shortFilm2 = new ShortFilm();
		shortFilm2.setTitle("new film");
		shortFilm2.setVideoUrl("example.mp4");
		shortFilm2.setUploadDate(now - 23L);
		shortFilm2.setDescription("");
		shortFilm2.setUploader(filmmaker2);
		shortFilm2.setViewCount(5L);
		shortFilmRepository.save(shortFilm2);

		ShortFilm shortFilm3 = new ShortFilm();
		shortFilm3.setTitle("test new");
		shortFilm3.setVideoUrl("example.mp4");
		shortFilm3.setUploadDate(now - 5L);
		shortFilm3.setDescription("");
		shortFilm3.setUploader(filmmaker2);
		shortFilm3.setViewCount(5L);
		shortFilmRepository.save(shortFilm3);
		
		ShortFilm shortFilm4 = new ShortFilm();
		shortFilm4.setTitle("test new film");
		shortFilm4.setVideoUrl("example.mp4");
		shortFilm4.setUploadDate(now - 7L);
		shortFilm4.setDescription("");
		shortFilm4.setUploader(filmmaker);
		shortFilm4.setViewCount(5L);
		shortFilmRepository.save(shortFilm4);
		
		Tag tag = new Tag();
		tag.setName("comedy");
		tag.getMovies().add(shortFilm2);

		tagRepository.save(tag);

		Tag tag2 = new Tag();
		tag2.setName("eed");
		tag2.getMovies().add(shortFilm);
		tag2.getMovies().add(shortFilm2);

		tagRepository.save(tag2);

		Set<Tag> tags = new HashSet<Tag>();
		tags.add(tag);
		tags.add(tag2);
		
		SearchData searchData = new SearchData();
		searchData.setQ("film");
		searchData.setTags(tags);
		searchData.setDateFilter(DateFilter.TODAY);
		searchData.setSortType(SortType.UPLOAD_DATE);
		searchData.setPagination(Pagination.empty());
		try {
			searchData.getPagination().setCurrentPage(1);

			System.out.println("PAGE 1:");
			for(ShortFilm s: shortFilmService.searchShortFilms(searchData).getContent()) {
				System.out.println(s.toString()+s.getTags().toString());
			}
			searchData.getPagination().setCurrentPage(2);
			System.out.println("PAGE 2:");

			for(ShortFilm s: shortFilmService.searchShortFilms(searchData).getContent()) {
				System.out.println(s.toString()+s.getTags().toString());
			}

		} catch (BadRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
