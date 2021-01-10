package io.github.fourfantastics.standby.model.form;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.github.fourfantastics.standby.model.Comment;
import io.github.fourfantastics.standby.model.Rating;
import io.github.fourfantastics.standby.model.ShortFilm;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShortFilmViewData {
	ShortFilm shortFilm;

	List<Comment> comments = new ArrayList<Comment>();

	Double meanRating;
	Double maxRating = 10.0;
	Long totalRatings;

	Long watcherId;
	String watcherName;
	String watcherPhotoUrl;

	Pagination commentPagination;

	String newCommentText;
	
	Rating userRating;

	public String getFormattedRating() {
		DecimalFormat df = new DecimalFormat("#.#");
		df.setRoundingMode(RoundingMode.HALF_EVEN);
		return df.format(meanRating);
	}
	
	public String getFormattedMaxRating() {
		DecimalFormat df = new DecimalFormat("#.#");
		df.setRoundingMode(RoundingMode.HALF_EVEN);
		return df.format(maxRating);
	}
}
