package io.github.fourfantastics.standby.model.form;

import java.util.ArrayList;
import java.util.List;

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
	Integer currentPage;
	Integer pageElements;
	Integer maxSidePages;
	Integer totalElements;

	public static Pagination of(Integer currentPage, Integer pageElements, Integer maxSidePages, Integer totalElements) {
		return new Pagination(currentPage, pageElements, maxSidePages, totalElements);
	}
	
	public static Pagination of(Integer pageElements, Integer maxSidePages, Integer totalElements) {
		return new Pagination(1, pageElements, maxSidePages, totalElements);
	}
	
	public static Pagination of(Integer maxSidePages, Integer totalElements) {
		return Pagination.of(5, maxSidePages, totalElements);
	}
	
	public static Pagination of(Integer totalElements) {
		return Pagination.of(2, totalElements);
	}
	
	public static Pagination empty() {
		return Pagination.of(0);
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
		Integer totalElements = getTotalElements();
		Integer pageElements = getPageElements();
		return (totalElements > 0)
				? totalElements / pageElements + ((totalElements % pageElements == 0) ? 0 : 1)
				: 1;
	}
	
	public Boolean isPreviousDisabled() {
		return getCurrentPage().compareTo(1) == 0;
	}

	public Boolean isNextDisabled() {
		return getCurrentPage().compareTo(getTotalPages()) == 0;
	}
	
	public List<Integer> getPages() {
		Integer currentPage = getCurrentPage();
		Integer maxSidePages = getMaxSidePages();
		Integer totalPages = getTotalPages();
		List<Integer> pages = new ArrayList<Integer>();
		
		for (Integer page = currentPage - 1; page >= currentPage - maxSidePages && page >= 1; page--) {
			pages.add(page);
		}
		
		pages.add(currentPage);
		
		for (Integer page = currentPage + 1; page <= currentPage + maxSidePages && page <= totalPages; page++) {
			pages.add(page);
		}
		
		return pages;
	}
	
	public Boolean isIndexVisible(Integer index) {
		Integer currentPage = getCurrentPage();
		Integer pageElements = getPageElements();
		return index >= (currentPage - 1) * pageElements && index < currentPage * pageElements; 
	}
	
	public Boolean isCountVisible(Integer count) {
		return isIndexVisible(count - 1);
	}
}
