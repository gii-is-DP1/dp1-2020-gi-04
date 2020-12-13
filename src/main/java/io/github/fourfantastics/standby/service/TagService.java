package io.github.fourfantastics.standby.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Tag;
import io.github.fourfantastics.standby.repository.TagRepository;

@Service
public class TagService {
	@Autowired
	TagRepository tagRepository;

	public Optional<Tag> getTagById(Long id) {
		return tagRepository.findById(id);
	}

	public void saveTag(Tag tag) {
		tagRepository.save(tag);
	}
}
