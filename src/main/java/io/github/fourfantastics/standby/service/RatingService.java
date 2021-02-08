package io.github.fourfantastics.standby.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Notification;
import io.github.fourfantastics.standby.model.NotificationType;
import io.github.fourfantastics.standby.model.Rating;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.RatingRepository;

@Service
public class RatingService {
	RatingRepository ratingRepository;
	ShortFilmService shortFilmService;
	NotificationService notificationService;

	@Autowired
	public RatingService(RatingRepository ratingRepository, ShortFilmService shortFilmService,
			NotificationService notificationService) {
		this.ratingRepository = ratingRepository;
		this.shortFilmService = shortFilmService;
		this.notificationService = notificationService;
	}

	public Long getRatingCount(ShortFilm shortFilm) {
		return ratingRepository.countByShortFilm(shortFilm);
	}

	private Double getAverageRating(ShortFilm shortFilm) {
		Double rating = ratingRepository.averageShortFilmRating(shortFilm.getId());
		return rating == null ? 0 : rating;
	}

	public Rating rateShortFilm(ShortFilm shortFilm, User user, Integer rate) {
		Rating rating = ratingRepository.findByUserAndShortFilm(user, shortFilm).orElse(null);
		if (rating == null) {
			rating = new Rating();
			rating.setUser(user);
			rating.setShortFilm(shortFilm);
		}
		
		List<Notification> notification = shortFilm.getUploader().getNotifications().stream()
				.filter(x -> x.getText().contains(user.getName()) && x.getType().equals(NotificationType.RATING))
				.collect(Collectors.toList());
		if (!notification.isEmpty()) {
			notificationService.deleteNotification(notification.get(0));
		}
		
		rating.setDate(Instant.now().toEpochMilli());
		rating.setGrade(rate);

		Rating savedRating = ratingRepository.save(rating);

		Double avgRating = getAverageRating(shortFilm);
		shortFilm.setRatingAverage(avgRating);

		shortFilmService.save(shortFilm);

		if (shortFilm.getUploader().getConfiguration().getByRatings() && !user.equals(shortFilm.getUploader())) {
			notificationService.sendNotification(shortFilm.getUploader(), NotificationType.RATING,
					String.format("%s has rated your shortfilm '%s' with %d", user.getName(), shortFilm.getTitle(),
							rating.getGrade()));
		}

		return savedRating;
	}

	public Rating getRatingByUserAndShortFilm(User user, ShortFilm shortFilm) {
		return ratingRepository.findByUserAndShortFilm(user, shortFilm).orElse(null);
	}

	public void removeRating(User user, ShortFilm shortFilm) {
			List<Notification> notification = shortFilm.getUploader().getNotifications().stream()
					.filter(x -> x.getText().contains(user.getName()) && x.getType().equals(NotificationType.RATING))
					.collect(Collectors.toList());
			if (!notification.isEmpty()) {
				notificationService.deleteNotification(notification.get(0));
			}
			ratingRepository.deleteByUserAndShortFilm(user, shortFilm);
		}
	}

