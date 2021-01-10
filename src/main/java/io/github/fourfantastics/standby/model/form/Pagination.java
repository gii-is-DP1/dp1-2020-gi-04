package io.github.fourfantastics.standby.model.form;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import io.github.fourfantastics.standby.utils.Utils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Setter
@EqualsAndHashCode
@ToString
public class Pagination {
	Integer totalElements;
	Integer pageElements;
	Integer currentPage;
	Integer maxSidePages;

	public static Pagination of(Integer totalElements, Integer pageElements, Integer currentPage,
			Integer maxSidePages) {
		return new Pagination(totalElements, pageElements, currentPage, maxSidePages);
	}

	public static Pagination of(Integer totalElements, Integer pageElements, Integer currentPage) {
		return new Pagination(totalElements, pageElements, currentPage, 3);
	}

	public static Pagination of(Integer totalElements, Integer pageElements) {
		return new Pagination(totalElements, pageElements, 1, 3);
	}

	public static Pagination of(Integer totalElements) {
		return new Pagination(totalElements, 5, 1, 3);
	}

	public static Pagination empty() {
		return new Pagination(0, 5, 1, 3);
	}

	public Integer getCurrentPage() {
		return Utils.ensureRange(currentPage, getTotalPages(), 1);
	}

	public Integer getPageElements() {
		return Utils.ensureMin(pageElements, 1);
	}

	public Integer getMaxSidePages() {
		return Utils.ensureMin(maxSidePages, 0);
	}

	public Integer getTotalElements() {
		return Utils.ensureMin(totalElements, 0);
	}

	public Integer getTotalPages() {
		int totalElements = getTotalElements();
		int pageElements = getPageElements();
		return (totalElements > 0) ? totalElements / pageElements + ((totalElements % pageElements == 0) ? 0 : 1) : 1;
	}

	public Boolean isPreviousDisabled() {
		return getCurrentPage().compareTo(1) == 0;
	}

	public Boolean isNextDisabled() {
		return getCurrentPage().compareTo(getTotalPages()) == 0;
	}

	public List<Integer> getPages() {
		int currentPage = getCurrentPage();
		int maxSidePages = getMaxSidePages();
		int totalPages = getTotalPages();
		List<Integer> pages = new ArrayList<Integer>();

		for (int page = currentPage - 1; page >= currentPage - maxSidePages && page >= 1; page--) {
			pages.add(page);
		}
		pages.add(currentPage);
		for (int page = currentPage + 1; page <= currentPage + maxSidePages && page <= totalPages; page++) {
			pages.add(page);
		}

		return pages;
	}

	public Boolean isIndexVisible(Integer index) {
		int currentPage = getCurrentPage();
		int pageElements = getPageElements();
		return index >= (currentPage - 1) * pageElements && index < currentPage * pageElements;
	}

	public Boolean isCountVisible(Integer count) {
		return isIndexVisible(count - 1);
	}

	public PageRequest getPageRequest() {
		return PageRequest.of(getCurrentPage() - 1, getPageElements());
	}

	public PageRequest getPageRequest(Sort sort) {
		return PageRequest.of(getCurrentPage() - 1, getPageElements(), sort);
	}
}
