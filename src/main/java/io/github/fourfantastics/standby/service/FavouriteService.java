package io.github.fourfantastics.standby.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Favourite;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.FavouriteRepository;
import io.github.fourfantastics.standby.repository.UserRepository;

@Service
public class FavouriteService {
	
	FavouriteRepository favouriteRepository;
	UserRepository userRepository;
	
	@Autowired
	public FavouriteService(FavouriteRepository favouriteRepository, UserRepository userRepository) {
		this.favouriteRepository = favouriteRepository;
		this.userRepository = userRepository;
	}
	
	public void favouriteShortFilm(ShortFilm shortFilm, User user) {
		if (favouriteRepository.findByUserAndFavouriteShortfilm(user, shortFilm).isPresent()) {
			return;
		}
		
		Favourite favourite= new Favourite();
		favourite.setFavouriteShortfilm(shortFilm);
		favourite.setUser(user);
		favouriteRepository.save(favourite);
	}
	
	public void removeFavouriteShortFilm(ShortFilm shortFilm, User user) {
		Favourite favourite =favouriteRepository.findByUserAndFavouriteShortfilm(user, shortFilm).orElse(null);
		if (favourite==null) {
			return;
		}
		
		favouriteRepository.delete(favourite);
	}
	
	public Boolean hasFavouriteShortFilm(ShortFilm shortFilm, User user) {
		return favouriteRepository.findByUserAndFavouriteShortfilm(user, shortFilm).isPresent();
	}
	
	public Integer getFavouriteShortFilmsCount(User user) {
		return favouriteRepository.countByUser(user);
	}
	
	public Page<Favourite> getFavouriteShortFilmsByUser(User user,Pageable pageable) {
		return favouriteRepository.findFavouriteShortfilmByUser(user, pageable);
	}

}
