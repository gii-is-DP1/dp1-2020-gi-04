package io.github.fourfantastic.standby.web;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.web.FilmmakerController;

@ActiveProfiles("test")
@ContextConfiguration(classes = StandbyApplication.class)
@WebMvcTest(controllers = FilmmakerController.class)
public class FilmmakerControllerTest {


}
