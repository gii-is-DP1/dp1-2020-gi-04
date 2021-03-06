package io.github.fourfantastics.standby.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.github.fourfantastics.standby.filters.ShortFilmSpecifications;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.form.DateFilter;
import io.github.fourfantastics.standby.model.form.Pagination;
import io.github.fourfantastics.standby.model.form.SearchData;
import io.github.fourfantastics.standby.model.form.ShortFilmUploadData;
import io.github.fourfantastics.standby.model.form.SortOrder;
import io.github.fourfantastics.standby.model.form.SortType;
import io.github.fourfantastics.standby.repository.FileRepository;
import io.github.fourfantastics.standby.repository.ShortFilmRepository;
import io.github.fourfantastics.standby.service.exception.BadRequestException;
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
	ShortFilmSpecifications shortFilmSpecifications;

	@Autowired
	public ShortFilmService(ShortFilmRepository shortFilmRepository, FileRepository fileRepository, ShortFilmSpecifications shortFilmSpecifications) {
		this.shortFilmRepository = shortFilmRepository;
		this.fileRepository = fileRepository;
		this.shortFilmSpecifications = shortFilmSpecifications;
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

	public Integer getAttachedShortFilmsCountByFilmmaker(Long filmmakerId) {
		return shortFilmRepository.countAttachedShortFilmByFilmmaker(filmmakerId);
	}

	public Page<ShortFilm> getAttachedShortFilmsByFilmmaker(Long filmmakerId, Pageable pageable) {
		return shortFilmRepository.findAttachedShortFilmByFilmmaker(filmmakerId, pageable);
	}

	public Integer getFollowedShortFilmsCount(Long userId) {
		return shortFilmRepository.countFollowedShortFilms(userId);
	}

	public Page<ShortFilm> getFollowedShortFilms(Long userId, Pageable pageable) {
		return shortFilmRepository.followedShortFilms(userId, pageable);
	}

	public Page<ShortFilm> searchShortFilms(SearchData searchData) throws BadRequestException {
		String q = searchData.getQ();
		if (q == null || q.chars().allMatch(Character::isWhitespace)) {
			throw new BadRequestException("Search query cannot be null");
		}
		DateFilter dateFilter = searchData.getDateFilter();
		SortOrder sortOrder = searchData.getSortOrder();
		SortType sortType = searchData.getSortType();
		Pagination pagination = searchData.getPagination();
		Specification<ShortFilm> filters = (!q.startsWith("#"))
				? shortFilmSpecifications.hasTitle("%" + q + "%") :
					shortFilmSpecifications.hasTags(Utils.hashSet(q.substring(1)));

		Long now = Instant.now().toEpochMilli();
		Long day = 24L * 60L * 60L * 1000L;

		if (dateFilter != null) {
			switch (dateFilter) {
			case TODAY:
				Specification<ShortFilm> fromToday = shortFilmSpecifications.betweenDates(now - day, now);
				filters = filters.and(fromToday);
				break;
			case WEEK:
				Long week = day * 7L;
				Specification<ShortFilm> fromWeek = shortFilmSpecifications.betweenDates(now - week, now);
				filters = filters.and(fromWeek);
				break;
			case MONTH:
				Long month = day * 30L;
				Specification<ShortFilm> fromMonth = shortFilmSpecifications.betweenDates(now - month, now);
				filters = filters.and(fromMonth);
				break;
			case YEAR:
				Long year = day * 365L;
				Specification<ShortFilm> fromYear = shortFilmSpecifications.betweenDates(now - year, now);
				filters = filters.and(fromYear);
				break;
			case ALL:
			default:
				Specification<ShortFilm> fromAllTime = shortFilmSpecifications.betweenDates(0L, now);
				filters = filters.and(fromAllTime);
				break;
			}
		} else {
			Specification<ShortFilm> fromAllTime = shortFilmSpecifications.betweenDates(0L, now);
			filters = filters.and(fromAllTime);
		}
		
		Boolean ascending = false;
		if (sortOrder != null && sortOrder.equals(SortOrder.ASCENDING)) {
			ascending = true;
		}
		
		if (sortType != null) {
			switch (sortType) {
			case RATINGS:
				Specification<ShortFilm> sortByRating = shortFilmSpecifications.sortByRating(ascending);
				filters = filters.and(sortByRating);
				break;
			case UPLOAD_DATE:
				Specification<ShortFilm> sortByUploadDate = shortFilmSpecifications.sortByUploadDate(ascending);
				filters = filters.and(sortByUploadDate);
				break;
			case VIEWS:
			default:
				Specification<ShortFilm> sortByViews = shortFilmSpecifications.sortByViews(ascending);
				filters = filters.and(sortByViews);
				break;
			}
		} else {
			Specification<ShortFilm> sortByUploadDate = shortFilmSpecifications.sortByUploadDate(ascending);
			filters = filters.and(sortByUploadDate);
		}
		
		pagination.setTotalElements((int) shortFilmRepository.count(filters));
		pagination.setPageElements(5);
		Pageable pageable = pagination.getPageRequest();
		return shortFilmRepository.findAll(filters, pageable);
	}
}
