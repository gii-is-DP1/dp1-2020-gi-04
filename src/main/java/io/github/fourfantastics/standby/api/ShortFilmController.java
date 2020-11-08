package io.github.fourfantastics.standby.api;

import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.fourfantastics.standby.exception.InvalidShortFilmException;
import io.github.fourfantastics.standby.exception.ShortFilmNotFoundException;
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
				.orElseThrow(() -> new ShortFilmNotFoundException(id));
	}
	
	@PostMapping("/api/films")
	public ShortFilm postShortFilm(@Valid @RequestBody ShortFilm newShortFilm, BindingResult binding) {
		if (binding.hasErrors()) {
			throw new InvalidShortFilmException();
		}
		
		newShortFilm.setId(null);
		shortFilmService.saveShortFilm(newShortFilm);
		return newShortFilm;
	}
	
	@PutMapping("/api/films/{id}")
	public ShortFilm putShortFilmById(@PathVariable Long id, @Valid @RequestBody ShortFilm modifiedShortFilm, BindingResult binding) {
		if (binding.hasErrors()) {
			System.out.println(binding.getFieldErrors());
			throw new InvalidShortFilmException();
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
				.orElseThrow(() -> new ShortFilmNotFoundException(id));
				
	}
	
	@DeleteMapping("/api/films/{id}")
	public ShortFilm deleteShortFilmById(@PathVariable Long id) {
		ShortFilm shortFilm = shortFilmService.getShortFilmById(id)
			.orElseThrow(() -> new ShortFilmNotFoundException(id));
		
		shortFilmService.deleteShortFilm(shortFilm);
		return shortFilm;
	}
}
