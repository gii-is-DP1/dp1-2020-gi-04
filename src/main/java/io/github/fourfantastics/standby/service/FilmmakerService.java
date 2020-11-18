package io.github.fourfantastics.standby.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.repository.FilmmakerRepository;

public class FilmmakerService {

	@Autowired
	FilmmakerRepository filmmakerRepository;

	public Optional<Filmmaker> getFilmmmakerById(Long id) {
		return filmmakerRepository.findById(id);
	}

	public void saveFilmmaker(Filmmaker filmmaker) {
		filmmakerRepository.save(filmmaker);
	}

}
