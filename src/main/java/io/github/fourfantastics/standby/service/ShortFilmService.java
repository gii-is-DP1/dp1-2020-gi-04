package io.github.fourfantastics.standby.service;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.Tag;
import io.github.fourfantastics.standby.model.form.ShortFilmUploadData;
import io.github.fourfantastics.standby.repository.ShortFilmRepository;
import io.github.fourfantastics.standby.service.exceptions.InvalidExtensionException;

@Service
public class ShortFilmService {
	ShortFilmRepository shortFilmRepository;
	FileService fileService;

	@Autowired
	public ShortFilmService(ShortFilmRepository shortFilmRepository, FileService fileService) {
		this.shortFilmRepository = shortFilmRepository;
		this.fileService = fileService;
	}
	
	public Optional<ShortFilm> getShortFilmById(Long id) {
		return shortFilmRepository.findById(id);
	}
	
	public Optional<ShortFilm> getShortFilmByTitle(String title) {
		return shortFilmRepository.findByTitle(title);
	}
	
	public ShortFilm save(ShortFilm shortFilm) {
		return shortFilmRepository.save(shortFilm);
	}

	public Set<ShortFilm> getAllShortFilms() {
		Set<ShortFilm> shortFilms = new HashSet<>();
		Iterator<ShortFilm> iterator = shortFilmRepository.findAll().iterator();
		while (iterator.hasNext()) {
			shortFilms.add(iterator.next());
		}
		return shortFilms;
	}

	public void deleteShortFilm(ShortFilm shortFilm) {
		shortFilmRepository.delete(shortFilm);
	}

	public Set<Tag> getShortFilmTags(ShortFilm shortFilm) {
		Long id = shortFilm.getId();
		return shortFilmRepository.findTagsByShortFilmId(id);
	}

	public ShortFilm upload(ShortFilmUploadData uploadData, Filmmaker uploader)
			throws InvalidExtensionException, RuntimeException {
		ShortFilm shortFilm = uploadData.toShortFilm();
		String path = fileService.save(uploadData.getFile());
		shortFilm.setFileUrl(path);
		shortFilm.setUploadDate(new Date().getTime());
		shortFilm.setUploader(uploader);
		shortFilmRepository.save(shortFilm);
		return shortFilm;
	}
}
