package io.github.fourfantastics.standby.model.form;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.fourfantastics.standby.model.RoleType;
import io.github.fourfantastics.standby.model.ShortFilm;
import io.github.fourfantastics.standby.model.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShortFilmEditData {
	String title;

	String description;

	List<String> tags;

	Map<String, RoleType> roles;
	
	String newTagName;
	
	String newRoleFilmmaker;
	
	RoleType newRoleType;

	public static ShortFilmEditData fromShortFilm(ShortFilm shortFilm) {
		ShortFilmEditData shortFilmEditData = new ShortFilmEditData();
		shortFilmEditData.setTitle(shortFilm.getTitle());
		shortFilmEditData.setDescription(shortFilm.getDescription());
		shortFilmEditData.setRoles(shortFilm.getRoles().stream().collect(Collectors.toMap(x -> x.getFilmmaker().getName(), x -> x.getRole())));
		shortFilmEditData.setTags(shortFilm.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
		
		shortFilmEditData.setNewTagName("");
		shortFilmEditData.setNewRoleFilmmaker("");
		shortFilmEditData.setNewRoleType(RoleType.ACTOR);
		
		return shortFilmEditData;
	}
	
	public void copyToShortFilm(ShortFilm shortFilm) {
		shortFilm.setTitle(getTitle());
		shortFilm.setDescription(getDescription());
	}
}