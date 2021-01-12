package io.github.fourfantastics.standby.service;

import java.util.Collection;
import java.util.Optional;

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

	public Optional<Tag> getTagByName(String name) {
		return tagRepository.findByName(name);
	}
	
	public void tagShortFilm(Collection<String> tags, ShortFilm shortFilm) {
		shortFilm.getTags().clear();
		for (String tagName : tags) {
			if (tagName == null) {
				continue;
			}

			Tag tag = getTagByName(tagName).orElse(null);
			if (tag == null) {
				tag = new Tag();
				tag.setName(tagName);
			}
			tag.getMovies().add(shortFilm);
			tagRepository.save(tag);
		}
	}
}
