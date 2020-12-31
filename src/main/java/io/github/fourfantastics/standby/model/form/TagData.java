package io.github.fourfantastics.standby.model.form;

import io.github.fourfantastics.standby.model.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TagData {

	Long id;

	String tagname;

	public static TagData fromTag(Tag tag) {
		TagData tagData = new TagData();
		tagData.setId(tag.getId());
		tagData.setTagname(tag.getTagname());
		return tagData;
	}
}
