package io.github.fourfantastics.standby.model.form;

import java.util.Set;
import java.util.stream.Collectors;

import io.github.fourfantastics.standby.model.ShortFilm;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShortFilmEditData {

	String title;

	String description;

	Set<TagData> tags;

	Set<RoleData> roles;

	public static ShortFilmEditData fromShortFilm(ShortFilm shortFilm) {
		ShortFilmEditData shortFilmEditData = new ShortFilmEditData();
		shortFilmEditData.setTitle(shortFilm.getTitle());
		shortFilmEditData.setDescription(shortFilm.getDescription());
		shortFilmEditData.setRoles(shortFilm.getRoles().stream().map(RoleData::fromRole).collect(Collectors.toSet()));
		shortFilmEditData.setTags(shortFilm.getTags().stream().map(TagData::fromTag).collect(Collectors.toSet()));
		return shortFilmEditData;

	}

}