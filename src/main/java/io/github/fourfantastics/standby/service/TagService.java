package io.github.fourfantastics.standby.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.Tag;
import io.github.fourfantastics.standby.repository.TagRepository;

@Service
public class TagService {
	TagRepository tagRepository;

	@Autowired
	public TagService(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
	}
	
	public void tagShortFilm(Collection<String> tags, ShortFilm shortFilm) {
		shortFilm.getTags().clear();
		for (String tagName : tags) {
			if (tagName.isEmpty() || tagName.chars().allMatch(Character::isWhitespace)) {
				continue;
			}

			Tag tag = tagRepository.findByName(tagName).orElse(null);
			if (tag == null) {
				tag = new Tag();
				tag.setName(tagName);
			}
			tag.getMovies().add(shortFilm);
			tagRepository.save(tag);
		}
	}
}
