package io.github.fourfantastics.standby.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

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

		List<Tag> tags = new ArrayList<Tag>();
		tags.add(action);
		tags.add(comedy);

		action = tagRepository.save(action);
		thriller = tagRepository.save(thriller);
		comedy = tagRepository.save(comedy);

		assertNotNull(action.getId());
		assertNotNull(thriller.getId());
		assertNotNull(comedy.getId());

		ShortFilm shortFilm = new ShortFilm();
		shortFilm.setName("A new awesome video");
		shortFilm.setFileUrl("file://video.mp4");
		shortFilm.setUploadDate(1L);
		shortFilm.setDescription("Description");
		shortFilm.setTags(tags);

		shortFilm = shortFilmService.save(shortFilm);

		assertNotNull(shortFilm.getId());

		ShortFilm queriedShortFilm = shortFilmService.getShortFilmById(shortFilm.getId()).orElse(null);

		assertNotNull(queriedShortFilm);
		List<Tag> shortFilmTags = shortFilmService.getShortFilmTags(queriedShortFilm);
		assertNotNull(shortFilmTags);
		assertEquals(2, shortFilmTags.size());
		System.out.println(shortFilmTags);

	}
}
