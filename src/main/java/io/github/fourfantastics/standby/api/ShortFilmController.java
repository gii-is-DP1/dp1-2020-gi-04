package io.github.fourfantastics.standby.api;

import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.service.ShortFilmService;

@RestController
public class ShortFilmController {
	@Autowired
	ShortFilmService shortFilmService;
	
	@GetMapping("/api/films")
	public Set<ShortFilm> getAllShortFilms() {
		return shortFilmService.getAllShortFilms();
	}
	
	@GetMapping("/api/films/{id}")
	public ShortFilm getShortFilmById(@PathVariable Long id) {
		return shortFilmService.getShortFilmById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
	
	@PostMapping("/api/films")
	public ShortFilm postShortFilm(@Valid @RequestBody ShortFilm newShortFilm, BindingResult binding) {
		if (binding.hasErrors()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		shortFilmService.saveShortFilm(newShortFilm);
		return newShortFilm;
	}
	
	@PutMapping("/api/films/{id}")
	public ShortFilm putShortFilmById(@PathVariable Long id, @Valid @RequestBody ShortFilm modifiedShortFilm, BindingResult binding) {
		if (binding.hasErrors()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		return shortFilmService.getShortFilmById(id)
				.map(x -> {
					x.setDescription(modifiedShortFilm.getDescription());
					x.setDuration(modifiedShortFilm.getDuration());
					x.setName(modifiedShortFilm.getName());
					x.setUploadDate(modifiedShortFilm.getUploadDate());
					shortFilmService.saveShortFilm(x);
					return x;
				})
				.orElseGet(() -> {
					modifiedShortFilm.setId(id);
					shortFilmService.saveShortFilm(modifiedShortFilm);
					return modifiedShortFilm;
				});
				
	}
}
