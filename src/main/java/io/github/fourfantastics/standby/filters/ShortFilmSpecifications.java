package io.github.fourfantastics.standby.filters;

import java.util.Date;
import java.util.Set;

import javax.persistence.criteria.SetJoin;

import org.springframework.data.jpa.domain.Specification;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.Tag;
import io.github.fourfantastics.standby.model.Rating;

public class ShortFilmSpecifications {

	public static Specification<ShortFilm> hasTitle(String title) {
		return (root, query, cb) -> {
			return cb.like(root.get("title"), title);
		};
	}

	public static Specification<ShortFilm> hasUploader(Filmmaker filmmaker) {
		return (root, query, cb) -> {
			return cb.equal(root.get("uploader"), filmmaker);
		};
	}

	public static Specification<ShortFilm> hasTags(Set<String> tags) {
		return (root, query, cb) -> {
			SetJoin<ShortFilm, Tag> filmTags = root.joinSet("tags");
			query.distinct(true);
			return cb.in(filmTags.get("name")).value(tags);
		};
	}

	public static Specification<ShortFilm> byUploadDate(Long from, Long to) {
		return (root, query, cb) -> {
			return cb.and(cb.greaterThan(root.get("uploadDate"), from), cb.lessThan(root.get("uploadDate"), to));
		};
	}

	public static Specification<ShortFilm> sortByViews() {
		return (root, query, cb) -> {
			query.orderBy(cb.desc(root.get("viewCount")));
			return cb.conjunction();
		};
	}
	
	public static Specification<ShortFilm> sortByRating() {
		return (root, query, cb) -> {
			//TODO average rating and order by
			SetJoin<ShortFilm, Rating> filmRatings = root.joinSet("tags");
			query.distinct(true);
			return cb.conjunction();
		};
	}
}
