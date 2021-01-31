package io.github.fourfantastic.standby.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.Tag;
import io.github.fourfantastics.standby.repository.TagRepository;
import io.github.fourfantastics.standby.service.TagService;
import io.github.fourfantastics.standby.utils.Utils;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class TagServiceTest {
	TagService tagService;

	@Mock
	TagRepository tagRepository;

	@BeforeEach
	public void setup() {
		tagService = new TagService(tagRepository);
	}

	@Test
	public void setTagsOfShortFilmTest() {
		final String filmmakerName = "filmmaker";

		final Filmmaker filmmaker = new Filmmaker();
		filmmaker.setId(1L);
		filmmaker.setName(filmmakerName);

		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.getTags().add(new Tag());

		final Set<String> tagsName = Utils.hashSet("action", "scifi");

		assertDoesNotThrow(() -> {
			tagService.tagShortFilm(tagsName, mockShortFilm);
		});

		assertTrue(mockShortFilm.getTags().isEmpty());

		for (String tagName : tagsName) {
			verify(tagRepository, times(1)).findByName(tagName);
		}
		verify(tagRepository, times(2)).save(new Tag());
		verifyNoMoreInteractions(tagRepository);
	}
}
