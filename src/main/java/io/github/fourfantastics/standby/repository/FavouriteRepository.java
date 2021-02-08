package io.github.fourfantastics.standby.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import io.github.fourfantastics.standby.model.Favourite;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;

public interface FavouriteRepository extends CrudRepository<Favourite, Long> {
	public Optional<Favourite> findByUserAndFavouriteShortfilm(User user, ShortFilm favouriteShortfilm);

	public Page<Favourite> findFavouriteShortfilmByUser(User user, Pageable pageable);
}
