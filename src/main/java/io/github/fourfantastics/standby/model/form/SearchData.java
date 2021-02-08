package io.github.fourfantastics.standby.model.form;

import java.util.Set;

import io.github.fourfantastics.standby.model.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchData {
	String q;
	
	Set<Tag> tags;
	
	DateFilter dateFilter;
	
	SortType sortType;
	
	Pagination pagination = Pagination.empty();
}
