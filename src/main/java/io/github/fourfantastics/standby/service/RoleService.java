package io.github.fourfantastics.standby.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Filmmaker;
import io.github.fourfantastics.standby.model.Role;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.User;
import io.github.fourfantastics.standby.model.UserType;
import io.github.fourfantastics.standby.model.form.RoleData;
import io.github.fourfantastics.standby.repository.RoleRepository;

@Service
public class RoleService {
	RoleRepository roleRepository;
	UserService userService;
	
	@Autowired
	public RoleService(RoleRepository roleRepository, UserService userService) {
		this.roleRepository = roleRepository;
		this.userService = userService;
	}
	
	public void setRolesOfShortFilm(Collection<RoleData> roles, ShortFilm shortFilm) {
		for (Role role : shortFilm.getRoles()) {
			roleRepository.delete(role);
		}
		shortFilm.getRoles().clear();
		
		for (RoleData roleData : roles) {
			String filmmakerName = roleData.getFilmmakerName();
			if (filmmakerName == null || filmmakerName.chars().allMatch(Character::isWhitespace)) {
				continue;
			}

			User roleUser = userService.getUserByName(filmmakerName).orElse(null);
			if (roleUser != null && roleUser.getType().equals(UserType.Filmmaker)) {
				Role newRole = new Role();
				newRole.setFilmmaker((Filmmaker) roleUser);
				newRole.setRole(roleData.getRoleType());
				newRole.setShortfilm(shortFilm);
				roleRepository.save(newRole);
			}
		}
	}
}
