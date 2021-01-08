package io.github.fourfantastics.standby.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import io.github.fourfantastics.standby.utils.VideoUtils;

@Service
public class ShortFilmService {
	final Path fileRoot = Paths.get("uploads");
	final Set<String> allowedFileExtensions = Utils.hashSet(".mp4", ".avi", ".wmv", ".webm");

	ShortFilmRepository shortFilmRepository;
	FileRepository fileRepository;
	VideoUtils videoUtils;

	@Autowired
	public ShortFilmService(ShortFilmRepository shortFilmRepository, FileRepository fileRepository,
			VideoUtils videoUtils) {
		this.shortFilmRepository = shortFilmRepository;
		this.fileRepository = fileRepository;
		this.videoUtils = videoUtils;
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
		MultipartFile videoFile = shortFilmUploadData.getFile();

		String extension = fileRepository.getFileExtension(videoFile);
		if (!allowedFileExtensions.contains(extension)) {
			throw new InvalidExtensionException("Invalid extension for the file");
		}

		long gigabyte = 1000L * 1000L * 1000L;
		if (videoFile.getSize() > gigabyte) {
			throw new TooBigException("Uploaded file is too big");
		}

		String shortFilmUuid = UUID.randomUUID().toString();
		String videoPath = String.format("%s%s", shortFilmUuid, extension);
		String thumbnailPath = String.format("%s%s", shortFilmUuid, ".png");

		fileRepository.createDirectory(fileRoot);
		if (!fileRepository.saveFile(videoFile, fileRoot.resolve(videoPath))) {
			throw new RuntimeException("Couldn't upload file");
		}

		ByteArrayOutputStream thumbnailImage = videoUtils.getThumbnailFromVideo(videoFile);
		if (thumbnailImage == null) {
			System.out.println("Image null");
			thumbnailPath = null;
		} else {
			if (!fileRepository.saveFile(thumbnailImage, fileRoot.resolve(thumbnailPath))) {
				System.out.println("Not saved");
				thumbnailPath = null;
			}
			try {
				thumbnailImage.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		ShortFilm shortFilm = shortFilmUploadData.toShortFilm();
		shortFilm.setVideoUrl(videoPath);
		shortFilm.setThumbnailUrl(thumbnailPath);
		shortFilm.setUploadDate(new Date().getTime());
		shortFilm.setUploader(uploader);
		return shortFilmRepository.save(shortFilm);
	}

	public Set<ShortFilm> getShortFilmByFilmmaker(Filmmaker filmmaker) {
		Set<Role> roles = filmmaker.getParticipateAs();
		return roles.stream().map(x -> x.getShortfilm()).collect(Collectors.toSet());
	}
}
