package io.github.fourfantastics.standby.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

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
	
	public Optional<Tag> getTagById(Long id) {
		return tagRepository.findById(id);
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
			saveTag(tag);
		}
	}
	
	public Set<Tag> getAllTags() {
		Set<Tag> tags = new HashSet<>();
		Iterator<Tag> iterator = tagRepository.findAll().iterator();
		while (iterator.hasNext()) {
			tags.add(iterator.next());
		}
		return tags;
	}

	public void saveTag(Tag tag) {
		tagRepository.save(tag);
	}
}
