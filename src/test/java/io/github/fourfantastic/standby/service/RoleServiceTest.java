package io.github.fourfantastic.standby.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.fourfantastics.standby.StandbyApplication;
import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.Role;
import io.github.fourfantastics.standby.model.RoleType;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.form.RoleData;
import io.github.fourfantastics.standby.repository.RoleRepository;
import io.github.fourfantastics.standby.service.RoleService;
import io.github.fourfantastics.standby.service.UserService;

@ActiveProfiles("test")
@SpringBootTest(classes = StandbyApplication.class)
public class RoleServiceTest {
	RoleService roleService;

	@Mock
	RoleRepository roleRepository;

	@Mock
	UserService userService;

	@BeforeEach
	public void setup() {
		roleService = new RoleService(roleRepository, userService);
	}

	@Test
	public void setRolesOfShortFilmTest() {
		final String filmmakerName = "filmmaker";

		final Filmmaker filmmaker = new Filmmaker();
		filmmaker.setId(1L);
		filmmaker.setName(filmmakerName);

		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.getRoles().add(new Role());

		final List<RoleData> rolesData = new ArrayList<RoleData>();
		final RoleData roleData = new RoleData();
		roleData.setFilmmakerName(filmmakerName);
		roleData.setRoleType(RoleType.ACTOR);
		rolesData.add(roleData);
		final Role role = new Role();
		role.setFilmmaker(filmmaker);
		role.setRole(RoleType.ACTOR);
		role.setShortfilm(mockShortFilm);

		when(userService.getUserByName(filmmakerName)).thenReturn(Optional.of(filmmaker));

		assertDoesNotThrow(() -> {
			roleService.setRolesOfShortFilm(rolesData, mockShortFilm);
		});

		assertTrue(mockShortFilm.getRoles().isEmpty());

		verify(roleRepository, times(1)).delete(new Role());
		verify(userService, only()).getUserByName(filmmakerName);
		verify(roleRepository, times(1)).save(role);
		verifyNoMoreInteractions(roleRepository);
	}

	@Test
	public void setWhiteRolesOfShortFilmTest() {
		final String filmmakerName = "filmmaker";

		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.getRoles().add(new Role());

		final List<RoleData> rolesData = new ArrayList<RoleData>();
		final RoleData roleData = new RoleData();
		roleData.setFilmmakerName("     ");
		roleData.setRoleType(RoleType.ACTOR);
		rolesData.add(roleData);

		when(userService.getUserByName(filmmakerName)).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			roleService.setRolesOfShortFilm(rolesData, mockShortFilm);
		});

		assertTrue(mockShortFilm.getRoles().isEmpty());

		verify(roleRepository, only()).delete(new Role());
		verifyNoInteractions(userService);
	}
	
	@Test
	public void setInvalidRolesOfShortFilmTest() {
		final String filmmakerName = "filmmaker";

		final ShortFilm mockShortFilm = new ShortFilm();
		mockShortFilm.getRoles().add(new Role());

		final List<RoleData> rolesData = new ArrayList<RoleData>();
		final RoleData roleData = new RoleData();
		roleData.setFilmmakerName(filmmakerName);
		roleData.setRoleType(RoleType.ACTOR);
		rolesData.add(roleData);

		when(userService.getUserByName(filmmakerName)).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> {
			roleService.setRolesOfShortFilm(rolesData, mockShortFilm);
		});

		assertTrue(mockShortFilm.getRoles().isEmpty());

		verify(roleRepository, times(1)).delete(new Role());
		verify(userService, only()).getUserByName(filmmakerName);
	}
}
