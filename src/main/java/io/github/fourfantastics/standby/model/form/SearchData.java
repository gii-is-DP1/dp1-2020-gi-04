package io.github.fourfantastics.standby.model.form;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchData {
	String q;
	
	DateFilter dateFilter;
	
	SortType sortType;
	
	SortOrder sortOrder;
	
	Pagination pagination = Pagination.empty();
}
