package io.github.fourfantastic.standby.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.multipart.MultipartFile;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.Tag;
import io.github.fourfantastics.standby.model.form.FilmmakerRegisterData;
import io.github.fourfantastics.standby.model.form.ShortFilmUploadData;
import io.github.fourfantastics.standby.service.FilmmakerService;
import io.github.fourfantastics.standby.service.ShortFilmService;
import io.github.fourfantastics.standby.service.exceptions.DataMismatchException;
import io.github.fourfantastics.standby.service.exceptions.InvalidExtensionException;
import io.github.fourfantastics.standby.service.exceptions.NotUniqueException;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(classes = StandbyApplication.class)
public class ShortFilmServiceTest {
	
	 @Autowired
		protected ShortFilmService shortFilmService;
	 @Autowired
		protected FilmmakerService filmmakerService;
	 
	 @Test
	 void getShortFilmByIdTest() throws DataMismatchException, NotUniqueException {
		FilmmakerRegisterData filmmakerRegisterData = new FilmmakerRegisterData();
		filmmakerRegisterData.setName("Filmmaker2");
		filmmakerRegisterData.setFullname("Filmmaker2 Surnam2e");
		filmmakerRegisterData.setCity("Seville");
		filmmakerRegisterData.setCountry("Spain");
		filmmakerRegisterData.setEmail("filmmaker2@gmail.com");
		filmmakerRegisterData.setPhone("678543877");
		filmmakerRegisterData.setPassword("patata");
		filmmakerRegisterData.setConfirmPassword("patata");
		Filmmaker filmmaker = this.filmmakerService.registerFilmmaker(filmmakerRegisterData );
		 

		 
		ShortFilm shortFilm = new ShortFilm();
		shortFilm.setName("Film name");
		shortFilm.setUploadDate(16-12-2020L);
		shortFilm.setFileUrl("url");
		shortFilm.setDescription("description");
		shortFilm.setUploader(filmmaker);
		shortFilmService.save(shortFilm);
		
		Optional<ShortFilm> shortFilm1 = this.shortFilmService.getShortFilmById(shortFilm.getId());
		assertThat(shortFilm1.isPresent()).isEqualTo(true);
		 
		Optional<ShortFilm> shortFilm2 = this.shortFilmService.getShortFilmById(84l);
		assertThat(shortFilm2.isPresent()).isEqualTo(false);
	 }
	 
	 @Test
	 void  getShortFilmTags() throws DataMismatchException, NotUniqueException {
		 
		FilmmakerRegisterData filmmakerRegisterData = new FilmmakerRegisterData();
		filmmakerRegisterData.setName("Filmmaker2");
		filmmakerRegisterData.setFullname("Filmmaker2 Surname2");
		filmmakerRegisterData.setCity("Seville");
		filmmakerRegisterData.setCountry("Spain");
		filmmakerRegisterData.setEmail("filmmaker2@gmail.com");
		filmmakerRegisterData.setPhone("678543877");
		filmmakerRegisterData.setPassword("patata");
		filmmakerRegisterData.setConfirmPassword("patata");
		Filmmaker filmmaker = this.filmmakerService.registerFilmmaker(filmmakerRegisterData );
			 

			 
		ShortFilm shortFilm = new ShortFilm();
		shortFilm.setName("Film name");
		shortFilm.setUploadDate(16-12-2020L);
		shortFilm.setFileUrl("url");
		shortFilm.setDescription("description");
		shortFilm.setUploader(filmmaker);
		
		Set<Tag> tags = this.shortFilmService.getShortFilmTags(shortFilm);
		assertThat(tags.isEmpty()).isEqualTo(true);
		
		
		/*
		Tag tag =new Tag();
		tag.setTagname("drama");
		Set<Tag> tags1 = new HashSet<Tag>();
		tags1.add(tag);

		ShortFilm shortFilm2 = new ShortFilm();
		shortFilm2.setName("Film name 2");
		shortFilm2.setUploadDate(16-12-2020L);
		shortFilm2.setFileUrl("url2");
		shortFilm2.setDescription("description2");
		shortFilm2.setTags(tags1);
		shortFilm2.setUploader(filmmaker);
		
		
		Set<ShortFilm> setShortFilms = new HashSet<>();
		setShortFilms.add(shortFilm2);
		tag.setMovies(setShortFilms);
		
		Set<Tag> tags2 = this.shortFilmService.getShortFilmTags(shortFilm2);
		assertThat(tags2.isEmpty()).isEqualTo(true);
		 
		*/
		
	 }
	 
	 /*
	 @Test
	 void uploadTest() throws DataMismatchException, NotUniqueException, IOException, InvalidExtensionException, RuntimeException{
		 
		FilmmakerRegisterData filmmakerRegisterData = new FilmmakerRegisterData();
		filmmakerRegisterData.setName("Filmmaker3");
		filmmakerRegisterData.setFullname("Filmmaker3 Surname3");
		filmmakerRegisterData.setCity("Seville");
		filmmakerRegisterData.setCountry("Spain");
		filmmakerRegisterData.setEmail("filmmaker3@gmail.com");
		filmmakerRegisterData.setPhone("678546879");
		filmmakerRegisterData.setPassword("patata");
		filmmakerRegisterData.setConfirmPassword("patata");
		Filmmaker filmmaker = this.filmmakerService.registerFilmmaker(filmmakerRegisterData);
		
		ShortFilmUploadData uploadData = new ShortFilmUploadData();
		MultipartFile file=(MultipartFile) File.createTempFile("example", ".mp4");
		
		uploadData.setTitle("Title");
		uploadData.setDescription("Description");
		uploadData.setFile(file);
		
		ShortFilm shortFilm= this.shortFilmService.upload(uploadData, filmmaker);
		assertThat(shortFilm.getName()).isEqualTo("Title");
		 
	 }
	 
	 */

}
