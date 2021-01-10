package io.github.fourfantastics.standby.model.form;

import java.util.ArrayList;
import java.util.List;

import io.github.fourfantastics.standby.model.Comment;
import io.github.fourfantastics.standby.model.ShortFilm;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShortFilmViewData {
	ShortFilm shortFilm;
	
	List<Comment> comments = new ArrayList<Comment>();
	
	Long watcherId;
	String watcherName;
	String watcherPhotoUrl;
	
	Pagination commentPagination;
	
	String newCommentText;
}
