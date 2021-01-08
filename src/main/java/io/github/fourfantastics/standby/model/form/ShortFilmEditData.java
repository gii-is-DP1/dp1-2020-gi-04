package io.github.fourfantastics.standby.model.form;

import java.util.ArrayList;
import java.util.List;
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

	List<String> tags = new ArrayList<String>();

	List<RoleData> roles = new ArrayList<RoleData>();
	
	Pagination rolePagination = Pagination.empty();
	
	String newTagName;
	
	String newRoleFilmmaker;
	
	RoleType newRoleType;

	public static ShortFilmEditData fromShortFilm(ShortFilm shortFilm) {
		ShortFilmEditData shortFilmEditData = new ShortFilmEditData();
		shortFilmEditData.setTitle(shortFilm.getTitle());
		shortFilmEditData.setDescription(shortFilm.getDescription());
		shortFilmEditData.setRoles(shortFilm.getRoles().stream().map(x -> RoleData.of(x.getFilmmaker().getName(), x.getRole())).collect(Collectors.toList()));
		shortFilmEditData.getRolePagination().setTotalElements(shortFilmEditData.getRoles().size());
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