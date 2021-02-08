package io.github.fourfantastic.standby.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Favourite;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.repository.FavouriteRepository;
import io.github.fourfantastics.standby.repository.UserRepository;
import io.github.fourfantastics.standby.service.FavouriteService;
import io.github.fourfantastics.standby.service.exception.NotUniqueException;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class FavouriteServiceTest {
	FavouriteService favouriteService;
	
	@Mock
	FavouriteRepository favouriteRepository;
	
	@Mock
	UserRepository userRepository;
	
	@BeforeEach
	public void setup() throws NotUniqueException {
		favouriteService = new FavouriteService(favouriteRepository, userRepository);
	}
	
	@Test
	public void addfavouriteShortFilmTest() {
		final ShortFilm mockShortFilm = new ShortFilm();
		final User mockUser = new User();
		
		when(favouriteRepository.findByUserAndFavouriteShortfilm(mockUser, mockShortFilm)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			favouriteService.favouriteShortFilm(mockShortFilm, mockUser);	
		});
		
		verify(favouriteRepository, times(1)).findByUserAndFavouriteShortfilm(mockUser, mockShortFilm);
		verify(favouriteRepository, times(1)).save(any(Favourite.class));
		verifyNoMoreInteractions(favouriteRepository);
	}
	
	@Test
	public void addAlreadyExistancefavouriteShortFilmTest() {
		final ShortFilm mockShortFilm = new ShortFilm();
		final User mockUser = new User();
		
		when(favouriteRepository.findByUserAndFavouriteShortfilm(mockUser, mockShortFilm)).thenReturn(Optional.of(new Favourite()));
		
		assertDoesNotThrow(() -> {
			favouriteService.favouriteShortFilm(mockShortFilm, mockUser);	
		});
		
		verify(favouriteRepository, only()).findByUserAndFavouriteShortfilm(mockUser, mockShortFilm);
	}
	
	@Test
	public void deletefavouriteShortFilmTest() {
		final ShortFilm mockShortFilm = new ShortFilm();
		final User mockUser = new User();
		
		when(favouriteRepository.findByUserAndFavouriteShortfilm(mockUser, mockShortFilm)).thenReturn(Optional.of(new Favourite()));
		
		assertDoesNotThrow(() -> {
			favouriteService.removeFavouriteShortFilm(mockShortFilm, mockUser);	
		});
		
		verify(favouriteRepository, times(1)).findByUserAndFavouriteShortfilm(mockUser, mockShortFilm);
		verify(favouriteRepository, times(1)).delete(any(Favourite.class));
		verifyNoMoreInteractions(favouriteRepository);
	}
	
	@Test
	public void deleteAlreadyExistancefavouriteShortFilmTest() {
		final ShortFilm mockShortFilm = new ShortFilm();
		final User mockUser = new User();
		
		when(favouriteRepository.findByUserAndFavouriteShortfilm(mockUser, mockShortFilm)).thenReturn(Optional.empty());
		
		assertDoesNotThrow(() -> {
			favouriteService.removeFavouriteShortFilm(mockShortFilm, mockUser);	
		});
		
		verify(favouriteRepository, only()).findByUserAndFavouriteShortfilm(mockUser, mockShortFilm);
	}
	
}
