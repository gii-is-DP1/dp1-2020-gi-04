package io.github.fourfantastics.standby.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.Role;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.form.ShortFilmUploadData;
import io.github.fourfantastics.standby.repository.FileRepository;
import io.github.fourfantastics.standby.repository.ShortFilmRepository;
import io.github.fourfantastics.standby.service.exception.InvalidExtensionException;
import io.github.fourfantastics.standby.service.exception.TooBigException;
import io.github.fourfantastics.standby.utils.Utils;

@Service
public class ShortFilmService {
	final Path fileRoot = Paths.get("uploads");
	final Set<String> allowedFileExtensions = Utils.hashSet(".mp4", ".avi", ".wmv", ".webm");
	
	ShortFilmRepository shortFilmRepository;
	FileRepository fileRepository;

	@Autowired
	public ShortFilmService(ShortFilmRepository shortFilmRepository, FileRepository fileRepository) {
		this.shortFilmRepository = shortFilmRepository;
		this.fileRepository = fileRepository;
	}
	
	public boolean init() {
		return fileRepository.createDirectory(fileRoot);
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

	public ShortFilm upload(ShortFilmUploadData shortFilmUploadData, Filmmaker uploader)
			throws InvalidExtensionException, TooBigException, RuntimeException {
		MultipartFile file = shortFilmUploadData.getFile();
		
		String extension = fileRepository.getFileExtension(file);
		if (!allowedFileExtensions.contains(extension)) {
			throw new InvalidExtensionException("Invalid extension for the file");
		}
		
		long gigabyte = 1000L * 1000L * 1000L;
		if (file.getSize() > gigabyte) {
			throw new TooBigException("Uploaded file is too big");
		}
		
		String filePath = UUID.randomUUID().toString() + extension;
		if (!fileRepository.saveFile(file, fileRoot.resolve(filePath))) {
			throw new RuntimeException("Couldn't upload file");
		}
		
		ShortFilm shortFilm = shortFilmUploadData.toShortFilm();
		shortFilm.setFileUrl(filePath);
		shortFilm.setUploadDate(new Date().getTime());
		shortFilm.setUploader(uploader);
		return shortFilmRepository.save(shortFilm);
	}
	
	public Set<ShortFilm> getShortFilmbyFilmmaker(Filmmaker filmmaker){
		Set<Role> roles = filmmaker.getParticipateAs();
		return roles.stream().map(x -> x.getShortfilm()).collect(Collectors.toSet());
	}
}
