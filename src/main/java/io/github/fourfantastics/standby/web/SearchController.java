package io.github.fourfantastics.standby.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.form.SearchData;
import io.github.fourfantastics.standby.service.FilmmakerService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.exception.BadRequestException;

@Controller
public class SearchController {
	
	@Autowired
	ShortFilmService shortFilmService;
	
	@Autowired
	FilmmakerService filmmakerService;

	@RequestMapping("/search")
	public String search(@ModelAttribute SearchData searchData, BindingResult result, Map<String, Object> model) {

		List<ShortFilm> shortFilms = new ArrayList<ShortFilm>();
		List<Filmmaker> filmmakers = new ArrayList<Filmmaker>();
		try {
			shortFilms = shortFilmService.searchShortFilms(searchData).getContent();
			filmmakers = filmmakerService.findFirst3ByNameLike(searchData.getQ());
		} catch (BadRequestException e) {
			return "redirect:/";
		}
		
		model.put("search", searchData);
		model.put("shortFilms", shortFilms);
		model.put("filmmakers", filmmakers);
		return "search";
	}
}
