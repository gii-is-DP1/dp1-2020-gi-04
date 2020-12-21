package io.github.fourfantastics.standby.model.form;


import org.springframework.web.multipart.MultipartFile;

import io.github.fourfantastics.standby.model.ShortFilm;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShortFilmUploadData {
	String title;
	
	String description;
	
	MultipartFile file;
	
	public ShortFilm toShortFilm() {
		ShortFilm film = new ShortFilm();
		film.setTitle(this.getTitle());
		film.setDescription(this.getDescription());
		return film;
	}
}
