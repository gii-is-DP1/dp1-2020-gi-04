package io.github.fourfantastics.standby.model.form;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

	Set<RoleData> roles;
	
	String newTagName;

	public static ShortFilmEditData fromShortFilm(ShortFilm shortFilm) {
		ShortFilmEditData shortFilmEditData = new ShortFilmEditData();
		shortFilmEditData.setTitle(shortFilm.getTitle());
		shortFilmEditData.setDescription(shortFilm.getDescription());
		shortFilmEditData.setRoles(shortFilm.getRoles().stream().map(RoleData::fromRole).collect(Collectors.toSet()));
		shortFilmEditData.setTags(shortFilm.getTags().stream().map(Tag::getTagname).collect(Collectors.toList()));
		shortFilmEditData.setNewTagName("");
		return shortFilmEditData;
	}
}