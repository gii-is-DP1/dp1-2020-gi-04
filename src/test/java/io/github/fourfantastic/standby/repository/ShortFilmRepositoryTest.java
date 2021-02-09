package io.github.fourfantastic.standby.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.filters.ShortFilmSpecifications;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.NotificationConfiguration;
import io.github.fourfantastics.standby.model.Role;
import io.github.fourfantastics.standby.model.RoleType;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.Subscription;
import io.github.fourfantastics.standby.model.Tag;
import io.github.fourfantastics.standby.model.form.Pagination;
import io.github.fourfantastics.standby.repository.FilmmakerRepository;
import io.github.fourfantastics.standby.repository.NotificationConfigurationRepository;
import io.github.fourfantastics.standby.repository.RoleRepository;
import io.github.fourfantastics.standby.repository.ShortFilmRepository;
import io.github.fourfantastics.standby.repository.SubscriptionRepository;
import io.github.fourfantastics.standby.repository.TagRepository;
import io.github.fourfantastics.standby.service.RatingService;
import io.github.fourfantastics.standby.service.ShortFilmService;

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

	@Autowired
	RoleRepository rolesRepository;

	@Autowired
	NotificationConfigurationRepository notificationConfigurationRepository;

	@Autowired
	SubscriptionRepository subscriptionRepository;
	
	@Autowired
	ShortFilmSpecifications shortFilmSpecifications;

	@Test
	void countAttachedShortFilmByFilmmakerTest() {
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

		ShortFilm shortFilm = new ShortFilm();
		shortFilm.setTitle("Test film");
		shortFilm.setVideoUrl("example.mp4");
		shortFilm.setUploadDate(8L);
		shortFilm.setDescription("");
		shortFilm.setUploader(filmmaker);
		shortFilm.setViewCount(3L);
		shortFilmRepository.save(shortFilm);

		Role role = new Role();
		role.setFilmmaker(filmmaker);
		role.setRole(RoleType.ACTOR);
		role.setShortfilm(shortFilm);
		rolesRepository.save(role);

		assertEquals(shortFilmRepository.countAttachedShortFilmByFilmmaker(filmmaker.getId()), 1);
	}

	@Test
	void getAttachedShortFilmByFilmmakerTest() {
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

		ShortFilm shortFilm = new ShortFilm();
		shortFilm.setTitle("Test film");
		shortFilm.setVideoUrl("example.mp4");
		shortFilm.setUploadDate(8L);
		shortFilm.setDescription("");
		shortFilm.setUploader(filmmaker);
		shortFilm.setViewCount(3L);
		shortFilmRepository.save(shortFilm);

		Role role = new Role();
		role.setFilmmaker(filmmaker);
		role.setRole(RoleType.ACTOR);
		role.setShortfilm(shortFilm);
		rolesRepository.save(role);

		List<ShortFilm> resultExpected = new ArrayList<ShortFilm>();
		resultExpected.add(shortFilmRepository.findById(shortFilm.getId()).get());

		assertEquals(shortFilmRepository
				.findAttachedShortFilmByFilmmaker(filmmaker.getId(), Pagination.empty().getPageRequest()).getContent(),
				resultExpected);
	}

	@Test
	void countFollowedShortFilmsTest() {
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
		filmmaker2.setFullname("Filmmaker Díaz García 2");
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

		Subscription subscription = new Subscription();
		subscription.setFilmmaker(filmmaker);
		subscription.setSubscriber(filmmaker2);
		subscriptionRepository.save(subscription);

		assertEquals(shortFilmRepository.countFollowedShortFilms(filmmaker2.getId()), 1);
	}

	@Test
	void getFollowedShortFilmsTest() {
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
		filmmaker2.setFullname("Filmmaker Díaz García 2");
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

		Subscription subscription = new Subscription();
		subscription.setFilmmaker(filmmaker);
		subscription.setSubscriber(filmmaker2);
		subscriptionRepository.save(subscription);

		List<ShortFilm> shortFilms = new ArrayList<ShortFilm>();
		shortFilms.add(shortFilmRepository.findById(shortFilm.getId()).get());

		assertEquals(shortFilmRepository.followedShortFilms(filmmaker2.getId(), Pagination.empty().getPageRequest())
				.getContent(), shortFilms);
	}

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

		Specification<ShortFilm> thatHasTitle = shortFilmSpecifications.hasTitle("%film%");

		assertEquals(2, shortFilmRepository.findAll(thatHasTitle).size());
	}

	@Test
	void checkFilterShortFilmCaseInsensitive() {
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

		Specification<ShortFilm> thatHasTitle = shortFilmSpecifications.hasTitle("%test%");

		assertEquals(2, shortFilmRepository.findAll(thatHasTitle).size());
	}

	@Test
	void filterShortFilmByTag() {
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

		Tag comedy = new Tag();
		comedy.setName("comedy");
		comedy.getMovies().add(shortFilm);

		Tag action = new Tag();
		action.setName("action");
		action.getMovies().add(shortFilm2);

		tagRepository.save(comedy);
		tagRepository.save(action);

		Set<String> tags = new HashSet<String>();
		tags.add(comedy.getName());
		tags.add(action.getName());

		Specification<ShortFilm> withTags = shortFilmSpecifications.hasTags(tags);

		List<ShortFilm> shortFilms = shortFilmRepository.findAll(withTags);
		assertEquals(2, shortFilms.size());
		assertTrue(shortFilms.contains(shortFilm));
		assertTrue(shortFilms.contains(shortFilm2));
	}

	@Test
	void filterShortFilmByEmptyTags() {
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

		Tag comedy = new Tag();
		comedy.setName("comedy");
		comedy.getMovies().add(shortFilm);

		Tag action = new Tag();
		action.setName("action");
		action.getMovies().add(shortFilm2);

		tagRepository.save(comedy);
		tagRepository.save(action);

		Set<String> emptyTags = new HashSet<String>();

		Specification<ShortFilm> withTags = shortFilmSpecifications.hasTags(emptyTags);

		List<ShortFilm> shortFilms = shortFilmRepository.findAll(withTags);
		assertEquals(0, shortFilms.size());

	}

	@Test
	void filterShortFilmByUploadDate() {
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

		Specification<ShortFilm> between7and11 = shortFilmSpecifications.betweenDates(7L, 11L);

		List<ShortFilm> shortFilms = shortFilmRepository.findAll(between7and11);
		assertEquals(2, shortFilms.size());
		assertTrue(shortFilms.contains(shortFilm));
		assertTrue(shortFilms.contains(shortFilm2));

		Specification<ShortFilm> between7and9 = shortFilmSpecifications.betweenDates(7L, 9L);

		List<ShortFilm> shortFilms2 = shortFilmRepository.findAll(between7and9);
		assertEquals(1, shortFilms2.size());
		assertTrue(shortFilms2.contains(shortFilm));

		Specification<ShortFilm> between13and14 = shortFilmSpecifications.betweenDates(13L, 14L);

		List<ShortFilm> shortFilms3 = shortFilmRepository.findAll(between13and14);
		assertEquals(0, shortFilms3.size());
	}

	@Test
	void orderShortFilmByUploadDate() {
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
		shortFilm.setUploadDate(18L);
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

		Specification<ShortFilm> orderByUploadDateAsc = shortFilmSpecifications.sortByUploadDate(true);
		List<ShortFilm> shortfilmAsc = shortFilmRepository.findAll(orderByUploadDateAsc);

		assertEquals(3, shortfilmAsc.size());
		assertTrue(shortfilmAsc.get(0).equals(shortFilm2));
		assertTrue(shortfilmAsc.get(1).equals(shortFilm3));
		assertTrue(shortfilmAsc.get(2).equals(shortFilm));

		Specification<ShortFilm> orderByUploadDateDesc = shortFilmSpecifications.sortByUploadDate(false);
		List<ShortFilm> shortfilmDesc = shortFilmRepository.findAll(orderByUploadDateDesc);

		assertEquals(3, shortfilmDesc.size());
		assertTrue(shortfilmDesc.get(0).equals(shortFilm));
		assertTrue(shortfilmDesc.get(1).equals(shortFilm3));
		assertTrue(shortfilmDesc.get(2).equals(shortFilm2));

		Collections.reverse(shortfilmDesc);

		assertTrue(IntStream.iterate(0, x -> x + 1).limit(2)
				.allMatch(x -> shortfilmAsc.get(x).equals(shortfilmDesc.get(x))));

	}

	@Test
	void orderShortFilmByView() {
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
		shortFilm.setUploadDate(18L);
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
		shortFilm2.setViewCount(6L);
		shortFilmRepository.save(shortFilm2);

		ShortFilm shortFilm3 = new ShortFilm();
		shortFilm3.setTitle("test");
		shortFilm3.setVideoUrl("example.mp4");
		shortFilm3.setUploadDate(12L);
		shortFilm3.setDescription("");
		shortFilm3.setUploader(filmmaker2);
		shortFilm3.setViewCount(5L);
		shortFilmRepository.save(shortFilm3);

		Specification<ShortFilm> orderByViewsAsc = shortFilmSpecifications.sortByViews(true);
		List<ShortFilm> shortfilmAsc = shortFilmRepository.findAll(orderByViewsAsc);

		assertEquals(3, shortfilmAsc.size());
		assertTrue(shortfilmAsc.get(0).equals(shortFilm));
		assertTrue(shortfilmAsc.get(1).equals(shortFilm3));
		assertTrue(shortfilmAsc.get(2).equals(shortFilm2));

		Specification<ShortFilm> orderByViewsDesc = shortFilmSpecifications.sortByViews(false);
		List<ShortFilm> shortfilmDesc = shortFilmRepository.findAll(orderByViewsDesc);

		assertEquals(3, shortfilmDesc.size());
		assertTrue(shortfilmDesc.get(0).equals(shortFilm2));
		assertTrue(shortfilmDesc.get(1).equals(shortFilm3));
		assertTrue(shortfilmDesc.get(2).equals(shortFilm));

		Collections.reverse(shortfilmDesc);

		assertTrue(IntStream.iterate(0, x -> x + 1).limit(2)
				.allMatch(x -> shortfilmAsc.get(x).equals(shortfilmDesc.get(x))));

	}

	@Test
	void orderShortFilmByRating() {
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

		filmmaker = filmmakerRepository.save(filmmaker);

		NotificationConfiguration configuration = new NotificationConfiguration();
		configuration.setUser(filmmaker);

		configuration = notificationConfigurationRepository.save(configuration);
		
		filmmaker.setConfiguration(configuration);
		
		filmmaker = filmmakerRepository.save(filmmaker);

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
		shortFilm.setUploadDate(18L);
		shortFilm.setDescription("");
		shortFilm.setUploader(filmmaker);
		shortFilm.setViewCount(3L);
		shortFilmRepository.save(shortFilm);

		ShortFilm shortFilm2 = new ShortFilm();
		shortFilm2.setTitle("new film");
		shortFilm2.setVideoUrl("example.mp4");
		shortFilm2.setUploadDate(10L);
		shortFilm2.setDescription("");
		shortFilm2.setUploader(filmmaker);
		shortFilm2.setViewCount(6L);
		shortFilmRepository.save(shortFilm2);

		ShortFilm shortFilm3 = new ShortFilm();
		shortFilm3.setTitle("test");
		shortFilm3.setVideoUrl("example.mp4");
		shortFilm3.setUploadDate(12L);
		shortFilm3.setDescription("");
		shortFilm3.setUploader(filmmaker);
		shortFilm3.setViewCount(5L);
		shortFilmRepository.save(shortFilm3);

		ratingService.rateShortFilm(shortFilm3, filmmaker, 3);
		ratingService.rateShortFilm(shortFilm, filmmaker, 5);
		ratingService.rateShortFilm(shortFilm2, filmmaker, 7);

		Specification<ShortFilm> orderByRatingAsc = shortFilmSpecifications.sortByRating(true);
		List<ShortFilm> shortfilmAsc = shortFilmRepository.findAll(orderByRatingAsc);

		assertEquals(3, shortfilmAsc.size());
		assertTrue(shortfilmAsc.get(0).equals(shortFilm3));
		assertTrue(shortfilmAsc.get(1).equals(shortFilm));
		assertTrue(shortfilmAsc.get(2).equals(shortFilm2));

		Specification<ShortFilm> orderByRatingDesc = shortFilmSpecifications.sortByRating(false);
		List<ShortFilm> shortfilmDesc = shortFilmRepository.findAll(orderByRatingDesc);

		assertEquals(3, shortfilmDesc.size());
		assertTrue(shortfilmDesc.get(0).equals(shortFilm2));
		assertTrue(shortfilmDesc.get(1).equals(shortFilm));
		assertTrue(shortfilmDesc.get(2).equals(shortFilm3));

		Collections.reverse(shortfilmDesc);

		assertTrue(IntStream.iterate(0, x -> x + 1).limit(2)
				.allMatch(x -> shortfilmAsc.get(x).equals(shortfilmDesc.get(x))));

	}
}
