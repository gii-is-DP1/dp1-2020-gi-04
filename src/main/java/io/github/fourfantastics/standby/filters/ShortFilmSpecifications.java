package io.github.fourfantastics.standby.filters;

import java.util.Set;

import javax.persistence.criteria.SetJoin;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.Tag;

@Component
public class ShortFilmSpecifications {
	public Specification<ShortFilm> hasTitle(String title) {
		return (root, query, cb) -> {
			return cb.like(cb.lower(root.get("title")), title.toLowerCase());
		};
	}

	public Specification<ShortFilm> hasTags(Set<String> tags) {
		return (root, query, cb) -> {
			SetJoin<ShortFilm, Tag> filmTags = root.joinSet("tags");
			query.distinct(true);
			return cb.in(filmTags.get("name")).value(tags);
		};
	}

	public Specification<ShortFilm> betweenDates(Long from, Long to) {
		return (root, query, cb) -> {
			return cb.and(cb.greaterThan(root.get("uploadDate"), from), cb.lessThan(root.get("uploadDate"), to));
		};
	}

	public Specification<ShortFilm> sortByViews(Boolean asc) {
		return (root, query, cb) -> {
			if (asc) {
				query.orderBy(cb.asc(root.get("viewCount")));
			} else {
				query.orderBy(cb.desc(root.get("viewCount")));
			}

			return cb.conjunction();
		};
	}

	public Specification<ShortFilm> sortByRating(Boolean asc) {
		return (root, query, cb) -> {
			if (asc) {
				query.orderBy(cb.asc(root.get("ratingAverage")));
			} else {
				query.orderBy(cb.desc(root.get("ratingAverage")));
			}

			return cb.conjunction();
		};
	}

	public Specification<ShortFilm> sortByUploadDate(Boolean asc) {
		return (root, query, cb) -> {
			if (asc) {
				query.orderBy(cb.asc(root.get("uploadDate")));
			} else {
				query.orderBy(cb.desc(root.get("uploadDate")));
			}

			return cb.conjunction();
		};
	}
}
