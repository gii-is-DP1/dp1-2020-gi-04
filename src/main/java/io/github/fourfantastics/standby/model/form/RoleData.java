package io.github.fourfantastics.standby.model.form;

import io.github.fourfantastics.standby.model.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleData {
	public String filmmakerName;
	
	public RoleType roleType;
	
	public static RoleData of(String filmmakerName, RoleType roleType) {
		return new RoleData(filmmakerName, roleType);
	}
}
