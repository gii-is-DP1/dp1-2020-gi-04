package io.github.fourfantastics.standby.model.form;


import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShortFilmUploadData {

	String title;
	
	String description;
	
	MultipartFile file;
	
	
}
