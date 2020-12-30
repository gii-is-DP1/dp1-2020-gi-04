package io.github.fourfantastics.standby.model.form;

import io.github.fourfantastics.standby.model.Role;
import io.github.fourfantastics.standby.model.RoleType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleData {

	String filmmaker;

	RoleType role;

	public static RoleData fromRole(Role roleFilmmarker) {
		RoleData roleData = new RoleData();
		roleData.setFilmmaker(roleFilmmarker.getFilmmaker().getName());
		roleData.setRole(roleFilmmarker.getRole());
		return roleData;
	}
}
