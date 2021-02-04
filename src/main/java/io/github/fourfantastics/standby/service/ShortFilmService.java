package io.github.fourfantastics.standby.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.Role;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.form.ShortFilmUploadData;
import io.github.fourfantastics.standby.repository.FileRepository;
import io.github.fourfantastics.standby.repository.ShortFilmRepository;
import io.github.fourfantastics.standby.service.exception.InvalidExtensionException;
import io.github.fourfantastics.standby.service.exception.TooBigException;
import io.github.fourfantastics.standby.utils.Utils;

@Service
public class ShortFilmService {
	final Path fileRoot = Paths.get("uploads");
	final Set<String> allowedVideoFileExtensions = Utils.hashSet(".mp4", ".webm");
	final Set<String> allowedImageFileExtensions = Utils.hashSet(".bmp", ".png", ".jpg", ".jpeg", ".webp");

	ShortFilmRepository shortFilmRepository;
	FileRepository fileRepository;

	@Autowired
	public ShortFilmService(ShortFilmRepository shortFilmRepository, FileRepository fileRepository) {
		this.shortFilmRepository = shortFilmRepository;
		this.fileRepository = fileRepository;
	}

	public Optional<ShortFilm> getShortFilmById(Long id) {
		return shortFilmRepository.findById(id);
	}

	public ShortFilm save(ShortFilm shortFilm) {
		return shortFilmRepository.save(shortFilm);
	}

	public ShortFilm upload(ShortFilmUploadData shortFilmUploadData, Filmmaker uploader)
			throws InvalidExtensionException, TooBigException, RuntimeException {
		MultipartFile videoFile = shortFilmUploadData.getFile();

		String extension = fileRepository.getFileExtension(videoFile);
		if (!allowedVideoFileExtensions.contains(extension)) {
			throw new InvalidExtensionException("Invalid extension for the file");
		}

		long gigabyte = 1000L * 1000L * 1000L;
		if (videoFile.getSize() > gigabyte) {
			throw new TooBigException("Uploaded file is too big");
		}

		String shortFilmUuid = UUID.randomUUID().toString();
		String videoPath = String.format("%s%s", shortFilmUuid, extension);

		fileRepository.createDirectory(fileRoot);
		if (!fileRepository.saveFile(videoFile, fileRoot.resolve(videoPath))) {
			throw new RuntimeException("Couldn't upload file");
		}

		ShortFilm shortFilm = shortFilmUploadData.toShortFilm();
		shortFilm.setVideoUrl(videoPath.toString());
		shortFilm.setThumbnailUrl(null);
		shortFilm.setUploader(uploader);
		shortFilm.setUploadDate(new Date().getTime());
		return shortFilmRepository.save(shortFilm);
	}

	public void uploadThumbnail(ShortFilm shortFilm, MultipartFile thumbnailFile)
			throws TooBigException, InvalidExtensionException, RuntimeException {
		String extension = fileRepository.getFileExtension(thumbnailFile);
		if (!allowedImageFileExtensions.contains(extension)) {
			throw new InvalidExtensionException("Invalid extension for the thumbnail");
		}

		long gigabyte = 1000L * 1000L * 5L; /* 5MB */
		if (thumbnailFile.getSize() > gigabyte) {
			throw new TooBigException("Uploaded thumbnail is too big");
		}

		String shortFilmUuid = UUID.randomUUID().toString();
		String thumbnailPath = String.format("%s%s", shortFilmUuid, extension);

		fileRepository.createDirectory(fileRoot);
		if (!fileRepository.saveFile(thumbnailFile, fileRoot.resolve(thumbnailPath))) {
			throw new RuntimeException("Couldn't upload thumbnail");
		}

		shortFilm.setThumbnailUrl(thumbnailPath);
	}

	public void updateShortFilmMetadata(ShortFilm shortFilm, String title, String description) {
		shortFilm.setTitle(title);
		shortFilm.setDescription(description);
		shortFilmRepository.save(shortFilm);
	}

	public Set<ShortFilm> getShortFilmByFilmmaker(Filmmaker filmmaker) {
		Set<Role> roles = filmmaker.getParticipateAs();
		return roles.stream().map(x -> x.getShortfilm()).collect(Collectors.toSet());
	}

	public void updateViewCount(ShortFilm shortFilm, Integer sum) {
		shortFilm.setViewCount(shortFilm.getViewCount() + sum);
		shortFilmRepository.save(shortFilm);
	}

	public Integer getShortFilmsCountByUploader(Filmmaker uploader) {
		return shortFilmRepository.countByUploader(uploader);
	}

	public Page<ShortFilm> getShortFilmsByUploader(Filmmaker uploader, Pageable pageable) {
		return shortFilmRepository.findByUploader(uploader, pageable);
	}

	public Integer getShortFilmsCountAttachedShortFilmByFilmmaker(Long filmmakerID) {
		return shortFilmRepository.countAttachedShortFilmByFilmmaker(filmmakerID);
	}

	public Page<ShortFilm> getAttachedShortFilmByFilmmaker(Long filmmakerID, Pageable pageable) {
		return shortFilmRepository.findAttachedShortFilmByFilmmaker(filmmakerID, pageable);
	}

	public Page<ShortFilm> getFollowedShortFilms(Long userID, Pageable pageable) {
		return shortFilmRepository.followedShortFilms(userID, pageable);

	}
	
	public Integer getCountFavouriteShortFilmsByUser(User user) {
		List<ShortFilm> favouriteShortFilms= new ArrayList<>(user.getFavouriteShortFilms());
		return favouriteShortFilms.size();
	}
	
	public Page<ShortFilm> getFavouriteShortFilmsByUser(User user,Pageable pageable) {
		List<ShortFilm> favouriteShortFilms= new ArrayList<>(user.getFavouriteShortFilms());
		int start= (int) pageable.getOffset();
		int end = (start+pageable.getPageSize())>favouriteShortFilms.size()?favouriteShortFilms.size():(start+pageable.getPageSize());
		return new PageImpl<ShortFilm>(favouriteShortFilms.subList(start, end),pageable,favouriteShortFilms.size());
	}

}
