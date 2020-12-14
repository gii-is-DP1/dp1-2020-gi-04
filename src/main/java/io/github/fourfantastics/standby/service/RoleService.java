package io.github.fourfantastics.standby.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.fourfantastics.standby.model.Role;
import io.github.fourfantastics.standby.repository.RoleRepository;

@Service
public class RoleService {
	@Autowired
	RoleRepository roleRepository;

	public Optional<Role> getRoleById(Long id) {
		return roleRepository.findById(id);
	}

	public void saveRole(Role role) {
		roleRepository.save(role);
	}

	public Set<Role> getAllRoles() {
		Set<Role> roles = new HashSet<>();
		Iterator<Role> iterator = roleRepository.findAll().iterator();
		while (iterator.hasNext()) {
			roles.add(iterator.next());
		}
		return roles;
	}

	public void deleteRole(Role role) {
		roleRepository.delete(role);
	}
}
