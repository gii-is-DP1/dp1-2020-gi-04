package io.github.fourfantastics.standby.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import io.github.fourfantastics.standby.repository.ShortFilmRepository;
import io.github.fourfantastics.standby.repository.TagRepository;
import io.github.fourfantastics.standby.service.ShortFilmService;

@SpringBootTest
public class TagsIntegrationTests {

	@Autowired
	TagRepository tagRepository;

	@Autowired
	ShortFilmService shortFilmService;

	@Test
	void createShortFilmTagRelation() {
		Tag action = new Tag();
		action.setTagname("action");
		Tag thriller = new Tag();
		thriller.setTagname("thriller");
		Tag comedy = new Tag();
		comedy.setTagname("comedy");

		action = tagRepository.save(action);
		thriller = tagRepository.save(thriller);
		comedy = tagRepository.save(comedy);

		assertNotNull(action.getId());
		assertNotNull(thriller.getId());
		assertNotNull(comedy.getId());

		Set<Tag> tags = new HashSet<Tag>();
		tags.add(action);
		tags.add(comedy);

		ShortFilm shortFilm = new ShortFilm();
		shortFilm.setName("A new awesome video");
		shortFilm.setFileUrl("file://video.mp4");
		shortFilm.setUploadDate(1L);
		shortFilm.setDescription("Description");
		shortFilm.setTags(tags);

		ShortFilm shortFilm2 = new ShortFilm();
		shortFilm2.setName("Second Video");
		shortFilm2.setFileUrl("file://video.mp4");
		shortFilm2.setUploadDate(1L);
		shortFilm2.setDescription("Description");
		Set<Tag> tags2 = new HashSet<Tag>();
		tags2.add(comedy);
		shortFilm2.setTags(tags2);
		shortFilmService.save(shortFilm2);
		shortFilm = shortFilmService.save(shortFilm);

		assertNotNull(shortFilm.getId());

		ShortFilm queriedShortFilm = shortFilmService.getShortFilmById(shortFilm.getId()).orElse(null);

		assertNotNull(queriedShortFilm);
		Set<Tag> shortFilmTags = queriedShortFilm.getTags();
		assertNotNull(shortFilmTags);
		assertEquals(2, shortFilmTags.size());
		comedy = tagRepository.findById(comedy.getId()).get();
		assertEquals(2, comedy.getMovies().size());

		queriedShortFilm.getTags().remove(comedy);
		queriedShortFilm = shortFilmService.save(queriedShortFilm);

		comedy = tagRepository.findById(comedy.getId()).get();
		assertEquals(1, comedy.getMovies().size());

	}
}
